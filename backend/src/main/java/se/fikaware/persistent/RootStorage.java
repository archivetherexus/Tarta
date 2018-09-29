package se.fikaware.persistent;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class RootStorage {
    private Map<String, DataStorage> storages = new HashMap<>();
    private BiFunction<RootStorage, String, DataStorage> loader;

    public RootStorage(BiFunction<RootStorage, String, DataStorage> loader) {
        this.loader = loader;
    }

    public DataStorage getStorage(String storageName) {
        return storages.computeIfAbsent(storageName, k -> loader.apply(this, k));
    }
}
