package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.util.ArrayList;
import java.util.Collection;

@Syncable
public class Course {
    public static MongoCollection<Document> courseCollection = null;

    public School school;

    @Name("slugName")
    public String slugName;

    @Name("courseName")
    public String courseName;

    @Name("id")
    public ObjectId id;

    public Course(School school, String slugName, String courseName, ObjectId id) {
        this.school = school;
        this.slugName = slugName;
        this.courseName = courseName;
        this.id = id;
    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    static Course create(School school, String courseName) {
        Course course = new Course(school, createSlug(courseName), courseName, new ObjectId());
        courseCollection.insertOne(course.toDocument().append("_id", course.id));
        return course;
    }

    public static Collection<Course> getAll(School school) {
        Collection<Course> list = new ArrayList<>();
        var iterator = courseCollection.find(Filters.eq("school_id", school.reference));

        for (var entry: iterator) {
            list.add(new Course(School.load(entry.getObjectId("school_id")), entry.getString("slug_name"), entry.getString("course_name"), entry.getObjectId("_id")));
        }

        return list;
    }

    private Document toDocument() {
        return new Document("course_name", courseName)
                .append("school_id", school.reference)
                .append("slug_name", school.slugName);
    }

    public static Course load(ObjectId id) {
        var course = courseCollection.find(Filters.eq("_id", id)).first();
        return new Course(School.load(course.getObjectId("school_id")), course.getString("slug_name"), course.getString("course_name"), id);
    }
}
