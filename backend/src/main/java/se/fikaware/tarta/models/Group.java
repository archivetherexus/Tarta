package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.persistent.*;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;
import se.fikaware.web.Sendable;
import se.fikaware.web.Server;

@Syncable
public class Group extends PersistentObject implements Sendable {
    public String slugName;
    public String name;
    public School school;
    public List<User> members;

    public Group(DataStorage storage, DataReader dr) {
        super(storage);
        slugName = dr.readString();
        name = dr.readString();
        school = storage.getObject(School.class, dr.readString());
        members = new LinkedList<>();
        int c = dr.readInt();
        for (int i = 0; i < c; i++) {
            int id = dr.readInt();
            members.add(storage.getObject(User.class, id));
        }
    }

    public Group(School school, String groupName) throws IOException {
        super(school.schoolStorage);
        this.school = school;
        slugName = createSlug(groupName);
        name = groupName;
        members = new LinkedList<>();
        this.save();
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public void addUser(User user) {
        members.add(user);
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(slugName);
        writer.writeString(name);
        writer.writeString(school.slugName);
        for (User member: members) {
            writer.writeString(member.username);
        }
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeMapBegin();
        writer.writeMapKey("slugName");
        writer.writeString(slugName);
        writer.writeMapNext();
        writer.writeMapKey("name");
        writer.writeString(name);
        writer.writeMapEnd();
    }
}
