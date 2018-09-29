package se.fikaware.persistent;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class CommaSeparatedStorage implements DataStorage {
    class SimpleDataReader implements DataReader {
        private int index = 0;
        Object key = null;
        private final String []csvData;

        SimpleDataReader(String []csvData) {
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

    class SimpleDataWriter implements DataWriter {
        final StringBuilder builder = new StringBuilder();
        Object key = null;

        @Override
        public void writeString(String value) {
            if (key == null) {
                key = value;
            }
            builder.append(value);
            builder.append(',');
        }

        @Override
        public void writeBoolean(boolean value) {
            if (key == null) {
                key = value;
            }
            builder.append(value ? "true," : "false,");
        }

        @Override
        public void writeInt(int value) {
            if (key == null) {
                key = value;
            }
            builder.append(Integer.toString(value));
            builder.append(',');
        }

        @Override
        public void writeNull() throws IOException {
            builder.append("null,");
        }
    }

    private static String persistentRoot = System.getenv().get("HOME") + "/pstorage";
    private final RootStorage rootStorage;
    private final String storageName;
    private Map<Class<? extends PersistentObject>, Map<Object, PersistentObject>> loadedObjects = new HashMap<>();
    private HashSet<Class> hasLoadedFromFile = new HashSet<>();

    private <T extends PersistentObject> Map<Object, T> getLoadedObjects(Class<? extends PersistentObject> type) {
        // noinspection unchecked
        return (Map<Object, T>) loadedObjects.computeIfAbsent(type, k -> new HashMap<>());
    }

    public CommaSeparatedStorage(RootStorage rootStorage, String storageName) {
        this.rootStorage = rootStorage;
        this.storageName = storageName;
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName().replace("$", "..") + ".csv");
    }

    private <T extends PersistentObject> T loadObjectIntoMapFromCSV(Class<? extends PersistentObject> type, String []csvData, Map<Object, T> into) {
        try {
            SimpleDataReader reader = new SimpleDataReader(csvData);

            if (type.isMemberClass() && (type.getModifiers() & Modifier.STATIC) == 0) {
                throw new RuntimeException("Please make " + type.getSimpleName() + " a static class");
            }

            //noinspection unchecked
            T o = (T) type.getConstructor(DataStorage.class, DataReader.class).newInstance(this, reader);
            return into.putIfAbsent(reader.key, o);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create class for category.");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Please add a constructor that looks like this: public " + type.getSimpleName() + "(CommaSeparatedStorage s, DataReader r) { ... }");
        }
    }

    @Override
    public boolean insertObject(PersistentObject object) throws IOException {
        // TODO: Duplicate same-key but different objects!

        Class<? extends PersistentObject> type = object.getClass();
        Map<Object, PersistentObject> map = getLoadedObjects(type);
        SimpleDataWriter writer = new SimpleDataWriter();
        object.write(writer);
        if (!map.containsKey(writer.key)) {
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

    private <T extends PersistentObject> void loadObjectsFromFile(Class<T> type, Map<Object, T> into) {
        Path path = getCategoryPath(type);
        try {
            Files.lines(getCategoryPath(type)).forEach(f -> this.loadObjectIntoMapFromCSV(type, f.split(","), into));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find category: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends PersistentObject> T getObject(Class<T> type, Object key) {
        Map<Object, T> map = getLoadedObjects(type);
        return map.computeIfAbsent(key, k -> {
            Path path = getCategoryPath(type);
            try {
                Stream<String[]> csvLines = Files.lines(path).map(l -> l.split(";"));
                Iterator<String[]> i;
                String[] csvLine;
                for (i = csvLines.iterator(), csvLine = i.next(); i.hasNext(); csvLine = i.next()) {
                    if (csvLine.length > 0 && csvLine[0].equals(key)) {
                        return loadObjectIntoMapFromCSV(type, csvLine, map);
                    }
                }
                return null;
            } catch (FileNotFoundException | NoSuchFileException e) {
                try {
                    Files.createFile(path);
                    return null;
                } catch (IOException e1) {
                    throw new RuntimeException("Could not create category file: " + path + "\n" + e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T extends PersistentObject> Stream<T> getAll(Class<T> type) {
        if (hasLoadedFromFile.contains(type)) {
            return this.<T>getLoadedObjects(type).values().stream();
        } else {
            hasLoadedFromFile.add(type);

            Map<Object, T> intoMap = getLoadedObjects(type);

            loadObjectsFromFile(type, intoMap);

            return intoMap.values().stream();
        }
    }

    public void update(Class<? extends PersistentObject> type) throws IOException {
        SimpleDataWriter writer = new SimpleDataWriter();
        for (PersistentObject p: getLoadedObjects(type).values()) {
            p.write(writer);
            writer.builder.append('\n');
        }
        try {
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getCategoryPath(type).toFile(), false), StandardCharsets.UTF_8))
                    .append(writer.builder.toString()).flush();
        } catch (IOException e) {
            throw new RuntimeException("Could not update objects with type:  " + type.getSimpleName());
        }
    }

    @Override
    public void remove(Class<? extends PersistentObject> aClass, PersistentObject persistentObject) {
        // FIXME: We could get the INDEX/KEY here instead, same as in the other methods.
        getLoadedObjects(aClass).values().remove(persistentObject);
    }

    @Override
    public void deleteAll() {
        File file = Paths.get(persistentRoot, storageName).toFile();
        if (file != null) {
            file.delete();
        }
    }

    @Override
    public RootStorage getRootStorage() {
        return null;
    }
}
