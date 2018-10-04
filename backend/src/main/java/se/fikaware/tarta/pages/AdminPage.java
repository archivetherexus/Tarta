package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Course;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.models.User;
import se.fikaware.web.BadRequest;
import se.fikaware.web.Response;
import se.fikaware.web.SendableIterator;
import se.fikaware.web.Server;

import java.io.IOException;

public class AdminPage {

    public static void schoolCreate(User user, HttpServerExchange exchange) throws IOException {
        var schoolName = exchange.getQueryParameters().get("name").getFirst();
        System.out.println("School Name: " + schoolName);
        new School(schoolName);
        Response.ok(exchange);
    }

    public static void schoolList(User user, HttpServerExchange exchange) {
        var schools = Server.getInstance().miscStorage.getAll(School.class);
        schools.forEach(s -> System.out.println("Name: " + s.schoolName));
        Response.json(exchange, new SendableIterator<>(schools.iterator()));
    }

    public static void userList(User user, HttpServerExchange exchange) {
        Response.json(exchange, new SendableIterator<>(Server.getInstance().miscStorage.getAll(User.class).iterator()));
    }

    public static void userCreate(User user, HttpServerExchange exchange) throws IOException {
        var username = exchange.getQueryParameters().get("username").getFirst();
        var password = exchange.getQueryParameters().get("password").getFirst();
        new User(user.schools.get(0), username, password);
        Response.ok(exchange);
    }

    public static void schoolGet(User user, HttpServerExchange exchange) {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!");
        }
        Response.json(exchange, school);
    }

    public static void schoolDelete(User user, HttpServerExchange exchange) throws IOException {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        school.delete();
        Response.ok(exchange);
    }

    public static void schoolGroupList(User user, HttpServerExchange exchange) {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        Response.json(exchange, new SendableIterator<>(school.schoolStorage.getAll(Group.class).iterator()));
    }

    public static void schoolGroupCreate(User user, HttpServerExchange exchange) throws IOException {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        var groupName = exchange.getQueryParameters().get("groupName").getFirst();
        new Group(school, groupName);
        Response.ok(exchange);
    }

    public static void schoolCourseCreate(User user, HttpServerExchange exchange) throws IOException {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        var courseName = exchange.getQueryParameters().get("courseName").getFirst();
        new Course(school, courseName);
        Response.ok(exchange);
    }

    public static void reset(User user, HttpServerExchange exchange) throws IOException {
        /*var all = new Document();
        Post.postCollection.deleteMany(all);
        School.schoolCollection.deleteMany(all);
        User.userCollection.deleteMany(all);
        Group.groupCollection.deleteMany(all);*/

        var school = new School("Test School");
        var a = new User(school, "adminAC", "a");
        a.isAdmin = true;
        a.save();

        Response.ok(exchange);
    }


    public static void schoolCourseList(User user, HttpServerExchange exchange) {
        var school = Server.getInstance().miscStorage.getObject(School.class, exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        Response.json(exchange, new SendableIterator<>(school.schoolStorage.getAll(Course.class).iterator()));
    }
}
