package se.fikaware.persistent;

public abstract class PersistentObject {
    private final SimpleStorage owner;

    protected PersistentObject(SimpleStorage owner) {
        this.owner = owner;
    }

    protected abstract void write(PersistentWriter writer);

    public final void save() {
        if (!owner.insertObject(this)) {
            owner.update(this.getClass());
        }
    }
}
