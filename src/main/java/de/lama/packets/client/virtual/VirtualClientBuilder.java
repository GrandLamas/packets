package de.lama.packets.client.virtual;

import de.lama.packets.AbstractPacketComponentBuilder;
import de.lama.packets.client.Client;
import de.lama.packets.server.PacketServer;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;
import java.util.Objects;

public class VirtualClientBuilder extends AbstractPacketComponentBuilder {

    private PacketServer server;

    public VirtualClientBuilder server(PacketServer server) {
        this.server = server;
        return this;
    }

    @Override
    public VirtualClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        return (VirtualClientBuilder) super.exceptionHandler(exceptionHandler);
    }

    public Client build(Socket socket) {
        Objects.requireNonNull(this.server);
        return new VirtualGsonClient(Objects.requireNonNull(socket), Objects.requireNonNull(this.server.getRegistry()),
                this.server.getTickrate(), Objects.requireNonNull(this.exceptionHandler));
    }
}
