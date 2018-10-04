package se.fikaware.web;

import se.fikaware.database.ExtendedDataWriter;

import java.io.IOException;

public interface Sendable {
    void send(ExtendedDataWriter writer) throws IOException;
}
