package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.School;
import se.fikaware.tarta.models.User;
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
        for (var school : schools) {
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
}
