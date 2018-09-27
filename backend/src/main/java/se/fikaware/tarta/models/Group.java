package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.persistent.DataReader;
import se.fikaware.persistent.DataStorage;
import se.fikaware.persistent.DataWriter;
import se.fikaware.persistent.PersistentObject;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;
import se.fikaware.web.Server;

@Syncable
public class Group extends PersistentObject  {
    public static MongoCollection<Document> groupCollection = null;

    public List<User> members;

    public School school;

    @Name("slugName")
    public String slugName;

    @Name("name")
    public String name;

    public Group(DataStorage storage, School school, String slugName, String name, User[] members, ObjectId id) {
        super(storage);
        this.school = school;
        this.slugName = slugName;
        this.name = name;
        this.members = Arrays.stream(members).collect(Collectors.toList());
    }

    public Group(DataStorage storage, DataReader dr) {
        super(storage);
        slugName = dr.readString();
        name = dr.readString();
        members = new LinkedList<>();
        int c = dr.readInt();
        for (int i = 0; i < c; i++) {
            int id = dr.readInt();
            members.add(storage.getObject(User.class, id));
        }
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("slug_name", slugName)
                .append("school_id", school.reference)
                .append("members", new LinkedList());
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public static Group create(School school, String name) {
        ObjectId id = new ObjectId();
        var group = new Group(Server.storage, school, createSlug(name), name, new User[]{}, id);
        groupCollection.insertOne(group.toDocument().append("_id", id));
        return group;
    }

    public static Group load(String recipientSlugName) {
        var entry = groupCollection.find(Filters.eq("slug_name", recipientSlugName)).first();
        return new Group(Server.storage, School.load(entry.getObjectId("school_id")),
                recipientSlugName,
                entry.getString("name"),
                entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                entry.getObjectId("_id"));

    }

    public static Group load(ObjectId id) {
        var entry = groupCollection.find(Filters.eq("_id", id)).first();
        return new Group(Server.storage, School.load(entry.getObjectId("school_id")),
                         entry.getString("slug_name"),
                         entry.getString("name"),
                         entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                         entry.getObjectId("_id"));
    }

    public static Collection<Group> getAll(School school) {
        Collection<Group> list = new ArrayList<>();
        var iterator = groupCollection.find(Filters.eq("school_id", school.reference));

        for (var entry: iterator) {
            list.add(new Group(Server.storage,
                                school,
                                entry.getString("slug_name"),
                                entry.getString("name"),
                                entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                                entry.getObjectId("_id")));
        }

        return list;
    }

    public static Collection<Group> getAll(User user) {
        Collection<Group> list = new ArrayList<>();
        var iterator = groupCollection.find(Filters.in("members", user.id));

        for (var entry: iterator) {
            list.add(new Group(Server.storage,
                    School.load(entry.getObjectId("school_id")),
                    entry.getString("slug_name"),
                    entry.getString("name"),
                    entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                    entry.getObjectId("_id")));
        }

        return list;
    }

    public void addUser(User user) {
        members.add(user);
        //groupCollection.updateOne(Filters.eq("_id", id), Updates.addToSet("members", user.id));
    }

    public static void deleteFrom(School from) {
        var filter = Filters.eq("school_id", from.reference);
        Post.postCollection.deleteMany(Filters.in("recipient", groupCollection.find(filter).map(s -> s.getObjectId("_id")).into(new ArrayList<>())));
        groupCollection.deleteMany(filter);
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(slugName);
        writer.writeString(name);
    }
}
