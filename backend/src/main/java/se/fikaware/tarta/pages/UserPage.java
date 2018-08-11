package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import se.fikaware.tarta.models.Session;
import se.fikaware.web.Request;
import se.fikaware.web.Response;

import java.io.IOException;
import java.util.HashMap;

public class UserPage {
    public static void Login(HttpServerExchange exchange) throws IOException {
        var form = exchange.getAttachment(FormDataParser.FORM_DATA);


        var username = Request.getString(form, "username", null);
        var password = Request.getString(form, "password", null);

        if (username == null || password == null) {
            exchange.setStatusCode(400);
            return;
        }

        if (username.equals(password)) {
            var response = new HashMap<String, String>();
            response.put("status", "OK");
            response.put("session_id", Session.startSession().sessionID);
            Response.json(exchange, response);
        } else {
            var response = new HashMap<String, String>();
            response.put("status", "OK");
            Response.json(exchange, "Failure");
        }
    }

    public static void SettingsSet(HttpServerExchange exchange) {

    }

    public static void SettingsGet(HttpServerExchange exchange) {
    }
}
