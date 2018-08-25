package se.fikaware.tarta;

import com.mongodb.MongoClient;
import se.fikaware.sync.Syncer;
import se.fikaware.sync.json.JsonWriter;
import se.fikaware.tarta.models.Post;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.pages.AdminPage;
import se.fikaware.tarta.pages.PostsPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Handlers;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App {
    public static void main(final String[] args) {
        var s = new Syncer();
        /*var i = new IWriter() {
            @Override
            public void writeNull() {
                System.out.println("Null");
            }

            @Override
            public void writeInteger(int i) {
                System.out.println("Write integer: " + i);
            }

            @Override
            public void writeString(String s) {
                System.out.println("Write string: " + s);
            }

            @Override
            public void writeArrayBegin() {
                System.out.println("[");
            }

            @Override
            public void writeArrayNext() {
                System.out.println("-------");
            }

            @Override
            public void writeArrayEnd() {
                System.out.println("]");
            }

            @Override
            public void writeMapBegin() {
                System.out.println("{");
            }

            @Override
            public void writeMapKey(String keyName) {
                System.out.println(keyName + ": ");
            }

            @Override
            public void writeMapNext() throws IOException {
                System.out.println("-------");
            }

            @Override
            public void writeMapEnd() {
                System.out.println("}");
            }
        };*/
        var arr1 = new LinkedList<Post>();
        arr1.add(new Post(new School(), "Title1", "Content1"));
        arr1.add(new Post(new School(), "Title2", "Content2"));
        arr1.add(null);
        try {
            //s.write(i, arr1);
            s.write(new JsonWriter(System.out), arr1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Server(() -> new MongoClient().getDatabase("tarta-dev"))
                .get("/test", req -> {
                    req.getQueryParameters().get("hello");
                    List<String> list = new ArrayList<>();
                    list.add("Hello");
                    list.add("World");
                    list.add("Ok.");

                    Response.json(req, list);
                })
                .post("/user/login", UserPage::login)
                .get("/user/settings/get", UserPage::settingsGet)
                .get("/user/settings/set", UserPage::settingsSet)
                .get("/user/name", Handlers.withUser(UserPage::name))
                .get("/posts/feed/get", Handlers.withUser(PostsPage::feedGet))
                .post("/posts/create", Handlers.withAdmin(PostsPage::create))
                .get("/admin/school/create", AdminPage::schoolCreate)
                .get("/admin/school/list", AdminPage::schoolList)
                .get("/admin/user/create", AdminPage::userCreate)
                .get("/admin/user/list", AdminPage::userList)
                .start();
    }
}
