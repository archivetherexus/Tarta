package se.fikaware.tarta.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import se.fikaware.sync.Name;
import se.fikaware.sync.Syncable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Syncable
public class Post {
    static public MongoCollection<Document> postCollection = null;

    @Name("title")
    public String title;

    @Name("content")
    public String content;

    private School school;

    @Name("recipient")
    public Group recipient;

    @SuppressWarnings("unused")
    public Post() {
        this.school = null;
        this.title = "";
        this.content = "";
        this.recipient = null;
    }

    public Post(School school, String title, String content, Group recipient) {
        this.school = school;
        this.title = title;
        this.content = content;
        this.recipient = recipient;
    }

    private Document toDocument() {
        return new Document("school_id", school.reference)
                .append("title", title)
                .append("recipient", recipient.id)
                .append("content", content);
    }

    public static Collection<Post> getAll(School school, Group[] groups) {
        var list = new ArrayList<Post>();
        var iterator = postCollection.find(Filters.and(Filters.eq("school_id", school.reference),
                                                       Filters.in("recipient", Arrays.stream(groups).map(g -> g.id).collect(Collectors.toList()))));

        for (var entry: iterator) {
            var title = entry.getString("title");
            var content = entry.getString("content");
            var recipient = Group.load(entry.getObjectId("recipient"));
            list.add(new Post(school, title, content, recipient));
        }

        return list;
    }

    public static Post create(School school , String title, String content, Group recipient) {
        var post = new Post(school, title, content, recipient);
        postCollection.insertOne(post.toDocument());
        return post;
    }
}
