package se.fikaware.persistent;

import java.io.IOException;

public interface ExtendedDataWriter extends DataWriter {
    void writeArrayBegin() throws IOException;
    void writeArrayNext() throws IOException;
    void writeArrayEnd() throws IOException;
    void writeMapBegin() throws IOException;
    void writeMapKey(String keyName) throws IOException;
    void writeMapNext() throws IOException;
    void writeMapEnd() throws IOException;
}
