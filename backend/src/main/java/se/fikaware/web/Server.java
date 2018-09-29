package se.fikaware.web;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.util.HttpString;

import org.slf4j.LoggerFactory;
import se.fikaware.misc.EverythingIsNonnullByDefault;
import se.fikaware.persistent.CommaSeparatedStorage;
import se.fikaware.persistent.DataStorage;
import se.fikaware.persistent.RootStorage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.*;

@EverythingIsNonnullByDefault
public class Server {

    @Nullable
    private static Server instance = null;

    public static Server getInstance() {
        if (instance == null) {
            throw new RuntimeException("Server not initialised!");
        }
        return instance;
    }

    public final DataStorage miscStorage;

    private RoutingHandler routes = new RoutingHandler();

    private static final HttpString ACCESS_CONTROL_ALLOW_ORIGIN = new HttpString("Access-Control-Allow-Origin");

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Server.class);

    // TODO: Add Whitelist and Blacklist middle-layer for the Storages...

    public Server post(String path, HttpHandler handler) {
        routes.post(path, new EagerFormParsingHandler(exchange -> {
            try {
                exchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                handler.handleRequest(exchange);
            } catch (ClientError e) {
                exchange.setStatusCode(400);
                exchange.getResponseSender().send(e.getMessage());
            } catch (BadRequest b) {
                exchange.setStatusCode(400);
                logger.error(b.getMessage());
            } catch(Throwable r) {
                exchange.setStatusCode(500);
                r.printStackTrace();
            }
        }));
        return this;
    }

    public Server get(String path, HttpHandler handler) {
        routes.get(path, exchange -> {
            try {
                exchange.getResponseHeaders().put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                handler.handleRequest(exchange);
            } catch (ClientError e) {
                exchange.setStatusCode(400);
                exchange.getResponseSender().send(e.getMessage());
            } catch (BadRequest b) {
                exchange.setStatusCode(400);
                logger.error(b.getMessage());
            } catch(Throwable r) {
                exchange.setStatusCode(500);
                r.printStackTrace();
            } finally {
                if (!exchange.isComplete()) {
                    logger.error("No response written for: " + exchange.getRequestURL());
                }
            }
        });
        return this;
    }

    public Server() {
        instance = this;
        setupLogger();
        RootStorage storage = new RootStorage((s, name) -> new CommaSeparatedStorage(s, "tarta/" + name));
        try {
            miscStorage = storage.getStorage("_misc");
        } catch (IOException e) {
            throw new RuntimeException("Could not load _misc, reason:\n" + e);
        }
    }

    public void start() {
        setupWebServer(routes);
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
        for (Handler handler: handlers) {
            handler.setFormatter(formatter);
        }
    }

    private static void setupWebServer(HttpHandler routes) {
        Undertow server = Undertow.builder()
                .addHttpListener(3000, "localhost")
                .setHandler(routes)
                .build();
        server.start();
    }
}
/*
    SCRAP YARD:
    // Read the rest of the body... Wrap inside: BlockingHandler
    var body = new String(req.getInputStream().readAllBytes());
    System.out.println(body);
*/