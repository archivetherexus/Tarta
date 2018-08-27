package se.fikaware.sync;

public class UnsetObjectSyncer implements IObjectSyncer {
    @Override
    public void write(Object o, IWriter i) {
        throw new UnsupportedOperationException();
    }
}
