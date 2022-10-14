package de.lama.packets.server;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;
import de.lama.packets.util.ExceptionUtils;

public class ServerShutdownOperation extends AbstractSimpleThreadedOperation {

    private final LinkedClientServer server;

    ServerShutdownOperation(LinkedClientServer server) {
        this.server = server;
    }

    @Override
    public void complete() {
        this.server.close().complete();
        ExceptionUtils.operate(this.server.getExceptionHandler(), this.server.getSocket()::close, "Failed to close server properly");
    }
}
