package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.sync.Syncable;
import se.fikaware.tarta.models.syncers.SchoolSyncer;

import java.util.ArrayList;
import java.util.Collection;

@Syncable(syncer = SchoolSyncer.class)
public class School {
    public static MongoCollection<Document> schoolCollection = null;

    public final String schoolName;

    public final String slugName;

    public final ObjectId reference;

    public School() {
        schoolName = "";
        slugName = "";
        reference = null;
    }

    public School(String slugName, String schoolName, ObjectId reference) {
        this.slugName = slugName;
        this.schoolName = schoolName;
        this.reference = reference;
    }

    private Document toDocument() {
        return new Document("name", schoolName).append("slug_name", slugName);
    }

    public static School load(String slugName) {
        var school = schoolCollection.find(Filters.eq("slug_name", slugName)).first();
        return new School(slugName, school.getString("name"), school.getObjectId("_id"));
    }

    public static School load(ObjectId id) {
        var school = schoolCollection.find(Filters.eq("_id", id)).first();
        return new School(school.getString("slug_name"), school.getString("name"), id);
    }

    public static Collection<School> getAll() {
        var list = new ArrayList<School>();
        var iterator = schoolCollection.find();

        for (var entry: iterator) {
            var name = entry.getString("name");
            var slugName = entry.getString("slug_name");
            var id = entry.getObjectId("_id");
            list.add(new School(slugName, name, id));
        }

        return list;
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public static School create(String schoolName) {
        School school = new School(createSlug(schoolName), schoolName, new ObjectId());
        schoolCollection.insertOne(school.toDocument().append("_id", school.reference));
        Group.create(school, schoolName);
        return school;
    }

    public void delete() {
        Group.deleteFrom(this);
        schoolCollection.deleteOne(Filters.eq("_id", this.reference));
    }
}
