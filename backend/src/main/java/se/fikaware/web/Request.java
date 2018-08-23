package se.fikaware.web;

import io.undertow.server.handlers.form.FormData;
import se.fikaware.misc.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;

@EverythingIsNonnullByDefault
public class Request {
    public static @Nullable String getString(FormData form, String value, @Nullable String defaultValue) {
        var formValue = form.getFirst(value);
        if (formValue == null) {
            return defaultValue;
        } else {
            return formValue.getValue();
        }
    }
}
