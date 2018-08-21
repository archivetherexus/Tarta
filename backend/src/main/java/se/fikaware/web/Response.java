package se.fikaware.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import se.fikaware.sync.Syncer;
import se.fikaware.sync.json.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Response {
    public static <T> void json(HttpServerExchange exchange, T object) {
        try {
            Syncer syncer = new Syncer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            syncer.write(new JsonWriter(outputStream), object);
            exchange.getResponseSender().send(outputStream.toString());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/msgpack");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
