package se.fikaware.persistent;

import java.io.IOException;
import java.util.stream.Stream;

public interface DataStorage {
    <T extends PersistentObject> T getObject(Class<T> type, Object key);

    <T extends PersistentObject> Stream<T> getAll(Class<T> type);


    boolean insertObject(PersistentObject persistentObject) throws IOException;

    void update(Class<? extends PersistentObject> aClass) throws IOException;
}
