package se.fikaware.web;

public class BadRequest extends RuntimeException {
    private String message;

    public BadRequest(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
