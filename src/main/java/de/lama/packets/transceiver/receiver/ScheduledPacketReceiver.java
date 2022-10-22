package de.lama.packets.transceiver.receiver;

import de.lama.packets.io.BufferedPacketInputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.transceiver.IoTransceivablePacket;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.util.ExceptionHandler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final BufferedPacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private final PacketConsumer consumer;
    private TransceivablePacket last;

    ScheduledPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler, PacketConsumer consumer) {
        super(pool, tickrate);
        this.consumer = consumer;
        this.in = new BufferedPacketInputStream(new DataInputStream(new BufferedInputStream(inputStream)));
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected void tick() {
        this.exceptionHandler.operate(() -> {
            while (this.in.available() > 0) {
                TransceivablePacket ioPacket = new IoTransceivablePacket(this.in.readPacket());
                this.last = ioPacket;
                synchronized (this) {
                    this.notifyAll();
                }
                this.consumer.accept(ioPacket);
            }
        }, "Failed to read packet");
    }

    @Override
    public TransceivablePacket awaitPacket(long timeoutInMillis) {
        synchronized (this) {this.exceptionHandler.operate(() -> this.wait(timeoutInMillis), "Could not wait");}
        return this.last;
    }
}
