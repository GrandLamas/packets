package de.lama.packets.client.transceiver.transmitter;

import de.lama.packets.client.transceiver.AbstractTransceiverBuilder;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class PacketTransmitterBuilder extends AbstractTransceiverBuilder {

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

    public PacketTransmitter build(OutputStream out) {
        return new ScheduledPacketTransmitter(Objects.requireNonNull(out), this.tickrate, Objects.requireNonNull(this.pool),
                Objects.requireNonNull(this.exceptionHandler));
    }
}
