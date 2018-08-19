package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import javax.websocket.OnMessage;
import java.util.ArrayList;
import java.util.Collection;

@Message @Syncable
public class School {
    public static MongoCollection<Document> schoolCollection = null;

    @Index(0) @Name("name")
    public final String schoolName;

    @Ignore
    public final ObjectId reference;

    public School() {
        schoolName = "";
        reference = null;
    }

    public School(String schoolName, ObjectId reference) {
        this.schoolName = schoolName;
        this.reference = reference;
    }

    private Document toDocument() {
        return new Document("name", schoolName);
    }

    public static School load(String name) {
        var school = schoolCollection.find(new Document().append("name", name)).first();
        return new School(name, school.getObjectId("_id"));
    }

    public static Collection<School> getAll() {
        var list = new ArrayList<School>();
        var iterator = schoolCollection.find();

        for (var entry: iterator) {
            var name = entry.getString("name");
            var id = entry.getObjectId("_id");
            list.add(new School(name, id));
        }

        return list;
    }

    public static School create(String schoolName) {
        School school = new School(schoolName, new ObjectId());
        schoolCollection.insertOne(school.toDocument().append("_id", school.reference));
        return school;
    }
}
