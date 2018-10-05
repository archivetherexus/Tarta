package se.fikaware.database;


import se.fikaware.misc.ThrowingBiFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RootStorage {
    private Map<String, DataStorage> storages = new HashMap<>();
    private ThrowingBiFunction<RootStorage, String, DataStorage, IOException> loader;

    public RootStorage(ThrowingBiFunction<RootStorage, String, DataStorage, IOException> loader) {
        this.loader = loader;
    }

    public DataStorage getStorage(String storageName) throws IOException {
        if (storages.containsKey(storageName)) {
            return storages.get(storageName);
        } else {
            DataStorage storage = loader.apply(this, storageName);
            storages.put(storageName, storage);
            return storage;
        }
    }

    public void deleteStorage(DataStorage storage) {
        storage.handleDelete();
        // TODO: Lookup by index instead. Note that we can't trust the storage name! Might be an unnecessary optimization.
        storages.values().remove(storage);
    }
}
