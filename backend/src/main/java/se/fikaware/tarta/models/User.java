package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;


public class User {
    static public MongoCollection<Document> userCollection = null;

    public String username;

    public String password;

    public boolean isAdmin;

    public User() {
        this.username = "";
        this.password = "";
        this.isAdmin = false;
    }

    private Document toDocument() {
        return new Document()
                .append("username", username)
                .append("password", password)
                .append("is_admin", isAdmin);
    }

    public void insert() {
        userCollection.insertOne(toDocument());
    }

    public void update() {
        userCollection.findOneAndUpdate(new Document().append("username", username), toDocument());
    }

    public static boolean exists(String username) {
        var user = userCollection.find(new Document().append("username", username));
        return user.iterator().hasNext();
    }

    public static User load(String username) {
        var user = new User();
        var data = userCollection.find(new Document().append("username", username)).first();
        user.username = username;
        user.password = data.getString("password");
        user.isAdmin = data.getBoolean("is_admin");
        return user;
    }
}
