package se.fikaware.sync;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Syncer {
    private interface IObjectSyncer {
        void write(Object o, IWriter i) throws IllegalAccessException;
    }

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
            var parsedFields = new LinkedList<ParsedField>();

            var cl = classObject;
            while(cl != null) {
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
                for (var parsedField : parsedFields) {
                    i.writeKey(parsedField.name);
                    write(i, parsedField.field.get(o));
                }
                i.writeMapEnd();
            });
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
            for (Object object : (Collection)o) {
                write(i, object);
            }
            i.writeArrayEnd();
        });
    }

    public void write(IWriter i, Object o) {
        if (o == null) {
            i.writeNull();
        } else {
            var classObject = o.getClass();
            while (classObject != null) {
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
            }
            System.err.println("Unknown syncer for: " + o.getClass().getName());
        }
    }
}
