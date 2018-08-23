package se.fikaware.tarta.pages;

import io.undertow.server.HttpServerExchange;
import se.fikaware.tarta.models.School;
import se.fikaware.web.Response;

public class AdminPage {

    public static void schoolCreate(HttpServerExchange exchange) {
        var schoolName = exchange.getQueryParameters().get("name").getFirst();
        System.out.println("School Name: " + schoolName);
        School school = School.create(schoolName);
        Response.ok(exchange);
    }

    public static void schoolList(HttpServerExchange exchange) {
        var schools = School.getAll();
        for (var school : schools) {
            System.out.println("Name: " + school.schoolName);
        }
        Response.json(exchange, schools);
    }
}
