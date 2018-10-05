package se.fikaware.database;

import java.io.IOException;
import java.util.Collection;

public interface DataStorage {
    <T extends PersistentObject> T getObject(Class<T> type, Object key);
    <T extends PersistentObject> Collection<T> getAll(Class<T> type);

    void remove(Class<? extends PersistentObject> aClass, PersistentObject persistentObject);
    void remove(Class<? extends PersistentObject> aClass) throws IOException;
    void remove() throws IOException;

    void handleUpdate(Class<? extends PersistentObject> aClass) throws IOException;
    boolean handleInsertObject(PersistentObject persistentObject) throws IOException;
    void handleDelete();

    RootStorage getRootStorage();
}
