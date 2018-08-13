package se.fikaware.web;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Session;
import se.fikaware.tarta.models.User;
import java.util.function.BiConsumer;

public class Handlers {
    public static HttpHandler withUser(BiConsumer<User, HttpServerExchange> handler) {
        return (exchange) -> {
            var session_id = exchange.getQueryParameters().get("session_id").getFirst();
            if (session_id != null) {
                var session = Session.continueSession(session_id);
                if (session != null) {
                    if (session.user != null) {
                        handler.accept(session.user, exchange);
                    } else {
                        // TODO: Report not logged in.
                    }
                } else {
                    // TODO: Report invalid session ID.
                }
            } else {
                // TODO: Report error. Not enough parameters.
            }
        };
    }
}
