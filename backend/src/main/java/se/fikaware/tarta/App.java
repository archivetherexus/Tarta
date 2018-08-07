package se.fikaware.tarta;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

import io.undertow.util.HttpString;
import org.msgpack.MessagePack;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(final String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(3000, "localhost")
                .setHandler(getRoutes())
                .build();
        server.start();
    }

    static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN = new HttpString("Access-Control-Allow-Origin");

    public static HttpHandler getRoutes() {
        return Handlers.path()
                .addExactPath("/list", req -> {
                    req.getResponseHeaders()
                            .put(Headers.CONTENT_TYPE, "text/plain")
                            .put(ACCESS_CONTROL_ALLOW_ORIGIN, "*"); // TODO: Move this to some more approriate place. Also don't use '*'.
                    MessagePack msgpack = new MessagePack();
                    List list = new ArrayList<String>();
                    list.add("Hello");
                    list.add("World");
                    list.add("Ok.");
                    var bf = new ByteArrayOutputStream();
                    msgpack.write(bf, list);
                    req.getResponseSender().send(ByteBuffer.wrap(bf.toByteArray()));
                });
    }
}
