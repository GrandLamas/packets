package de.lama.packets.client;

import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.GsonFactory;
import de.lama.packets.wrapper.PacketWrapper;
import de.lama.packets.wrapper.WrapperFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Supplier;

public class ClientBuilder implements Cloneable {

    private static final int TICKRATE = (int) Math.pow(2, 4); // 16
    static final int TICKRATE_LIMIT = 1000;
    static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;
    static final Supplier<WrapperFactory> DEFAULT_WRAPPER = GsonFactory::new;

    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;
    private WrapperFactory wrapperFactory;
    private int tickrate;

    public ClientBuilder() {
        this.tickrate = TICKRATE;
    }

    private PacketRegistry buildRegistry() {
        return Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
    }

    private PacketWrapper buildWrapper(PacketRegistry registry) {
        return Objects.requireNonNullElseGet(this.wrapperFactory, DEFAULT_WRAPPER).create(registry);
    }

    private ExceptionHandler buildHandler() {
        return Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace);
    }

    public ClientBuilder registry(PacketRegistry registry) {
        this.registry = registry;
        return this;
    }

    public ClientBuilder wrapper(WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
        return this;
    }

    public ClientBuilder tickrate(int tickrate) {
        if (tickrate > TICKRATE_LIMIT) throw new IllegalArgumentException("Cannot use tickrate higher than " + TICKRATE_LIMIT);
        this.tickrate = tickrate;
        return this;
    }

    public ClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Client buildVirtual(Socket socket) {
        PacketRegistry registry = this.buildRegistry();
        return new ThreadedClient(socket, registry, this.buildWrapper(registry), this.tickrate, this.buildHandler());
    }

    public Client build(Socket socket) {
        PacketRegistry registry = this.buildRegistry();
        return new HandshakeClient(socket, registry, this.buildWrapper(registry), this.tickrate, this.buildHandler());
    }

    public Client build(String address, int port) throws IOException {
        return this.build(new Socket(address, port));
    }

    @Override
    public ClientBuilder clone() {
        try {
            ClientBuilder clone = (ClientBuilder) super.clone();
            clone.exceptionHandler = this.exceptionHandler;
            clone.registry = this.registry;
            clone.tickrate = this.tickrate;
            clone.wrapperFactory = this.wrapperFactory;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
