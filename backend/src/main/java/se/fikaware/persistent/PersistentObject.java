package se.fikaware.persistent;

public class PersistentObject {
    private final PersistentStorage owner;

    protected PersistentObject(PersistentStorage owner) {
        this.owner = owner;
    }
}
