package se.fikaware.web;

import se.fikaware.persistent.ExtendedDataWriter;

import java.io.IOException;
import java.util.Iterator;

public final class SendableIterator <T extends Sendable> implements Sendable {
    private final Iterator<T> iterator;

    public SendableIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeArrayBegin();
        while(iterator.hasNext()) {
            iterator.next().send(writer);
            if (iterator.hasNext()) {
                writer.writeArrayNext();
            }
        }
        writer.writeArrayEnd();
    }
}
