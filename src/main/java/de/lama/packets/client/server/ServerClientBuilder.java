package de.lama.packets.client.server;

import de.lama.packets.AbstractPacketComponentBuilder;
import de.lama.packets.client.Client;
import de.lama.packets.server.PacketServer;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;
import java.util.Objects;

public class ServerClientBuilder extends AbstractPacketComponentBuilder {

    private PacketServer server;

    public ServerClientBuilder server(PacketServer server) {
        this.server = server;
        return this;
    }

    @Override
    public ServerClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        return (ServerClientBuilder) super.exceptionHandler(exceptionHandler);
    }

    public Client build(Socket socket) {
        Objects.requireNonNull(this.server);
        return new ServerGsonClient(Objects.requireNonNull(socket), Objects.requireNonNull(this.server.getRegistry()),
                this.server.getTickrate(), Objects.requireNonNull(this.exceptionHandler));
    }
}
