package de.lama.packets.server;

import de.lama.packets.AbstractPacketComponentBuilder;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class ServerBuilder extends AbstractPacketComponentBuilder {

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(this.port);
    }

    @Override
    public ServerBuilder tickrate(int tickrate) {
        return (ServerBuilder) super.tickrate(tickrate);
    }

    @Override
    public ServerBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        return (ServerBuilder) super.exceptionHandler(exceptionHandler);
    }

    @Override
    public ServerBuilder port(int port) {
        return (ServerBuilder) super.port(port);
    }

    @Override
    public ServerBuilder registry(PacketRegistry registry) {
        return (ServerBuilder) super.registry(registry);
    }

    public PacketServer build() throws IOException {
        return new LinkedClientServer(this.createSocket(), this.tickrate, Objects.requireNonNull(this.registry), Objects.requireNonNull(this.exceptionHandler));
    }
}
