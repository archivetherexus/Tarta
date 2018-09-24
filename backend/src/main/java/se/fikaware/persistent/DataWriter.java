package se.fikaware.persistent;

import java.io.IOException;

public interface DataWriter {
    void writeString(String value) throws IOException;
    void writeBoolean(boolean value) throws IOException;
    void writeInt(int value) throws IOException;
    void writeNull() throws IOException;
}
