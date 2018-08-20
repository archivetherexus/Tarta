package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.util.ArrayList;
import java.util.Collection;

@Syncable
public class Post {
    static public MongoCollection<Document> postCollection = null;

    @Name("title")
    public String title;

    @Name("content")
    public String content;

    private School school;

    @SuppressWarnings("unused")
    public Post() {
        this.school = null;
        this.title = "";
        this.content = "";
    }

    public Post(School school, String title, String content) {
        this.school = school;
        this.title = title;
        this.content = content;
    }

    private Document toDocument() {
        return new Document("school", school.reference)
                .append("title", title)
                .append("content", content);
    }

    public static Collection<Post> getAll(School school) {
        var list = new ArrayList<Post>();
        var iterator = postCollection.find(new Document().append("school", school.reference));

        for (var entry: iterator) {
            var title = entry.getString("title");
            var content = entry.getString("content");
            list.add(new Post(school, title, content));
        }

        return list;
    }

    public static Post create(School school , String title, String content) {
        var post = new Post(school, title, content);
        postCollection.insertOne(post.toDocument());
        return post;
    }
}
