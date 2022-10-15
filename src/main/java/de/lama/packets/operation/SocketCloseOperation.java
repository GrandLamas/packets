package de.lama.packets.operation;

import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class SocketCloseOperation extends AbstractSimpleThreadedOperation {

    private final Socket socket;
    private final ExceptionHandler exceptionHandler;

    public SocketCloseOperation(Socket socket, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        this.exceptionHandler.operate(this.socket::close, "Could not close socket");
        return this;
    }
}
