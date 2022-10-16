package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.client.ClientHandshakeListener;
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
    static final int PORT = Packet.PORT;
    static final String LOCALHOST = "localhost";
    static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;
    static final Supplier<WrapperFactory> DEFAULT_WRAPPER = GsonFactory::new;

    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;
    private WrapperFactory wrapperFactory;
    private int tickrate;

    public ClientBuilder() {
        this.registry = DEFAULT_REGISTRY.get();
        this.wrapperFactory = DEFAULT_WRAPPER.get();
        this.tickrate = TICKRATE;
    }

    public ClientBuilder registry(PacketRegistry registry) {
        this.registry = registry;
        return this;
    }

    public ClientBuilder wrapperFactory(WrapperFactory wrapperFactory) {
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
        PacketRegistry registry = Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
        PacketWrapper wrapper = Objects.requireNonNullElseGet(this.wrapperFactory, DEFAULT_WRAPPER).create(registry);
        return new ThreadedClient(socket, registry, wrapper,
                this.tickrate, Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace));
    }

    public Client build(Socket socket) {
        Client built = this.buildVirtual(socket);
        built.getEventHandler().subscribe(PacketReceiveEvent.class, new ClientHandshakeListener(built));
        return built;
    }

    public Client build(String address, int port) throws IOException {
        return this.build(new Socket(address, port));
    }

    public Client build() throws IOException {
        return this.build(LOCALHOST, PORT);
    }

    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    public PacketRegistry registry() {
        return this.registry;
    }

    public int tickrate() {
        return this.tickrate;
    }

    public WrapperFactory wrapperFactory() {
        return this.wrapperFactory;
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
