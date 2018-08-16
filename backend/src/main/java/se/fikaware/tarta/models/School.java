package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;


public class School {

    public static MongoCollection<Document> schoolCollection = null;

    String schoolName;
    ObjectId reference;

    private School(String schoolName, ObjectId reference) {
        this.schoolName = schoolName;
        this.reference = reference;
    }

    public static School load(String name) {
        var school = schoolCollection.find(new Document().append("name", name)).first();
        return new School(name, school.getObjectId("_id"));
    }
}
