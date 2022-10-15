package de.lama.packets.server.client;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class ServerClientCloseOperation extends AbstractSimpleThreadedOperation {

    private final Socket socket;
    private final ExceptionHandler exceptionHandler;

    public ServerClientCloseOperation(Socket socket, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        this.exceptionHandler.operate(this.socket::close, "Could not close socket");
        return this;
    }
}
