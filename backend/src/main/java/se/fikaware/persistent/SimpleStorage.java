package se.fikaware.persistent;

import java.util.stream.Stream;

public interface SimpleStorage {
    <T extends PersistentObject> Stream<T> getAll(Class<T> type);

    boolean insertObject(PersistentObject persistentObject);

    void update(Class<? extends PersistentObject> aClass);
}
