package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;

import org.slf4j.LoggerFactory;

import se.fikaware.misc.TinyMap;
import se.fikaware.tarta.models.Session;
import se.fikaware.tarta.models.User;
import se.fikaware.web.Request;
import se.fikaware.web.Response;

public class UserPage {
    public static void login(HttpServerExchange exchange) {
        var form = exchange.getAttachment(FormDataParser.FORM_DATA);

        var logger = LoggerFactory.getLogger(UserPage.class);

        logger.info("A user attempted to login!");

        var username = Request.getString(form, "username", null);
        var password = Request.getString(form, "password", null);

        if (username == null || password == null) {
            exchange.setStatusCode(400);
            return;
        }


        if (User.exists(username)) {
            var user = User.load(username);

            // TODO: The password should be one-way hashed...
            if (password.equals(user.password)) {
                var session = Session.startSession();
                session.user = user;
                Response.json(exchange, new TinyMap<String, String>()
                        .add("status", "OK")
                        .add("session_id", session.sessionID));
            } else {
                Response.json(exchange, new TinyMap<String, String>()
                        .add("status", "Failure")
                        .add("reason", "Incorrect password!"));
            }
        } else {
            Response.json(exchange, new TinyMap<String, String>()
                    .add("status", "Failure")
                    .add("reason", "Unknown user!"));
        }

    }

    public static void settingsSet(HttpServerExchange exchange) {

    }

    public static void settingsGet(HttpServerExchange exchange) {
    }

    public static void name(User user, HttpServerExchange exchange) {
        Response.json(exchange, user.username);
    }
}
