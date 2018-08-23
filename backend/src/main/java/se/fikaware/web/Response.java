package se.fikaware.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import se.fikaware.sync.Syncer;
import se.fikaware.sync.json.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Response {
    static private Syncer syncer = new Syncer();
    public static <T> void json(HttpServerExchange exchange, T object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            syncer.write(new JsonWriter(outputStream), object);
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
