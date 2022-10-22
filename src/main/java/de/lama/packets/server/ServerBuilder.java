package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.function.Supplier;

public class ServerBuilder {

    private static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;

    private int port;
    private ClientBuilder clientBuilder;
    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;

    public ServerBuilder() {
        this.port = Packet.PORT;
    }

    private PacketRegistry buildRegistry() {
        return Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
    }

    private ClientBuilder buildClientBuilder() {
        return Objects.requireNonNullElseGet(this.clientBuilder, ClientBuilder::new).clone();
    }

    private ExceptionHandler buildExceptionHandler() {
        return Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace);
    }

    private ServerSocket createSocket() throws IOException {
        return new ServerSocket(this.port);
    }

    public ServerBuilder registry(PacketRegistry registry) {
        this.registry = registry;
        return this;
    }

    public ServerBuilder clients(ClientBuilder clientFactory) {
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

    public Server build() throws IOException {
        ExceptionHandler exceptionHandler = this.buildExceptionHandler();
        PacketRegistry registry = this.buildRegistry();
        ClientBuilder clientBuilder = this.buildClientBuilder().exceptionHandler(exceptionHandler).registry(registry);
        return new UniqueSocketServer(this.createSocket(), clientBuilder, registry, exceptionHandler);
    }
}
