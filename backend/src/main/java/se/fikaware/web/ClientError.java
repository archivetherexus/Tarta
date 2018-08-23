package se.fikaware.web;

import se.fikaware.misc.EverythingIsNonnullByDefault;

@EverythingIsNonnullByDefault
public class ClientError extends RuntimeException {
    private String message;

    public ClientError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
