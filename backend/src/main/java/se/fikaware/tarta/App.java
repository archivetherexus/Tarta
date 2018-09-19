package se.fikaware.tarta;

import com.mongodb.MongoClient;
import se.fikaware.persistent.PersistentObject;
import se.fikaware.persistent.PersistentReader;
import se.fikaware.persistent.PersistentStorage;
import se.fikaware.tarta.pages.AdminPage;
import se.fikaware.tarta.pages.GroupPage;
import se.fikaware.tarta.pages.PostsPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Handlers;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static class TestObject extends PersistentObject {
        public TestObject(PersistentStorage owner, PersistentReader r) {
            super(owner);
        }

        public void bark() {
            System.out.println("Woof woof!");
        }
    }

    public static void main(final String[] args) {

        PersistentStorage storage = new PersistentStorage("test-school");
        storage.loadAll(TestObject.class).forEach(TestObject::bark);

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
                .get("/admin/school/get", Handlers.withAdmin(AdminPage::schoolGet))
                .get("/admin/school/delete", Handlers.withAdmin(AdminPage::schoolDelete))
                .get("/admin/school/group/list", Handlers.withAdmin(AdminPage::schoolGroupList))
                .get("/admin/school/group/create", Handlers.withAdmin(AdminPage::schoolGroupCreate))
                .get("/admin/school/course/create", Handlers.withAdmin(AdminPage::schoolCourseCreate))
                .get("/admin/school/course/list", Handlers.withAdmin(AdminPage::schoolCourseList))
                .get("/admin/user/create", Handlers.withAdmin(AdminPage::userCreate))
                .get("/admin/user/list", AdminPage::userList)
                .get("/group/list/get", Handlers.withUser(GroupPage::listGet))
                .get("/reset", Handlers.withAdmin(AdminPage::reset))
                .start();
    }
}
