package de.lama.packets.server.client;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;

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
        ExceptionUtils.operate(this.exceptionHandler, this.socket::close, "Could not close socket");
        return this;
    }
}
