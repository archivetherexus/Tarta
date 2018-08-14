package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

import java.util.ArrayList;
import java.util.Collection;

@Message
public class Post {
    static public MongoCollection<Document> postCollection = null;

    @Index(0)
    public String title;

    @Index(1)
    public String content;

    @SuppressWarnings("unused")
    public Post() {
        this.title = "";
        this.content = "";
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private Document toDocument() {
        return new Document()
                .append("title", title)
                .append("content", content);
    }

    public static Collection<Post> getAll() {
        var list = new ArrayList<Post>();
        var iterator = Post.postCollection.find();

        for (var entry: iterator) {
            list.add(new Post(entry.getString("title"), entry.getString("content")));
        }

        return list;
    }

    public static Post create(String title, String content) {
        var post = new Post(title, content);
        postCollection.insertOne(post.toDocument());
        return post;
    }
}
