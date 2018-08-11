package se.fikaware.tarta;

import com.mongodb.MongoClient;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

import io.undertow.server.handlers.form.EagerFormParsingHandler;
import se.fikaware.misc.TinyMap;
import se.fikaware.tarta.pages.FeedPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(final String[] args) {
        var a = new TinyMap<String, String>();
        a.put("Test", "World");
        a.put("Hey", "Now");
        System.out.println(a.get("Test"));

        var iterator = a.entrySet().iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(iterator.hasNext()) {
            var entry = iterator.next();
            System.out.println(entry.getKey() + ":- " + entry.getValue());
        }

        for (String entry : a.keySet()) {
            System.out.println("Key: " + entry);
        }

        for (String entry : a.values()) {
            System.out.println("Values: " + entry);
        }

        Server.start(() -> getRoutes(), () -> new MongoClient().getDatabase("tarta-dev"));
    }

    private static HttpHandler getRoutes() {
        return new RoutingHandler()
                .get("/test", req -> {
                    req.getQueryParameters().get("hello");
                    List<String> list = new ArrayList<>();
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
