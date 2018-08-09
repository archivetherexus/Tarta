package se.fikaware.web;

import com.mongodb.MongoClient;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.HttpString;
import org.bson.Document;

public class Server {

    private static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN = new HttpString("Access-Control-Allow-Origin");

    public static void start(HttpHandler routes) {
        testDatabase();
        Undertow server = Undertow.builder()
                .addHttpListener(3000, "localhost")
                .setHandler(httpServerExchange -> {
                    httpServerExchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    routes.handleRequest(httpServerExchange);
                })
                .build();
        server.start();
    }

    static void testDatabase() {
        MongoClient mongoClient = new MongoClient();
        var db = mongoClient.getDatabase("tarta-dev");
        var collection = db.getCollection("people");
        var person = new Document().append("name", "John Smith")
                                   .append("age", 19)
                                   .append("great", true);
        collection.insertOne(person);
    }
}
