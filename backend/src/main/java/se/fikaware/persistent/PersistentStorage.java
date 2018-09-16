package se.fikaware.persistent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class PersistentStorage {
    private static String persistentRoot = "/home/alex/pstorage";
    private final String storageName;

    PersistentStorage(String storageName) {
        this.storageName = storageName;
    }

    private Path getCategoryPath(Class<? extends PersistentObject> type) {
        return Paths.get(persistentRoot, storageName, type.getName());
    }

    private <T extends PersistentObject> T loadPersistentObjectFromFile(Class<? extends PersistentObject> type, File file) {
        try {
            PersistentReader reader = new PersistentReader() {
            };

            //noinspection unchecked
            return (T) type.getConstructor(PersistentStorage.class, PersistentReader.class).newInstance(this, reader);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create class for category.");
        }
    }

    <T extends PersistentObject> Stream<T> loadAll(Class<? extends PersistentObject> type) {
        File[]files = getCategoryPath(type).toFile().listFiles();
        if (files != null) {
            return Arrays.stream(files).map(f -> loadPersistentObjectFromFile(type, f));
        } else {
            throw new RuntimeException("Could not find category.");
        }
    }
}
