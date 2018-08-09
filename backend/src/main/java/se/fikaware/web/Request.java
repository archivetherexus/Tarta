package se.fikaware.web;

import io.undertow.server.handlers.form.FormData;

public class Request {
    public static String getString(FormData form, String value, String defaultValue) {
        var formValue = form.getFirst(value);
        if (formValue == null) {
            return defaultValue;
        } else {
            return formValue.getValue();
        }
    }
}
