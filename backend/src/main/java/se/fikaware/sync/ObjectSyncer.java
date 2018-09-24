package se.fikaware.sync;

import se.fikaware.persistent.ExtendedDataWriter;

import java.io.IOException;

public interface ObjectSyncer {
    void write(Object o, ExtendedDataWriter i)  throws IOException, IllegalAccessException;
}