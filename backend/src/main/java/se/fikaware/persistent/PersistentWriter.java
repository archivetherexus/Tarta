package se.fikaware.persistent;

public interface PersistentWriter {
    void writeString(String value);
    void writeBoolean(boolean value);
    void writeInt(int value);
}
