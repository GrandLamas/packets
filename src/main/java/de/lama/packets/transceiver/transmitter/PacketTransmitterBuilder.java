package de.lama.packets.transceiver.transmitter;

import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PacketTransmitterBuilder {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(10);

    private ScheduledExecutorService pool = SERVICE;
    private int tickrate = 16;
    private ExceptionHandler exceptionHandler;
    private PacketWrapper packetWrapper;

    public PacketTransmitterBuilder tickrate(int tickrate) {
        this.tickrate = tickrate;
        return this;
    }

    public PacketTransmitterBuilder threadPool(ScheduledExecutorService pool) {
        this.pool = pool;
        return this;
    }

    public PacketTransmitterBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketTransmitterBuilder packetWrapper(PacketWrapper packetWrapper) {
        this.packetWrapper = packetWrapper;
        return this;
    }

    public PacketTransmitter build(OutputStream out) {
        return new ScheduledPacketQueue(Objects.requireNonNull(out), this.tickrate, Objects.requireNonNull(this.pool),
                Objects.requireNonNull(this.exceptionHandler), Objects.requireNonNull(this.packetWrapper));
    }
}
