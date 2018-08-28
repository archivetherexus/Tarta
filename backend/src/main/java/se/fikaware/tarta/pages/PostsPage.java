package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.Post;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.models.User;
import se.fikaware.web.Request;
import se.fikaware.web.Response;

public class PostsPage {

    public static void feedGet(User user, HttpServerExchange exchange) {
        Response.json(exchange, Post.getAll(user.school[0], Group.getAll(user).toArray(new Group[] {})));
    }

    public static void create(User user, HttpServerExchange exchange) {
        School school = user.school[0];
        var form = exchange.getAttachment(FormDataParser.FORM_DATA);
        var title = Request.getString(form, "title", null);
        var content = Request.getString(form, "content", null);
        var recipientSlugName = Request.getString(form, "recipient", null);


        if (title == null || content == null || recipientSlugName == null) {
            exchange.setStatusCode(400);
        } else {
            Post.create(school, title, content, Group.load(recipientSlugName));
            // TODO: Post a notification for all client.
        }
    }
}
