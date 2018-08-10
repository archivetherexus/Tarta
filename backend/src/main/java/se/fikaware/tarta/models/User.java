package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class User {
    static public MongoCollection<Document> userCollection = null;

    String username;
    String password;

    User(String username) {
        this.username = username;
    }

    private Document toDocument() {
        return new Document()
                .append("username", username)
                .append("password", password);
    }

    void insert() {
        userCollection.insertOne(toDocument());

    }

    void update() {
        userCollection.findOneAndUpdate(new Document().append("username", username), toDocument());
    }

    void load() {
        var data = userCollection.find(new Document().append("username", username)).first();
        password = data.getString("password");
    }
}
