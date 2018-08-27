package se.fikaware.sync;

import org.reflections.Reflections;
import se.fikaware.misc.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@EverythingIsNonnullByDefault
public class Syncer {
    private static class ParsedField {
        private final String name;
        private final Field field;

        ParsedField(String name, Field field) {
            this.name = name;
            this.field = field;
        }
    }

    private Map<Class, IObjectSyncer> objectSyncers;

    public Syncer() {
        objectSyncers = new HashMap<>();
        Reflections ref = new Reflections("se.fikaware");
        for (Class<?> classObject : ref.getTypesAnnotatedWith(Syncable.class)) {

            var syncer = classObject.getAnnotation(Syncable.class).syncer();

            if (syncer != UnsetObjectSyncer.class) {
                try {
                    objectSyncers.put(classObject, syncer.getConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {

                var parsedFields = new LinkedList<ParsedField>();

                Class cl = classObject;
                while (cl != null) {
                    var fields = cl.getDeclaredFields();
                    for (var field : fields) {
                        var nameAnnotation = field.getAnnotation(Name.class);
                        if (nameAnnotation != null) {
                            parsedFields.push(new ParsedField(nameAnnotation.value(), field));
                        }
                    }
                    cl = cl.getSuperclass();
                }

                objectSyncers.put(classObject, (o, i) -> {
                    i.writeMapBegin();
                    var iterator = parsedFields.iterator();
                    if (iterator.hasNext()) {
                        var parsedField = iterator.next();
                        i.writeMapKey(parsedField.name);
                        write(i, parsedField.field.get(o));
                        while (iterator.hasNext()) {
                            i.writeMapNext();
                            parsedField = iterator.next();
                            i.writeMapKey(parsedField.name);
                            write(i, parsedField.field.get(o));
                        }
                    }
                    i.writeMapEnd();
                });
            }
        }
        installPrimitives();
    }

    private void installPrimitives() {
        objectSyncers.put(int.class, (o, i) ->
            i.writeInteger((int)o)
        );
        objectSyncers.put(String.class, (o, i) ->
            i.writeString((String)o)
        );
        objectSyncers.put(Collection.class, (o, i) -> {
            i.writeArrayBegin();
            var iterator = ((Collection)o).iterator();
            if (iterator.hasNext()) {
                write(i, iterator.next());
                while(iterator.hasNext()) {
                    i.writeArrayNext();
                    write(i, iterator.next());
                }
            }
            i.writeArrayEnd();
        });
        objectSyncers.put(Map.class, (o, i) -> {
            i.writeMapBegin();
            @SuppressWarnings("unchecked")
            Iterator<Map.Entry> iterator = ((Map)o).entrySet().iterator();
            if (iterator.hasNext()) {
                var entry = iterator.next();
                i.writeMapKey(entry.getKey().toString());
                write(i, entry.getValue());
                while(iterator.hasNext()) {
                    i.writeMapNext();
                    entry = iterator.next();
                    i.writeMapKey(entry.getKey().toString());
                    write(i, entry.getValue());
                }
            }
            i.writeMapEnd();
        });
    }

    public void write(IWriter i, @Nullable Object o) throws IOException {
        if (o == null) {
            i.writeNull();
        } else {
            Class classObject = o.getClass();
            if (classObject != null) {
                if (classObject.isArray()) {
                    int length = Array.getLength(o) - 1;
                    i.writeArrayBegin();
                    for (int index = 0; index < length; index++) {
                        write(i, Array.get(o, index));
                        i.writeArrayNext();
                    }
                    if (length >= 0) {
                        write(i, Array.get(o, length));
                    }
                    i.writeArrayEnd();
                    return;
                } else {
                    do {
                        var objectSyncer = objectSyncers.get(classObject);
                        if (objectSyncer != null) {
                            try {
                                objectSyncer.write(o, i);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            return;
                        } else {
                            var interfaces = classObject.getInterfaces();
                            boolean foundInterface = false;
                            for (var interfaceClass : interfaces) {
                                if (objectSyncers.containsKey(interfaceClass)) {
                                    classObject = interfaceClass;
                                    foundInterface = true;
                                    break;
                                }
                            }
                            if (!foundInterface) {
                                classObject = classObject.getSuperclass();
                            }
                        }
                    } while (classObject != null);
                }
            }
            System.err.println("Unknown syncer for: " + o.getClass().getName());
        }
    }
}
