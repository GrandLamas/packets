package de.lama.packets.server;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;

public class ServerCloseOperation extends AbstractSimpleThreadedOperation {

    private final LinkedClientServer server;

    ServerCloseOperation(LinkedClientServer server) {
        this.server = server;
    }

    @Override
    public void complete() {
        this.server.setOpen(false);
    }
}
