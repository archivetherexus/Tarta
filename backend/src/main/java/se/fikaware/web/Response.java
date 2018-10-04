package se.fikaware.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import se.fikaware.database.json.JsonWriter;
import se.fikaware.misc.EverythingIsNonnullByDefault;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@EverythingIsNonnullByDefault
public class Response {
    public static void json(HttpServerExchange exchange, Sendable object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            object.send(new JsonWriter(outputStream));
            exchange.getResponseSender().send(outputStream.toString());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/msgpack");
            exchange.endExchange();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ok(HttpServerExchange exchange) {
        exchange.getResponseSender().send("{\"status\": \"OK\"}");
        exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/msgpack");
        exchange.endExchange();
    }
}
