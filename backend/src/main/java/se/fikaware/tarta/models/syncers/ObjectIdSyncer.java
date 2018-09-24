package se.fikaware.tarta.models.syncers;

import org.bson.types.ObjectId;
import se.fikaware.persistent.ExtendedDataWriter;
import se.fikaware.sync.ObjectSyncer;
import se.fikaware.sync.SyncerFor;

import java.io.IOException;

@SuppressWarnings("unused")
@SyncerFor(type = ObjectId.class)
public class ObjectIdSyncer implements ObjectSyncer {
    @Override
    public void write(Object o, ExtendedDataWriter i) throws IOException {
        i.writeString(((ObjectId)o).toString());
    }
}
