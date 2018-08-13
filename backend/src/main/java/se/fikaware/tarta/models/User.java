package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class User {
    static public MongoCollection<Document> userCollection = null;

    public String username;
    public String password;

    public User(String username) {
        this.username = username;
    }

    private Document toDocument() {
        return new Document()
                .append("username", username)
                .append("password", password);
    }

    public boolean exists() {
        var user = userCollection.find(new Document().append("username", username));
        return user.iterator().hasNext();
    }

    public void insert() {
        userCollection.insertOne(toDocument());

    }

    public void update() {
        userCollection.findOneAndUpdate(new Document().append("username", username), toDocument());
    }

    public void load() {
        var data = userCollection.find(new Document().append("username", username)).first();
        password = data.getString("password");
    }
}
