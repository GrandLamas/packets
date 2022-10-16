package de.lama.packets.client;

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

public class ClientBuilder {

    private static final int TICKRATE = (int) Math.pow(2, 4); // 16
    static final int TICKRATE_LIMIT = 1000;
    static final PacketRegistry GLOBAL_REGISTRY = new HashedPacketRegistry();
    static final WrapperFactory DEFAULT_WRAPPER = new GsonFactory();

    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;
    private WrapperFactory wrapperFactory;
    private int tickrate;

    public ClientBuilder() {
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

    public Client build(SocketBuilder builder) throws IOException {
        Socket socket = builder.build();
        Client client = this.buildVirtual(socket);
        client.getEventHandler().subscribe(PacketReceiveEvent.class, new ClientHandshakeListener(client));
        return client;
    }

    public Client buildVirtual(Socket socket) {
        PacketRegistry registry = Objects.requireNonNullElse(this.registry, GLOBAL_REGISTRY);
        PacketWrapper wrapper = Objects.requireNonNullElse(this.wrapperFactory, DEFAULT_WRAPPER).create(registry);
        return new ThreadedClient(socket, registry, wrapper,
                this.tickrate, Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace));
    }

    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    public PacketRegistry registry() {
        return this.registry;
    }

    public WrapperFactory wrapperFactory() {
        return this.wrapperFactory;
    }

    public int tickrate() {
        return this.tickrate;
    }
}
