package de.lama.packets.server;

import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;

public class ServerShutdownOperation extends AbstractThreadedOperation {

    private final LinkedClientServer server;

    ServerShutdownOperation(LinkedClientServer server) {
        this.server = server;
    }

    @Override
    public Operation complete() {
        this.server.close().complete();
        this.server.getExceptionHandler().operate(this.server.getSocket()::close, "Failed to close server properly");
        return this;
    }
}
