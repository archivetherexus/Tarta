package se.fikaware.web;

import se.fikaware.persistent.ExtendedDataWriter;

import java.io.IOException;

public interface Sendable {
    void send(ExtendedDataWriter writer) throws IOException;
}
