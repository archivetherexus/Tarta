package se.fikaware.persistent;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PersistentStorage implements SimpleStorage {
    private static String persistentRoot = System.getenv().get("HOME") + "/pstorage";
    private final String storageName;
    private Map<Class, Map<Object, PersistentObject>> loadedObjects = new HashMap<>();

    public PersistentStorage(String storageName) {
        this.storageName = storageName;
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName().replace('$', '_') + ".csv");
    }

    class SimplePersistentReader implements PersistentReader {
        int index = 0;
        Object key = null;
        final String []csvData;

        SimplePersistentReader(String []csvData) {
            this.csvData = csvData;
        }

        @Override
        public String readString() {
            String value = csvData[index++];
            if (key == null) {
                key = value;
            }
            return value;
        }

        @Override
        public boolean readBoolean() {
            boolean value = Boolean.parseBoolean(csvData[index++]);
            if (key == null) {
                key = value;
            }
            return value;
        }

        @Override
        public int readInt() {
            int value = Integer.parseInt(csvData[index++]);
            if (key == null) {
                key = value;
                return value;
            }
            return value;
        }
    }

    private <T extends PersistentObject> void loadObjectIntoMapFromCSV(Class<? extends PersistentObject> type, String csvLine, Map<Object, T> into) {
        try {
            SimplePersistentReader reader = new SimplePersistentReader(csvLine.split(";"));

            if (type.isMemberClass() && (type.getModifiers() & Modifier.STATIC) == 0) {
                throw new RuntimeException("Please make " + type.getSimpleName() + " a static class");
            }

            //noinspection unchecked
            T o = (T) type.getConstructor(SimpleStorage.class, PersistentReader.class).newInstance(this, reader);
            into.put(reader.key, o);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create class for category.");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Please add a constructor that looks like this: public " + type.getSimpleName() + "(PersistentStorage s, PersistentReader r) { ... }");
        }
    }

    @Override
    public boolean insertObject(PersistentObject object) {
        // TODO: Duplicate same-key but different objects!

        Class<? extends PersistentObject> type = object.getClass();
        Map<Object, PersistentObject> map = loadedObjects.get(type);
        SimplePersistentWriter writer = new SimplePersistentWriter();
        object.write(writer);
        if (!map.containsKey(writer.key)) {
            //noinspection unchecked
            map.put(writer.key, object);
            try {
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
        Object key = null;

        @Override
        public void writeString(String value) {
            if (key == null) {
                key = value;
            }
            builder.append(value);
            builder.append(';');
        }

        @Override
        public void writeBoolean(boolean value) {
            if (key == null) {
                key = value;
            }
            builder.append(value ? "true;" : "false;");
        }

        @Override
        public void writeInt(int value) {
            if (key == null) {
                key = value;
            }
            builder.append(Integer.toString(value));
            builder.append(';');
        }
    }

    private <T extends PersistentObject> Map<Object, T> loadObjectsFromFile(Class<T> type) {
        Path path = getCategoryPath(type);
        try {
            Map<Object, T> map = new HashMap<>();
            Files.lines(getCategoryPath(type)).forEach(f -> this.loadObjectIntoMapFromCSV(type, f, map));
            return map;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find category: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends PersistentObject> Stream<T> getAll(Class<T> type) {
        //noinspection unchecked
        return (Stream<T>) loadedObjects.computeIfAbsent(type, this::loadObjectsFromFile).values().stream();
    }

    public void update(Class<? extends PersistentObject> type) {
        SimplePersistentWriter writer = new SimplePersistentWriter();
        loadedObjects.computeIfAbsent(type, k -> new HashMap<>()).forEach((k, v) -> {
            v.write(writer);
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
