package se.fikaware.tarta.models.syncers;

import org.bson.types.ObjectId;
import se.fikaware.sync.IObjectSyncer;
import se.fikaware.sync.IWriter;
import se.fikaware.sync.SyncerFor;

import java.io.IOException;

@SuppressWarnings("unused")
@SyncerFor(type = ObjectId.class)
public class ObjectIdSyncer implements IObjectSyncer {
    @Override
    public void write(Object o, IWriter i) throws IOException {
        i.writeString(((ObjectId)o).toString());
    }
}
