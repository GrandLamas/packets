package de.lama.packets.server;

import de.lama.packets.action.AbstractSimpleThreadedOperation;
import de.lama.packets.server.exception.ServerException;

import java.io.IOException;
import java.net.Socket;

public class ServerOpenAction extends AbstractSimpleThreadedOperation {

    private final PacketServerImpl server;

    public ServerOpenAction(PacketServerImpl server) {
        this.server = server;
    }

    @Override
    public void complete() {
        try {
            this.server.setOpen(true);
            while (this.server.isOpen()) {
                Socket accepted;
                if ((accepted = this.server.getSocket().accept()) != null) {
                    this.server.register(accepted);
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
