package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.util.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class ServerBuilder {

    private int port;
    private ClientBuilder clientBuilder;
    private ExceptionHandler exceptionHandler;

    public ServerBuilder() {
        this.port = Packet.PORT;
    }

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(this.port);
    }

    public ServerBuilder clientBuilder(ClientBuilder clientFactory) {
        this.clientBuilder = clientFactory;
        return this;
    }

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketServer build() throws IOException {
        return new LinkedClientServer(this.createSocket(), Objects.requireNonNullElseGet(this.clientBuilder, ClientBuilder::new),
                Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace));
    }
}
