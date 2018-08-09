package se.fikaware.tarta;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.util.AttachmentKey;
import javassist.bytecode.analysis.ControlFlow;
import se.fikaware.tarta.pages.FeedPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(final String[] args) {
        Server.start(getRoutes());
    }

    public static HttpHandler getRoutes() {
        return new RoutingHandler()
                .get("/test", req -> {
                    req.getQueryParameters().get("hello");
                    List list = new ArrayList<String>();
                    list.add("Hello");
                    list.add("World");
                    list.add("Ok.");

                    Response.json(req, list);
                })
                .post("/user/login", new EagerFormParsingHandler(UserPage::Login))
                .get("/user/settings/get", UserPage::SettingsGet)
                .get("/user/settings/set", UserPage::SettingsSet)
                .get("/feed/get", FeedPage::Get)
                .post("/feed/post", FeedPage::Post);
    }
}



/*
// Read the rest of the body... Wrap inside: BlockingHandler
var body = new String(req.getInputStream().readAllBytes());
System.out.println(body);

*/