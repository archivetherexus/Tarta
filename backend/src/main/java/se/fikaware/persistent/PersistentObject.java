package se.fikaware.persistent;

public class PersistentObject {
    private final PersistentStorage owner;

    PersistentObject(PersistentStorage owner) {
        this.owner = owner;
    }
}
