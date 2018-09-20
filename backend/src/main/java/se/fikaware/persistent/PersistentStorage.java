package se.fikaware.persistent;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersistentStorage {
    private static String persistentRoot = System.getenv().get("HOME") + "/pstorage";
    private final String storageName;
    private Map<Class, Collection<PersistentObject>> loadedObjects = new HashMap<>();

    public PersistentStorage(String storageName) {
        this.storageName = storageName;
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName().replace('$', '_') + ".csv");
    }

    private <T extends PersistentObject> T loadObjectFromCSV(Class<? extends PersistentObject> type, String csvLine) {
        try {

            String []csvData  = csvLine.split(";");

            PersistentReader reader = new PersistentReader() {
                int index = 0;

                @Override
                public String readString() {
                    return csvData[index++];
                }

                @Override
                public boolean readBoolean() {
                    return Boolean.parseBoolean(csvData[index++]);
                }

                @Override
                public int readInt() {
                    return Integer.parseInt(csvData[index++]);
                }
            };

            if (type.isMemberClass() && (type.getModifiers() & Modifier.STATIC) == 0) {
                throw new RuntimeException("Please make " + type.getSimpleName() + " a static class");
            }

            //noinspection unchecked
            return (T) type.getConstructor(PersistentStorage.class, PersistentReader.class).newInstance(this, reader);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create class for category.");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Please add a constructor that looks like this: public " + type.getSimpleName() + "(PersistentStorage s, PersistentReader r) { ... }");
        }
    }

    boolean insertObject(PersistentObject object) {
        Class type = object.getClass();
        Collection collection = loadedObjects.get(type);
        if (!collection.contains(object)) {
            //noinspection unchecked
            collection.add(object);

            try {
                SimplePersistentWriter writer = new SimplePersistentWriter();
                object.write(writer);
                writer.builder.append('\n');
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getCategoryPath(type).toFile(), true), StandardCharsets.UTF_8))
                        .append(writer.builder.toString()).flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not add object to persistent memory! Type: " + type.getSimpleName());
            }

            return true;
        } else {
            return false;
        }
    }

    class SimplePersistentWriter implements PersistentWriter {
        StringBuilder builder = new StringBuilder();
        @Override
        public void writeString(String value) {
            builder.append(value);
            builder.append(';');
        }

        @Override
        public void writeBoolean(boolean value) {
            builder.append(value ? "true;" : "false;");
        }

        @Override
        public void writeInt(int value) {
            builder.append(Integer.toString(value));
            builder.append(';');
        }

        public void writeNext() {
            builder.append('\n');
        }
    }

    private <T extends PersistentObject> Collection<T> loadObjectsFromFile(Class<T> type) {
        Path path = getCategoryPath(type);
        try {
            return Files.lines(getCategoryPath(type)).map(f -> this.<T>loadObjectFromCSV(type, f)).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find category: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends PersistentObject> Stream<T> getAll(Class<T> type) {
        //noinspection unchecked
        return (Stream<T>) loadedObjects.computeIfAbsent(type, this::loadObjectsFromFile).stream();
    }

    public void update(Class type) {
        SimplePersistentWriter writer = new SimplePersistentWriter();
        loadedObjects.computeIfAbsent(type, k -> new LinkedList<>()).forEach(o -> {
            o.write(writer);
            writer.builder.append('\n');
        });

        try {
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getCategoryPath(type).toFile(), false), StandardCharsets.UTF_8))
                    .append(writer.builder.toString()).flush();
        } catch (IOException e) {
            throw new RuntimeException("Could not update objects with type:  " + type.getSimpleName());
        }
    }
}
