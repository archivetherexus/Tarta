package se.fikaware.web;

import se.fikaware.persistent.ExtendedDataWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

// TODO: Move this into the extended writer instead. Under a writeObject or something...
public class SendableMap<T> implements Sendable {
    private final Map<String, T> map;

    public SendableMap(Map<String, T> map) {
        this.map = map;
    }

    @Override
    public void send(ExtendedDataWriter writer) throws IOException {
        writer.writeMapBegin();
        Iterator<Map.Entry<String, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, T> set = iterator.next();
            writer.writeMapKey(set.getKey());
            Object value = set.getValue();
            if (value instanceof Sendable) {
                ((Sendable)value).send(writer);
            } else if (value instanceof String) {
                writer.writeString((String) value);
            } else {
                throw new RuntimeException("Tried to send unsupported type.");
            }
            if (iterator.hasNext()) {
                writer.writeMapNext();
            }
        }
        writer.writeMapEnd();
    }
}
