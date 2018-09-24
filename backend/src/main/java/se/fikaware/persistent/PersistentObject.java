package se.fikaware.persistent;

import java.io.IOException;

public abstract class PersistentObject {
    private final DataStorage owner;

    protected PersistentObject(DataStorage owner) {
        this.owner = owner;
    }

    protected abstract void write(DataWriter writer) throws IOException;

    public final void save() throws IOException {
        if (!owner.insertObject(this)) {
            owner.update(this.getClass());
        }
    }
}
