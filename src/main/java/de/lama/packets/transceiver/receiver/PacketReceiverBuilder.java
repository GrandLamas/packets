package de.lama.packets.transceiver.receiver;

import de.lama.packets.transceiver.AbstractTransceiverBuilder;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class PacketReceiverBuilder extends AbstractTransceiverBuilder {

    public PacketReceiverBuilder tickrate(int tickrate) {
        this.tickrate = tickrate;
        return this;
    }

    public PacketReceiverBuilder threadPool(ScheduledExecutorService pool) {
        this.pool = pool;
        return this;
    }

    public PacketReceiverBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketReceiver build(InputStream in) {
        return new ScheduledPacketReceiver(Objects.requireNonNull(in), this.tickrate, Objects.requireNonNull(this.pool),
                Objects.requireNonNull(this.exceptionHandler));
    }
}
