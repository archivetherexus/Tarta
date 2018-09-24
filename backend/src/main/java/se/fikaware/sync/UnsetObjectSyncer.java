package se.fikaware.sync;

import se.fikaware.persistent.ExtendedDataWriter;

public class UnsetObjectSyncer implements ObjectSyncer {
    @Override
    public void write(Object o, ExtendedDataWriter i) {
        throw new UnsupportedOperationException();
    }
}
