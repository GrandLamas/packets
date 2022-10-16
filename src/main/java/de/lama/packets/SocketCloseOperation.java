package de.lama.packets;

import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class SocketCloseOperation extends AbstractThreadedOperation {

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
