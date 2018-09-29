package se.fikaware.tarta.models;

import se.fikaware.persistent.*;
import se.fikaware.sync.Syncable;
import se.fikaware.web.Sendable;
import se.fikaware.web.Server;

import java.io.IOException;

@Syncable
public class Post extends PersistentObject implements Sendable {
    public String title;
    public String content;
    School school;
    public Group recipient;


    public Post(School school, String title, String content, Group recipient) throws IOException {
        super(school.schoolStorage);
        this.school = school;
        this.title = title;
        this.content = content;
        this.recipient = recipient;
        this.save();
    }

    public Post(DataStorage storage, DataReader reader) {
        super(storage);
        title = reader.readString();
        content = reader.readString();
        school = Server.getInstance().miscStorage.getObject(School.class, reader.readString());
        recipient = storage.getObject(Group.class, reader.readString());
    }

    @Override
    protected void write(DataWriter writer) throws IOException {
        writer.writeString(title);
        writer.writeString(content);
        writer.writeString(school.slugName);
        writer.writeString(recipient.slugName);
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeMapBegin();
        writer.writeMapKey("title");
        writer.writeString(title);
        writer.writeMapNext();
        writer.writeMapKey("content");
        writer.writeString(content);
        writer.writeMapNext();
        writer.writeMapKey("recipient");
        recipient.send(writer);
        writer.writeMapEnd();
    }
}
