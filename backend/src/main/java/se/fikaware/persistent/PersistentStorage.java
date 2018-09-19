package se.fikaware.persistent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class PersistentStorage {
    private static String persistentRoot = System.getenv().get("HOME") + "/pstorage";
    private final String storageName;

    public PersistentStorage(String storageName) {
        this.storageName = storageName;
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName().replace('$', '_'));
    }

    private <T extends PersistentObject> T loadPersistentObjectFromFile(Class<? extends PersistentObject> type, File file) {
        try {
            PersistentReader reader = new PersistentReader() {
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

    public <T extends PersistentObject> Stream<T> loadAll(Class<T> type) {
        Path path = getCategoryPath(type);
        File[]files = path.toFile().listFiles();
        if (files != null) {
            return Arrays.stream(files).map(f -> loadPersistentObjectFromFile(type, f));
        } else {
            throw new RuntimeException("Could not find category: " + path);
        }
    }
}
