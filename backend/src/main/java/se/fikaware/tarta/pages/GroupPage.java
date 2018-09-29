package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.User;
import se.fikaware.web.Response;
import se.fikaware.web.SendableIterator;

public class GroupPage {
    public static void listGet(User user, HttpServerExchange exchange) {
        Response.json(exchange, new SendableIterator<>(user.schools.get(0).schoolStorage.getAll(Group.class).iterator()));
    }
}
