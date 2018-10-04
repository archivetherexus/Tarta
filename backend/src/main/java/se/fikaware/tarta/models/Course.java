package se.fikaware.tarta.models;

import se.fikaware.database.*;
import se.fikaware.web.Sendable;

import java.io.IOException;

public class Course extends PersistentObject implements Sendable {
    public String slugName;
    public String courseName;
    public School school;

    public Course(School school, String courseName) throws IOException {
        super(school.schoolStorage);
        this.school = school;
        this.slugName = createSlug(courseName);
        this.courseName = courseName;
        this.save();
    }

    public Course(DataStorage storage, DataReader reader) {
        super(storage);
        slugName = reader.readString();
        courseName = reader.readString();
        school = storage.getObject(School.class, reader.readString());

    }

    private static String createSlug(String schoolName) {
        return schoolName.replace(' ', '_').toLowerCase();
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(slugName);
        writer.writeString(courseName);
        writer.writeString(school.slugName);
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeMapBegin();
        writer.writeMapKey("courseName");
        writer.writeString(courseName);
        writer.writeMapNext();
        writer.writeMapKey("slugName");
        writer.writeString(slugName);
        writer.writeMapEnd();
    }
}
