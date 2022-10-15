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

public class DefaultPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final PacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private Packet last;

    public DefaultPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, PacketWrapper wrapper, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.in = new PacketInputStream(new DataInputStream(new BufferedInputStream(inputStream)), wrapper);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected void tick() {
        try {
            Packet packet;
            while ((packet = this.in.readPacket()) != null) {
                this.last = packet;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            // TODO
        }
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
