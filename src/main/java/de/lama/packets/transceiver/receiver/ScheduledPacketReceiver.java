package de.lama.packets.transceiver.receiver;

import de.lama.packets.Packet;
import de.lama.packets.io.PacketInputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

// TODO: Builder with merged tickrate on transmitter
public class ScheduledPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final PacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private final PacketConsumer consumer;
    private Packet last;

    public ScheduledPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, PacketWrapper wrapper, ExceptionHandler exceptionHandler, PacketConsumer consumer) {
        super(pool, tickrate);
        this.consumer = consumer;
        this.in = new PacketInputStream(new DataInputStream(new BufferedInputStream(inputStream)), wrapper);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected void tick() {
        this.exceptionHandler.operate(() -> {
            while (this.in.available() > 0) {
                Packet packet = this.in.readPacket();
                this.last = packet;
                this.consumer.accept(packet);
            }
        }, "Failed to read packet");
    }

    @Override
    public Packet awaitPacket(long timeoutInMillis) {
        long over = System.currentTimeMillis() + timeoutInMillis;
        Packet last = this.last;
        while (this.last == last) {
            this.exceptionHandler.operate(() -> Thread.sleep(this.getTickrate()), "Could not sleep");
            if (over <= System.currentTimeMillis()) return null;
        }

        return this.last;
    }
}
