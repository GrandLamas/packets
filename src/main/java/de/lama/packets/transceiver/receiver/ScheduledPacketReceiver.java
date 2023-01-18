package de.lama.packets.transceiver.receiver;

import de.lama.packets.io.BufferedPacketInputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.transceiver.IoTransceivablePacket;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final BufferedPacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private final Map<UUID, PacketConsumer> consumer;
    private TransceivablePacket last;

    ScheduledPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.consumer = new ConcurrentHashMap<>();
        this.in = new BufferedPacketInputStream(inputStream);
        this.exceptionHandler = exceptionHandler;
    }

    private void packetReceived(TransceivablePacket packet) {
        this.last = packet;
        synchronized (this) {
            this.notifyAll();
        }
        this.consumer.values().forEach(consumer -> consumer.accept(packet));
    }

    @Override
    protected void tick() {
        this.exceptionHandler.operate(() -> {
            while (this.in.available() > 0) {
                this.packetReceived(new IoTransceivablePacket(this.in.readPacket()));
            }
        }, "Failed to read packet");
    }

    @Override
    public TransceivablePacket awaitPacket(long timeoutInMillis) {
        synchronized (this) {this.exceptionHandler.operate(() -> this.wait(timeoutInMillis), "Could not wait");}
        return this.last;
    }

    @Override
    public UUID subscribe(PacketConsumer consumer) {
        UUID uuid = UUID.randomUUID();
        this.consumer.put(uuid, consumer);
        return uuid;
    }

    @Override
    public boolean unsubscribe(UUID uuid) {
        return this.consumer.remove(uuid) != null;
    }
}
