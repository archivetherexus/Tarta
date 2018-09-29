package se.fikaware.web;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import se.fikaware.misc.EverythingIsNonnullByDefault;
import se.fikaware.tarta.models.Session;
import se.fikaware.tarta.models.User;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.BiConsumer;

@EverythingIsNonnullByDefault
public class Handlers {
    public interface UserHandler {
        void handle(User u, HttpServerExchange e) throws IOException;
    }

    private static @Nullable
    Session getSession(HttpServerExchange exchange) {
        String sessionID;
        if (exchange.getRequestMethod().equalToString("POST")) {
            var form = exchange.getAttachment(FormDataParser.FORM_DATA);
            sessionID = Request.getString(form, "sessionID", null);
            if (sessionID == null) {
                throw new BadRequest("You must provide a sessionID!");
            }
        } else {
            var sessionIDAttempt = exchange.getQueryParameters().get("sessionID");
            if (sessionIDAttempt != null) {
                sessionID = sessionIDAttempt.getFirst();
            } else {
                throw new BadRequest("You must provide a sessionID!");
            }
        }
        return Session.continueSession(sessionID);
    }

    public static HttpHandler withUser(UserHandler handler) {
        return (exchange) -> {
            var session = getSession(exchange);
            if (session != null) {
                if (session.user != null) {
                    handler.handle(session.user, exchange);
                } else {
                    throw new ClientError("Please login first!");
                }
            } else {
                throw new ClientError("Invalid session id! Please login again.");
            }

        };
    }

    public static HttpHandler withAdmin(UserHandler handler) {
        return (exchange) -> {
            var session = getSession(exchange);
            if (session != null) {
                if (session.user != null) {
                    if (session.user.isAdmin) {
                        handler.handle(session.user, exchange);
                    } else {
                        throw new ClientError("You're not an admin!");
                    }
                } else {
                    throw new ClientError("Please login first!");
                }
            } else {
                throw new ClientError("Invalid session id! Please login again.");
            }
        };
    }
}
