package de.lama.packets.server;

import de.lama.packets.util.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class ServerBuilder {

    private static final int PORT = 4999;
    private static final int TICKRATE = (int) Math.pow(2, 4);
    private static final int TICKRATE_LIMIT = 1000;

    private int port = PORT;
    private int tickrate = TICKRATE;
    private ExceptionHandler exceptionHandler;

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(this.port);
    }

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder tickrate(int tickrate) {
        if (tickrate > TICKRATE_LIMIT) throw new IllegalArgumentException("Cannot have tickrate higher than " + TICKRATE_LIMIT);
        this.tickrate = tickrate;
        return this;
    }

    public ServerBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketServer build() throws IOException {
        return new LinkedClientServer(this.createSocket(), this.tickrate, Objects.requireNonNull(this.exceptionHandler));
    }
}
