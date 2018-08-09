package se.fikaware.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.msgpack.MessagePack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Response {
    public static <T> void json(HttpServerExchange exchange, T object) throws IOException {
        MessagePack msgpack = new MessagePack(); // TODO: Could this be made static and moved out?
        ByteArrayOutputStream bf = new ByteArrayOutputStream();
        msgpack.write(bf, object);
        exchange.getResponseSender().send(ByteBuffer.wrap(bf.toByteArray()));
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/msgpack");
    }
}
