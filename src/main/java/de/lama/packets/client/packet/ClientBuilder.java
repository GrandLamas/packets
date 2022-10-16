package de.lama.packets.client.packet;

import de.lama.packets.AbstractPacketComponentBuilder;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ClientBuilder extends AbstractPacketComponentBuilder {

    private static final String LOCALHOST = "localhost";

    private String address;

    private Socket createSocket() throws IOException {
        return new Socket(Objects.requireNonNull(this.address), this.port);
    }

    public ClientBuilder localhost() {
        return this.address(LOCALHOST);
    }

    public ClientBuilder address(String address) {
        this.address = address;
        return this;
    }

    public ClientBuilder port(int port) {
        return (ClientBuilder) super.port(port);
    }

    @Override
    public ClientBuilder registry(PacketRegistry registry) {
        return (ClientBuilder) super.registry(registry);
    }

    @Override
    public ClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        return (ClientBuilder) super.exceptionHandler(exceptionHandler);
    }

    @Override
    public ClientBuilder tickrate(int tickrate) {
        return (ClientBuilder) super.tickrate(tickrate);
    }

    public PacketClient build() throws IOException {
        return new GsonClient(this.createSocket(), Objects.requireNonNull(this.registry),
                this.tickrate, Objects.requireNonNull(this.exceptionHandler));
    }
}
