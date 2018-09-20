package se.fikaware.persistent;

public interface PersistentReader {
    String readString();
    boolean readBoolean();
    int readInt();
}
