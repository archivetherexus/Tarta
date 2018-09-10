package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.mongodb.client.model.Filters;

@Syncable
public class Group {
    public static MongoCollection<Document> groupCollection = null;

    public User[] members;

    public School school;

    @Name("slugName")
    public String slugName;

    @Name("name")
    public String name;

    public ObjectId id;

    public Group(School school, String slugName, String name, User[] members, ObjectId id) {
        this.school = school;
        this.slugName = slugName;
        this.name = name;
        this.members = members;
        this.id = id;
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("slug_name", slugName)
                .append("school_id", school.reference)
                .append("members", Arrays.stream(members).map(m -> m.id).collect(Collectors.toList()));
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public static Group create(School school, String name) {
        ObjectId id = new ObjectId();
        var group = new Group(school, createSlug(name), name, new User[]{}, id);
        groupCollection.insertOne(group.toDocument().append("_id", id));
        return group;
    }

    public static Group load(String recipientSlugName) {
        var entry = groupCollection.find(Filters.eq("slug_name", recipientSlugName)).first();
        return new Group(School.load(entry.getObjectId("school_id")),
                recipientSlugName,
                entry.getString("name"),
                entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                entry.getObjectId("_id"));

    }

    public static Group load(ObjectId id) {
        var entry = groupCollection.find(Filters.eq("_id", id)).first();
        return new Group(School.load(entry.getObjectId("school_id")),
                         entry.getString("slug_name"),
                         entry.getString("name"),
                         entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                         entry.getObjectId("_id"));
    }

    public static Collection<Group> getAll(School school) {
        Collection<Group> list = new ArrayList<>();
        var iterator = groupCollection.find(Filters.eq("school_id", school.reference));

        for (var entry: iterator) {
            list.add(new Group(school,
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
            list.add(new Group(School.load(entry.getObjectId("school_id")),
                    entry.getString("slug_name"),
                    entry.getString("name"),
                    entry.get("members", new ArrayList<ObjectId>()).stream().map(User::load).toArray(User[]::new),
                    entry.getObjectId("_id")));
        }

        return list;
    }

    public void addUser(User user) {
        members = Arrays.copyOf(members, members.length + 1);
        members[members.length - 1] = user;
        groupCollection.updateOne(Filters.eq("_id", id), Updates.addToSet("members", user.id));
    }

    public static void deleteFrom(School from) {
        var filter = Filters.eq("school_id", from.reference);
        Post.postCollection.deleteMany(Filters.in("recipient", groupCollection.find(filter).map(s -> s.getObjectId("_id")).into(new ArrayList<>())));
        groupCollection.deleteMany(filter);
    }
}
