package se.fikaware.web;

import com.mongodb.client.MongoDatabase;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.HttpString;

import se.fikaware.tarta.models.User;

import java.util.function.Supplier;
import java.util.logging.*;


public class Server {

    private static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN = new HttpString("Access-Control-Allow-Origin");

    public static void start(Supplier<HttpHandler> routesCreator, Supplier<MongoDatabase> databaseCreator) {
        setupLogger();
        setupDatabase(databaseCreator.get());
        setupWebServer(routesCreator.get());
    }

    private static void setupLogger() {
        var formatter = new Formatter() {

            @Override
            public String format(LogRecord record) {
                return "[" + record.getLoggerName() + "] " + record.getMessage() + "\n";
            }
        };

        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        Handler[] handlers = logger.getHandlers();
        for(Handler handler : handlers) {
            handler.setFormatter(formatter);
        }
    }

    private static void setupWebServer(HttpHandler routes) {
        Undertow server = Undertow.builder()
                .addHttpListener(3000, "localhost")
                .setHandler(httpServerExchange -> {
                    httpServerExchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    routes.handleRequest(httpServerExchange);
                })
                .build();
        server.start();
    }

    private static void setupDatabase(MongoDatabase database) {
        User.userCollection = database.getCollection("users");
    }

    /*

    SCRAP YARD:

    static void testDatabase() {
        var collection = db.getCollection("people");
        var person = new Document().append("name", "John Smith")
                                   .append("age", 19)
                                   .append("great", true);
        collection.insertOne(person);
    }

    // Read the rest of the body... Wrap inside: BlockingHandler
    var body = new String(req.getInputStream().readAllBytes());
    System.out.println(body);

    */
}
