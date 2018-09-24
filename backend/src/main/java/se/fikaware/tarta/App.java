package se.fikaware.tarta;

import com.mongodb.MongoClient;
import se.fikaware.persistent.*;
import se.fikaware.tarta.pages.AdminPage;
import se.fikaware.tarta.pages.GroupPage;
import se.fikaware.tarta.pages.PostsPage;
import se.fikaware.tarta.pages.UserPage;
import se.fikaware.web.Handlers;
import se.fikaware.web.Response;
import se.fikaware.web.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class App {

    public static class TestObject extends PersistentObject {
        String name;
        int age;
        boolean isCat;

        public TestObject(DataStorage owner, DataReader r) {
            super(owner);
            name = r.readString();
            age = r.readInt();
            isCat = r.readBoolean();
        }

        public TestObject(CommaSeparatedStorage owner, String name, int age, boolean isCat) {
            super(owner);
            this.name = name;
            this.age = age;
            this.isCat = isCat;
        }

        @Override
        protected void write(DataWriter writer) throws IOException {
            writer.writeString(name);
            writer.writeInt(age);
            writer.writeBoolean(isCat);
        }

        public void bark() {
            System.out.println(
                    (isCat ? "Nyaa! " : "Woof woof! ") +
                            "my name is: " +
                            name +
                            " and I am " + age +
                            " years old!"
            );
        }
    }

    public static void main(final String[] args) {
        try {
            CommaSeparatedStorage storage = new CommaSeparatedStorage("test-school");

            System.out.println("--- Round 1 ---");
            storage.getAll(TestObject.class).forEach(TestObject::bark);

            System.out.println("--- Round 2 ---");
            storage.getAll(TestObject.class).forEach(TestObject::bark);

            TestObject obj = new TestObject(storage, "Momo" + Calendar.getInstance().getTimeInMillis(), 21, true);

                obj.save();


            System.out.println("--- Round 3 ---");
            storage.getAll(TestObject.class).forEach(TestObject::bark);
            storage.getObject(TestObject.class, "Shibuya").bark();
            TestObject o = storage.getObject(TestObject.class, "Test");
            if (o != null) {
                o.bark();
            } else {
                System.out.println("Loaded null!");
            }

            obj.save();
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
