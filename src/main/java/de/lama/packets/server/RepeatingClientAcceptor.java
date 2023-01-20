package de.lama.packets.server;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.function.Function;

public class RepeatingClientAcceptor extends AbstractRepeatingOperation {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ServerSocket socket;
    private final Function<Socket, Boolean> clientRegister;
    private final ExceptionHandler exceptionHandler;

    public RepeatingClientAcceptor(ServerSocket socket, Function<Socket, Boolean> clientRegister, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.clientRegister = clientRegister;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation queue() {
        this.executor.submit(this::complete);
        return this;
    }

    @Override
    public Operation complete() {
        Socket accept = this.exceptionHandler.operate(this.socket::accept, "Could not accept client");
        this.clientRegister.apply(accept);
        return this;
    }

    @Override
    protected Future<?> createRepeatingTask() {
        return this.executor.submit(() -> {
            while (!this.socket.isClosed()) {
                this.complete();
            }
        });
    }
}
