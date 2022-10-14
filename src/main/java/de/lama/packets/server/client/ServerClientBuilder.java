package de.lama.packets.server.client;

import de.lama.packets.server.PacketServer;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;
import java.util.Objects;

public class ServerClientBuilder {

    private ExceptionHandler exceptionHandler;
    private PacketServer server;

    public ServerClientBuilder server(PacketServer server) {
        this.server = server;
        return this;
    }

    public ServerClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public ServerClient build(Socket socket) {
        return new ServerGsonClient(Objects.requireNonNull(this.server), Objects.requireNonNull(socket), Objects.requireNonNull(this.exceptionHandler));
    }
}
