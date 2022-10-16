package de.lama.packets.transceiver.receiver;

import de.lama.packets.io.DirectPacketInputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.transceiver.IoTransceivablePacket;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.util.ExceptionHandler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

// TODO: Builder with merged tickrate on transmitter
public class ScheduledPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final DirectPacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private final PacketConsumer consumer;
    private TransceivablePacket last;

    public ScheduledPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler, PacketConsumer consumer) {
        super(pool, tickrate);
        this.consumer = consumer;
        this.in = new DirectPacketInputStream(new DataInputStream(new BufferedInputStream(inputStream)));
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected void tick() {
        this.exceptionHandler.operate(() -> {
            while (this.in.available() > 0) {
                TransceivablePacket ioPacket = new IoTransceivablePacket(this.in.readPacket());
                this.last = ioPacket;
                this.consumer.accept(ioPacket);
            }
        }, "Failed to read packet");
    }

    @Override
    public TransceivablePacket awaitPacket(long timeoutInMillis) {
        long over = System.currentTimeMillis() + timeoutInMillis;
        TransceivablePacket last = this.last;
        while (this.last == last) {
            this.exceptionHandler.operate(() -> Thread.sleep(this.getTickrate()), "Could not sleep");
            if (over <= System.currentTimeMillis()) return null;
        }

        return this.last;
    }
}
