package de.lama.packets.transceiver.receiver;

import de.lama.packets.Packet;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final DataInputStream in;
    private final PacketWrapper wrapper;
    private final ExceptionHandler exceptionHandler;
    private Packet last;

    public DefaultPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, PacketWrapper wrapper, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.in = new DataInputStream(new BufferedInputStream(inputStream));
        this.exceptionHandler = exceptionHandler;
        this.wrapper = wrapper;
    }

    @Override
    protected void tick() {
        try {
            char type = this.in.readChar();
            if (type != PACKET_DATA_TYPE) return;
            int length = this.in.readInt();
            byte[] buffer = new byte[length];

            int totalBytesRead = 0;
            while (totalBytesRead < length) {
                totalBytesRead += this.in.read(buffer, totalBytesRead, buffer.length - totalBytesRead);
            }

            // TODO: Push packet to event
            this.last = this.wrapper.unwrap(buffer);
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
            ExceptionUtils.operate(this.exceptionHandler, () -> Thread.sleep(this.getTickrate()), "Could not sleep");
            if (over <= System.currentTimeMillis()) return null;
        }

        return this.last;
    }
}
