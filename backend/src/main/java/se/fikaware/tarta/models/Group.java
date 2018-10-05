package se.fikaware.tarta.models;

import se.fikaware.database.*;
import se.fikaware.web.Sendable;
import se.fikaware.web.Server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Group extends PersistentObject implements Sendable {
    public String slugName;
    public String name;
    public School school;
    private List<User> members = null;
    private List<String> memberUsernames;

    public Group(DataStorage storage, DataReader dr) {
        super(storage);
        if (storage == Server.getInstance().miscStorage) {
            throw new RuntimeException("Groups should not exists inside of the _misc storage!");
        }
        slugName = dr.readString();
        name = dr.readString();
        school = storage.getObject(School.class, dr.readString());
        memberUsernames = new LinkedList<>();
        int c = dr.readInt();
        for (int i = 0; i < c; i++) {
            memberUsernames.add(dr.readString());
        }
    }

    public Group(School school, String groupName) throws IOException {
        super(school.schoolStorage);
        this.school = school;
        slugName = createSlug(groupName);
        name = groupName;
        members = new LinkedList<>();
        memberUsernames = new LinkedList<>();
        this.save();
    }

    public List<User> getMembers() {
        if (members != null) {
            return members;
        } else {
            members = new LinkedList<>();
            for(String username: memberUsernames) {
                members.add(Server.getInstance().miscStorage.getObject(User.class, username));
            }
            return members;
        }
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public void addUser(User user) throws IOException {
        members.add(user);
        memberUsernames.add(user.username);
        this.save();
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(slugName);
        writer.writeString(name);
        writer.writeString(school.slugName);
        writer.writeInt(members.size());
        for (User member : members) {
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
