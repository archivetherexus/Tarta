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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Syncable(syncer = SchoolSyncer.class)
public class School {
    public static MongoCollection<Document> schoolCollection = null;

    public final String schoolName;

    public final String slugName;

    public final ObjectId reference;

    private final List<Course> courses;

    public School() {
        schoolName = "";
        slugName = "";
        reference = null;
        courses = new LinkedList<>();
    }

    public School(String slugName, String schoolName, ObjectId reference, List<Course> courses) {
        this.slugName = slugName;
        this.schoolName = schoolName;
        this.reference = reference;
        this.courses = courses;
    }

    private Document toDocument() {
        return new Document("name", schoolName)
                .append("slug_name", slugName)
                .append("course_ids", courses.stream().map(c -> c.id).collect(Collectors.toList()));
    }

    public static School load(String slugName) {
        var school = schoolCollection.find(Filters.eq("slug_name", slugName)).first();
        return new School(slugName, school.getString("name"), school.getObjectId("_id"), school.get("course_ids", new ArrayList<ObjectId>()).stream().map(Course::load).collect(Collectors.toList()));
    }

    public static School load(ObjectId id) {
        var school = schoolCollection.find(Filters.eq("_id", id)).first();
        return new School(school.getString("slug_name"), school.getString("name"), id, school.get("course_ids", new ArrayList<ObjectId>()).stream().map(Course::load).collect(Collectors.toList()));
    }

    public static Collection<School> getAll() {
        var list = new ArrayList<School>();
        var iterator = schoolCollection.find();

        for (var entry: iterator) {
            var name = entry.getString("name");
            var slugName = entry.getString("slug_name");
            var id = entry.getObjectId("_id");
            list.add(new School(slugName, name, id, entry.get("course_ids", new ArrayList<ObjectId>()).stream().map(Course::load).collect(Collectors.toList())));
        }

        return list;
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    public static School create(String schoolName) {
        School school = new School(createSlug(schoolName), schoolName, new ObjectId(), new ArrayList<>());
        schoolCollection.insertOne(school.toDocument().append("_id", school.reference));
        Group.create(school, schoolName);
        return school;
    }

    public void delete() {
        Group.deleteFrom(this);
        schoolCollection.deleteOne(Filters.eq("_id", this.reference));
    }

    public Course addCourse(String courseName) {
        Course course = Course.create(this, courseName);
        schoolCollection.updateOne(Filters.eq("_id", reference), Updates.addToSet("courses", course.id));
        courses.add(course);
        return course;
    }
}
