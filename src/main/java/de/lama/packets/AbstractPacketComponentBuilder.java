package de.lama.packets;

import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

public class AbstractPacketComponentBuilder {

    private static final int PORT = 4999;
    private static final int TICKRATE = (int) Math.pow(2, 4); // 16
    private static final int TICKRATE_LIMIT = 1000;

    protected int port;
    protected int tickrate;
    protected ExceptionHandler exceptionHandler;
    protected PacketRegistry registry;

    public AbstractPacketComponentBuilder() {
        this.port = PORT;
        this.tickrate = TICKRATE;
        this.exceptionHandler = Exception::printStackTrace;
        this.registry = new HashedPacketRegistry();
    }

    public AbstractPacketComponentBuilder registry(PacketRegistry registry) {
        this.registry = registry;
        return this;
    }

    public AbstractPacketComponentBuilder port(int port) {
        this.port = port;
        return this;
    }

    public AbstractPacketComponentBuilder tickrate(int tickrate) {
        if (tickrate > TICKRATE_LIMIT) throw new IllegalArgumentException("Cannot have tickrate higher than " + TICKRATE_LIMIT);
        this.tickrate = tickrate;
        return this;
    }

    public AbstractPacketComponentBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }
}
