package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.User;
import se.fikaware.web.Response;

public class GroupPage {
    public static void listGet(User user, HttpServerExchange exchange) {
        Response.json(exchange, Group.getAll(user.school[0]));
    }
}
