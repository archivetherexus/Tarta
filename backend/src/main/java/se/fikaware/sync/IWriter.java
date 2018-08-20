package se.fikaware.sync;

import java.io.IOException;

public interface IWriter {
    void writeNull() throws IOException;
    void writeInteger(int i) throws IOException;
    void writeString(String s) throws IOException;
    void writeArrayBegin() throws IOException;
    void writeArrayNext() throws IOException;
    void writeArrayEnd() throws IOException;
    void writeMapBegin() throws IOException;
    void writeMapKey(String keyName) throws IOException;
    void writeMapNext() throws IOException;
    void writeMapEnd() throws IOException;
}
