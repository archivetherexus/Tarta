package se.fikaware.web;

import se.fikaware.misc.EverythingIsNonnullByDefault;

@EverythingIsNonnullByDefault
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
