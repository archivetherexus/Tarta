package se.fikaware.tarta.models;

import se.fikaware.persistent.*;
import se.fikaware.sync.Syncable;
import se.fikaware.web.Sendable;

import java.io.IOException;
import java.util.*;

@Syncable
public class User extends PersistentObject implements Sendable {
    public String username;

    public String password;

    public boolean isAdmin;

    public List<School> schools;

    public User(School school, String username, String password) throws IOException {
        super(school.getDataStorage());
        this.username = username;
        this.password = password;
        this.isAdmin = false;
        schools = new LinkedList<>() {{
            add(school);
        }};
        this.save();
    }

    public User(DataStorage storage, DataReader reader) {
        super(storage);
        username = reader.readString();
        password = reader.readString();
        isAdmin = reader.readBoolean();
        int count = reader.readInt();
        schools = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            schools.add(storage.getObject(School.class, reader.readString()));
        }
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(username);
        writer.writeString(password);
        writer.writeBoolean(isAdmin);
        writer.writeInt(schools.size());
        for (School school: schools) {
            writer.writeString(school.slugName);
        }
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeMapBegin();
        writer.writeMapKey("username");
        writer.writeString(username);
        writer.writeMapEnd();
    }
}
