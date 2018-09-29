package se.fikaware.persistent;

import java.io.IOException;
import java.util.Collection;

public interface DataStorage {
    <T extends PersistentObject> T getObject(Class<T> type, Object key);

    <T extends PersistentObject> Collection<T> getAll(Class<T> type);


    boolean insertObject(PersistentObject persistentObject) throws IOException;

    void update(Class<? extends PersistentObject> aClass) throws IOException;

    void remove(Class<? extends PersistentObject> aClass, PersistentObject persistentObject);

    void deleteAll();

    RootStorage getRootStorage();
}
