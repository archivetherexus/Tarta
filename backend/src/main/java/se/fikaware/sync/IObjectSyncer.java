package se.fikaware.sync;

import java.io.IOException;

public interface IObjectSyncer {
    void write(Object o, IWriter i)  throws IOException, IllegalAccessException;
}