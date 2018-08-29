package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Syncable
public class User {
    static public MongoCollection<Document> userCollection = null;

    @Name("username")
    public String username;

    public String password;

    public boolean isAdmin;

    public ObjectId id;

    @Name("schools")
    public School[] school;

    public User() {
        this.username = "";
        this.password = "";
        this.isAdmin = false;
        school = new School[]{};
    }

    private Document toDocument() {
        return new Document("username", username)
                .append("password", password)
                .append("school_ids", Arrays.stream(school).map(s -> s.reference).collect(Collectors.toList()))
                .append("is_admin", isAdmin);
    }

    public void update() {
        userCollection.replaceOne(Filters.eq("username", username), toDocument());
    }

    public static boolean exists(String username) {
        var user = userCollection.find(Filters.eq("username", username));
        return user.iterator().hasNext();
    }

    public static User load(String username) {
        var user = new User();
        var entry = userCollection.find(Filters.eq("username", username)).first();
        user.username = username;
        user.password = entry.getString("password");
        user.isAdmin  = entry.getBoolean("is_admin");
        user.id       = entry.getObjectId("_id");
        user.school   = entry.get("school_ids", new ArrayList<ObjectId>()).stream().map(School::load).toArray(School[]::new);
        return user;
    }

    public static User load(ObjectId id) {
        var user = new User();
        var entry = userCollection.find(Filters.eq("_id", id)).first();
        user.username = entry.getString("username");
        user.password = entry.getString("password");
        user.isAdmin  = entry.getBoolean("is_admin");
        user.id       = id;
        user.school   = entry.get("school_ids", new ArrayList<ObjectId>()).stream().map(School::load).toArray(School[]::new);

        return user;
    }

    public static Collection<User> getAll() {
        var list = new ArrayList<User>();
        var iterator = userCollection.find();

        for (var entry: iterator) {
            var user = new User();
            user.username = entry.getString("username");
            user.password = entry.getString("password");
            user.isAdmin  = entry.getBoolean("is_admin");
            user.id       = entry.getObjectId("_id");
            user.school   = entry.get("school_ids", new ArrayList<ObjectId>()).stream().map(School::load).toArray(School[]::new);
            list.add(user);
        }

        return list;
    }

    public static User create(String username, String password, School school) {
        var user = new User();
        user.username = username;
        user.password = password;
        user.isAdmin = false;
        user.school = new School[]{school};
        user.id = new ObjectId();
        Group.load(school.slugName).addUser(user);
        userCollection.insertOne(user.toDocument().append("_id", user.id));
        return user;
    }
}
