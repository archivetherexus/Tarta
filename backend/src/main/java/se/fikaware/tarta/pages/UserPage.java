package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormDataParser;
import se.fikaware.web.Request;
import se.fikaware.web.Response;

import java.io.IOException;

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
            Response.json(exchange, "Success");
        } else {
            Response.json(exchange, "Failure");
        }
    }

    public static void SettingsSet(HttpServerExchange exchange) {

    }

    public static void SettingsGet(HttpServerExchange exchange) {
    }
}
