package se.fikaware.database;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class CommaSeparatedStorage implements DataStorage {
    private static final String SEPARATOR = ",";
    private static String persistentRoot = System.getenv().get("HOME") + "/pstorage";
    private final RootStorage rootStorage;
    private final String storageName;
    private Map<Class<? extends PersistentObject>, Map<Object, PersistentObject>> loadedObjects = new HashMap<>();
    private HashSet<Class> hasLoadedFromFile = new HashSet<>();

    public CommaSeparatedStorage(RootStorage rootStorage, String storageName) throws IOException {
        this.rootStorage = rootStorage;
        this.storageName = storageName;

        Path path = Paths.get(persistentRoot, storageName);
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IOException("Could not create sub-storage at path: " + path + "\n" + e);
            }
        }
    }

    private <T extends PersistentObject> Map<Object, T> getLoadedObjects(Class<? extends PersistentObject> type) {
        // noinspection unchecked
        return (Map<Object, T>) loadedObjects.computeIfAbsent(type, k -> new HashMap<>());
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName().replace("$", "..") + ".csv");
    }

    private <T extends PersistentObject> T loadObjectIntoMapFromCSV(Class<? extends PersistentObject> type, SimpleDataReader reader, Map<Object, T> into) {
        try {
            if (type.isMemberClass() && (type.getModifiers() & Modifier.STATIC) == 0) {
                throw new RuntimeException("Please make " + type.getSimpleName() + " a static class");
            }

            //noinspection unchecked
            T o = (T) type.getConstructor(DataStorage.class, DataReader.class).newInstance(this, reader);
            into.putIfAbsent(reader.key, o);
            return o;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create class for category: " + type.getSimpleName() + "\n" + e);
        } catch (InvocationTargetException e) {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.write("An exception occured while loading object of type: ");
            writer.write(type.getSimpleName());
            writer.write("\nReason: ");
            e.getCause().printStackTrace(writer);
            RuntimeException exception = new RuntimeException(out.toString());
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Please add a constructor that looks like this: public " + type.getSimpleName() + "(CommaSeparatedStorage s, DataReader r) { ... }");
        }
    }

    @Override
    public boolean handleInsertObject(PersistentObject object) throws IOException {
        // TODO: Duplicate same-key but different objects!

        Class<? extends PersistentObject> type = object.getClass();
        Map<Object, PersistentObject> map = getLoadedObjects(type);
        SimpleDataWriter writer = new SimpleDataWriter();
        object.write(writer);
        if (!map.containsKey(writer.key)) {
            map.put(writer.key, object);
            try {
                writer.builder.append('\n');
                File file = getCategoryPath(type).toFile();
                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))
                        .append(writer.builder.toString()).flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not add object to database memory! Type: " + type.getSimpleName() + " " + e);
            }
            return true;
        } else {
            return false;
        }
    }

    private <T extends PersistentObject> void loadObjectsFromFile(Class<T> type, Map<Object, T> into) throws FileNotFoundException, NoSuchFileException {
        try {
            Files.lines(getCategoryPath(type)).forEach(f -> this.loadObjectIntoMapFromCSV(type, new SimpleDataReader(f), into));
        } catch (FileNotFoundException | NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends PersistentObject> T getObject(Class<T> type, Object key) {
        String keyString = key.toString();
        Map<Object, T> map = getLoadedObjects(type);
        T object = map.get(key);
        if (object == null) {
            Path path = getCategoryPath(type);
            try {
                Stream<String> csvLines = Files.lines(path); // TODO: Perhaps we could skip to a line index using seek.
                Iterator<String> i = csvLines.iterator();
                while (i.hasNext()) {
                    SimpleDataReader reader = new SimpleDataReader(i.next());
                    reader.readString();
                    if (reader.key.equals(keyString)) {
                        reader.index = 0;
                        reader.key = null;
                        return loadObjectIntoMapFromCSV(type, reader, map);
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
        } else {
            return object;
        }
    }

    @Override
    public <T extends PersistentObject> Collection<T> getAll(Class<T> type) {
        if (hasLoadedFromFile.contains(type)) {
            return this.<T>getLoadedObjects(type).values();
        } else {
            hasLoadedFromFile.add(type);

            Map<Object, T> intoMap = getLoadedObjects(type);

            try {
                loadObjectsFromFile(type, intoMap);
            } catch (FileNotFoundException | NoSuchFileException e) {
                Path path = getCategoryPath(type);
                try {
                    Files.createFile(path);
                    return Collections.emptyList();
                } catch (IOException e1) {
                    throw new RuntimeException("Could not create category file: " + path + "\n" + e);
                }
            }

            return intoMap.values();
        }
    }

    public void handleUpdate(Class<? extends PersistentObject> type) throws IOException {
        SimpleDataWriter writer = new SimpleDataWriter();
        for (PersistentObject p : getLoadedObjects(type).values()) {
            p.write(writer);
            writer.builder.append('\n');
        }
        try {
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getCategoryPath(type).toFile(), false), StandardCharsets.UTF_8))
                    .append(writer.builder.toString()).flush();
        } catch (IOException e) {
            throw new RuntimeException("Could not handleUpdate objects with type: " + type.getSimpleName());
        }
    }

    @Override
    public void remove(Class<? extends PersistentObject> aClass, PersistentObject persistentObject) {
        // FIXME: We could get the INDEX/KEY here instead, same as in the other methods.
        getLoadedObjects(aClass).values().remove(persistentObject);
    }

    @Override
    public void remove(Class<? extends PersistentObject> aClass) throws IOException {
        getLoadedObjects(aClass).clear();
        FileChannel.open(getCategoryPath(aClass), StandardOpenOption.WRITE).truncate(0).close();
    }

    @Override
    public void remove() {
        loadedObjects.clear();
        File directory = Paths.get(persistentRoot, storageName).toFile();
        if (directory.exists()) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                deleteFileSystemEntry(new File(directory, children[i]));
            }
        }
    }

    private void deleteFileSystemEntry(File directory) {
        if (directory.isDirectory())
        {
            String[] children = directory.list();
            if (children == null) {
                return;
            }
            for (int i = 0; i < children.length; i++) {
                deleteFileSystemEntry(new File(directory, children[i]));
            }
        }
        directory.delete();
    }

    @Override
    public void handleDelete() {
        File file = Paths.get(persistentRoot, storageName).toFile();
        if (file.exists()) {
            deleteFileSystemEntry(file);
        }
    }

    @Override
    public RootStorage getRootStorage() {
        return rootStorage;
    }

    class SimpleDataReader implements DataReader { // TODO: Support "" \, and \\
        private final String[] csvData;
        Object key = null;
        private int index = 0;

        SimpleDataReader(String csvData) {
            this.csvData = csvData.split(SEPARATOR);
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
            // TODO: This could be optimised.
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                builder.append('"');
                for (int i = 0; i < value.length(); i++) {
                    char c = value.charAt(i);
                    switch (c) {
                        case '\n':
                            builder.append('\\');
                            builder.append('n');
                            break;
                        case '\\':
                            builder.append('\\');
                            builder.append('\\');
                            break;
                        case '"':
                            builder.append('"');
                            // fallthrough //
                        default:
                            builder.append(c);

                    }
                }
                builder.append('"');
            } else {
                builder.append(value);
            }
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
        public void writeNull() {
            builder.append("null,");
        }
    }
}
