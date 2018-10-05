package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.Post;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.models.User;
import se.fikaware.web.Request;
import se.fikaware.web.Response;
import se.fikaware.web.SendableIterator;

import java.io.IOException;
import java.util.stream.Collectors;

public class PostsPage {

    public static void feedGet(User user, HttpServerExchange exchange) {
        var userGroup = user.schools.get(0).schoolStorage.getAll(Group.class).stream().filter(g -> g.getMembers().contains(user)).collect(Collectors.toList());
        Response.json(exchange, new SendableIterator<>(user.schools.get(0).schoolStorage.getAll(Post.class).stream().filter(p -> userGroup.stream().anyMatch(g -> g == p.recipient)).iterator()));
    }

    public static void create(User user, HttpServerExchange exchange) throws IOException {
        School school = user.schools.get(0);
        var form = exchange.getAttachment(FormDataParser.FORM_DATA);
        var title = Request.getString(form, "title", null);
        var content = Request.getString(form, "content", null);
        var recipientSlugName = Request.getString(form, "recipient", null);


        if (title == null || content == null || recipientSlugName == null) {
            exchange.setStatusCode(400);
        } else {
            new Post(school, title, content, school.schoolStorage.getObject(Group.class, recipientSlugName));
            // TODO: Post a notification for all client.
            Response.ok(exchange);
        }
    }
}
