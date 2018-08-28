package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.Group;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.models.User;
import se.fikaware.web.BadRequest;
import se.fikaware.web.Response;

public class AdminPage {

    public static void schoolCreate(HttpServerExchange exchange) {
        var schoolName = exchange.getQueryParameters().get("name").getFirst();
        System.out.println("School Name: " + schoolName);
        School.create(schoolName);
        Response.ok(exchange);
    }

    public static void schoolList(HttpServerExchange exchange) {
        var schools = School.getAll();
        for (var school: schools) {
            System.out.println("Name: " + school.schoolName);
        }
        Response.json(exchange, schools);
    }

    public static void userList(HttpServerExchange exchange) {
        var users = User.getAll();
        Response.json(exchange, users);
    }

    public static void userCreate(User user, HttpServerExchange exchange) {
        var username = exchange.getQueryParameters().get("username").getFirst();
        var password = exchange.getQueryParameters().get("password").getFirst();
        User.create(username, password, user.school[0]);
        Response.ok(exchange);
    }

    public static void schoolGet(User user, HttpServerExchange exchange) {
        var school = School.load(exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!");
        }
        Response.json(exchange, school);
    }

    public static void schoolDelete(User user, HttpServerExchange exchange) {
        var school = School.load(exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        school.delete();
        Response.ok(exchange);
    }

    public static void schoolGroupList(User user, HttpServerExchange exchange) {
        var school = School.load(exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        Response.json(exchange, Group.getAll(school));
    }

    public static void schoolGroupCreate(User user, HttpServerExchange exchange) {
        var school = School.load(exchange.getQueryParameters().get("slugName").getFirst());
        if (school == null) {
            throw new BadRequest("No school exists with that slug name!\"");
        }
        var groupName = exchange.getQueryParameters().get("groupName").getFirst();
        Group.create(school, groupName);
        Response.ok(exchange);
    }
}
