package se.fikaware.sync;

public interface IWriter {
    void writeNull();
    void writeInteger(int i);
    void writeString(String s);
    void writeArrayBegin();
    void writeArrayEnd();
    void writeMapBegin();
    void writeKey(String keyName);
    void writeMapEnd();
}
