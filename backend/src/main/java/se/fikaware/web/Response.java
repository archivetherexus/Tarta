package se.fikaware.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.bson.types.ObjectId;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;
import se.fikaware.tarta.models.School;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Response {
    public static <T> void json(HttpServerExchange exchange, T object) {
        try {
            MessagePack msgpack = new MessagePack(); // TODO: Could this be made static and moved out?

            msgpack.register(School.class, new Template<School>() {
                @Override
                public void write(Packer pk, School v) throws IOException {
                    pk.writeArrayBegin(1);
                    pk.write(v.schoolName);
                    pk.writeArrayEnd();
                }

                @Override
                public void write(Packer pk, School v, boolean required) throws IOException {
                    write(pk, v);
                }

                @Override
                public School read(Unpacker u, School to) throws IOException {
                    u.readArrayBegin();
                    var name = u.readString();
                    u.readArrayEnd();
                    return new School(name, new ObjectId());
                }

                @Override
                public School read(Unpacker u, School to, boolean required) throws IOException {
                    return read(u, to);
                }
            });

            ByteArrayOutputStream bf = new ByteArrayOutputStream();
            msgpack.write(bf, object);
            exchange.getResponseSender().send(ByteBuffer.wrap(bf.toByteArray()));
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/msgpack");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
