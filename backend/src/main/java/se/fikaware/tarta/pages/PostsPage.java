package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Post;
import se.fikaware.web.Response;

public class PostsPage {
    public static void FeedGet(HttpServerExchange exchange) {
        Response.json(exchange, Post.getAll());
    }

    public static void Create(HttpServerExchange exchange) {

    }
}
