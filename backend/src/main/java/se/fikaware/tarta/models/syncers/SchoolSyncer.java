package se.fikaware.tarta.models.syncers;

import se.fikaware.persistent.ExtendedDataWriter;
import se.fikaware.sync.ObjectSyncer;
import se.fikaware.tarta.models.School;

import java.io.IOException;

public class SchoolSyncer implements ObjectSyncer {
    @Override
    public void write(Object o, ExtendedDataWriter i) throws IOException {
        var school = (School)o;
        i.writeMapBegin();
        i.writeMapKey("name");
        i.writeString(school.schoolName);
        i.writeMapNext();
        i.writeMapKey("slugName");
        i.writeString(school.slugName);
        i.writeMapNext();
        i.writeMapKey("id");
        i.writeString(school.reference.toString());
        i.writeMapEnd();
    }
}
