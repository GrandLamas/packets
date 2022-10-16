package de.lama.packets.server;

import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;

public class ServerCloseOperation extends AbstractThreadedOperation {

    private final LinkedClientServer server;

    ServerCloseOperation(LinkedClientServer server) {
        this.server = server;
    }

    @Override
    public Operation complete() {
        this.server.setOpen(false);
        return this;
    }
}
