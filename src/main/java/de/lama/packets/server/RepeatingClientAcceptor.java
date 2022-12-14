package de.lama.packets.server;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.ExceptionHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

class RepeatingClientAcceptor extends AbstractRepeatingOperation {

    private final ServerSocket socket;
    private final Function<Socket, Boolean> clientRegister;
    private final ExceptionHandler exceptionHandler;

    RepeatingClientAcceptor(ServerSocket socket, Function<Socket, Boolean> clientRegister, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.clientRegister = clientRegister;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        while (true) {
            Socket accept = this.exceptionHandler.operate(this.socket::accept, "Could not accept client");
            if (!this.clientRegister.apply(accept)) break;
        }
        return this;
    }
}
