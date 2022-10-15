package de.lama.packets.transceiver.transmitter;

import de.lama.packets.Packet;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

class TimedPacketQueue extends AbstractScheduledTransceiver implements PacketTransmitter {

    private final DataOutputStream out;
    private final ExceptionHandler exceptionHandler;
    private final PacketWrapper packetWrapper;
    private final Queue<Packet> packetQueue;

    TimedPacketQueue(OutputStream outputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler, PacketWrapper packetWrapper) {
        super(pool, tickrate);
        this.exceptionHandler = exceptionHandler;
        this.packetWrapper = packetWrapper;
        this.packetQueue = new ConcurrentLinkedQueue<>();
        this.out = new DataOutputStream(new BufferedOutputStream(outputStream));
    }

    private void flush() {
        ExceptionUtils.operate(this.exceptionHandler, this.out::flush, "Could not flush output");
    }

    private void write(Packet packet) {
        byte[] data = this.packetWrapper.wrap(packet);
        ExceptionUtils.operate(this.exceptionHandler, () -> {
            this.out.writeChar(PACKET_DATA_TYPE);
            this.out.writeInt(data.length);
            this.out.write(data);
        },"Could not write data");
    }

    @Override
    protected void tick() {
        Packet send;
        while ((send = this.packetQueue.poll()) != null) {
            this.complete(send);
        }
    }

    @Override
    public void queue(Packet packet) {
        this.packetQueue.add(packet);
    }

    @Override
    public void complete(Packet packet) {
        this.write(packet);
        this.flush();
    }
}
