package se.fikaware.sync.json;

import se.fikaware.persistent.ExtendedDataWriter;

import java.io.IOException;
import java.io.OutputStream;

public class JsonWriter implements ExtendedDataWriter {
    private OutputStream outputStream;
    static final private byte[] nullBytes = "null".getBytes();
    static final private byte[] trueBytes = "true".getBytes();
    static final private byte[] falseBytes = "false".getBytes();

    public JsonWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void writeNull() throws IOException {
        outputStream.write(nullBytes);
    }

    @Override
    public void writeString(String s) throws IOException {
        outputStream.write('"');
        for (var c: s.getBytes()) {
            if (c == '"' || c == '\\') {
                outputStream.write('\\');
            }
            outputStream.write(c);
        }
        outputStream.write('"');
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        outputStream.write(value ? trueBytes : falseBytes);
    }

    @Override
    public void writeInt(int value) throws IOException {
        outputStream.write(Integer.toString(value).getBytes());
    }

    @Override
    public void writeArrayBegin() throws IOException {
        outputStream.write('[');
    }

    @Override
    public void writeArrayNext() throws IOException {
        outputStream.write(',');
    }

    @Override
    public void writeArrayEnd() throws IOException {
        outputStream.write(']');
        outputStream.flush();
    }

    @Override
    public void writeMapBegin() throws IOException {
        outputStream.write('{');
    }

    @Override
    public void writeMapKey(String keyName) throws IOException {
        writeString(keyName);
        outputStream.write(':');
    }

    @Override
    public void writeMapNext() throws IOException {
        outputStream.write(',');
    }

    @Override
    public void writeMapEnd() throws IOException {
        outputStream.write('}');
    }
}
