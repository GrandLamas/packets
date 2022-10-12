package de.lama.packets.server;

import de.lama.packets.server.exception.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.function.Consumer;

public class ServerBuilder {

    private int port = 4999;
    private int tickrate = 16;
    private Consumer<ServerException> exceptionHandler;

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(this.port);
    }

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder tickrate(int tickrate) {
        this.tickrate = tickrate;
        return this;
    }

    public ServerBuilder exceptionHandler(Consumer<ServerException> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketServer build() throws IOException {
        return new PacketServerImpl(this.createSocket(), this.tickrate, Objects.requireNonNull(this.exceptionHandler));
    }
}
