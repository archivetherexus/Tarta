package se.fikaware.tarta;

import com.mongodb.MongoClient;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

import io.undertow.server.handlers.form.EagerFormParsingHandler;
import se.fikaware.tarta.pages.AdminPage;
import se.fikaware.tarta.pages.PostsPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Handlers;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(final String[] args) {
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
                .post("/user/login", new EagerFormParsingHandler(UserPage::login))
                .get("/user/settings/get", UserPage::settingsGet)
                .get("/user/settings/set", UserPage::settingsSet)
                .get("/user/name", Handlers.withUser(UserPage::name))
                .get("/posts/feed/get", PostsPage::feedGet)
                .post("/posts/create", new EagerFormParsingHandler(PostsPage::create))
                .get("/admin/school/create", AdminPage::schoolCreate)
                .get("/admin/school/list", AdminPage::schoolList);
    }
}
