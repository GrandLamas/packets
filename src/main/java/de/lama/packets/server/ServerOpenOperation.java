package de.lama.packets.server;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;
import de.lama.packets.server.exception.ServerException;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ServerOpenOperation extends AbstractSimpleThreadedOperation {

    private static final ExecutorService REGISTRATIONS = Executors.newCachedThreadPool();

    private final LinkedClientServer server;

    ServerOpenOperation(LinkedClientServer server) {
        this.server = server;
    }

    @Override
    public void complete() {
        try {
            this.server.setOpen(true);
            while (this.server.isOpen()) {
                Socket accepted;
                if ((accepted = this.server.getSocket().accept()) != null && this.server.isOpen()) {
                    REGISTRATIONS.submit(() -> this.server.register(accepted));
                }
            }
        } catch (IOException e) {
            if (this.server.isOpen()) {
                this.server.getExceptionHandler().accept(new ServerException("Could not connect with client", e));
            }
        }
        this.server.setOpen(false);
    }
}
