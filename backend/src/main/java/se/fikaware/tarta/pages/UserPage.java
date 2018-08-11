package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;

import org.slf4j.LoggerFactory;

import se.fikaware.misc.TinyMap;
import se.fikaware.tarta.models.Session;
import se.fikaware.web.Request;
import se.fikaware.web.Response;

import java.io.IOException;

public class UserPage {
    public static void Login(HttpServerExchange exchange) throws IOException {
        var form = exchange.getAttachment(FormDataParser.FORM_DATA);

        var logger = LoggerFactory.getLogger(UserPage.class);

        logger.info("A user attempted to login!");

        var username = Request.getString(form, "username", null);
        var password = Request.getString(form, "password", null);

        if (username == null || password == null) {
            exchange.setStatusCode(400);
            return;
        }

        if (username.equals(password)) {
            Response.json(exchange, new TinyMap<String, String>()
                    .add("status", "OK")
                    .add("session_id", Session.startSession().sessionID));
        } else {
            Response.json(exchange, new TinyMap<String, String>()
                    .add("status", "Failure")
                    .add("reason", "Incorrect password!"));
        }
    }

    public static void SettingsSet(HttpServerExchange exchange) {

    }

    public static void SettingsGet(HttpServerExchange exchange) {
    }
}
