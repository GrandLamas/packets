package de.lama.packets.transceiver.transmitter;

import de.lama.packets.Packet;
import de.lama.packets.io.PacketOutputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

class ScheduledPacketQueue extends AbstractScheduledTransceiver implements PacketTransmitter {

    private final PacketOutputStream out;
    private final ExceptionHandler exceptionHandler;
    private final Queue<Packet> packetQueue;

    ScheduledPacketQueue(OutputStream outputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler, PacketWrapper packetWrapper) {
        super(pool, tickrate);
        this.exceptionHandler = exceptionHandler;
        this.packetQueue = new ConcurrentLinkedQueue<>();
        this.out = new PacketOutputStream(new DataOutputStream(new BufferedOutputStream(outputStream)), packetWrapper);
    }

    private void flush() {
        this.exceptionHandler.operate(this.out::flush, "Could not flush output");
    }

    private void write(Packet packet) {
        this.exceptionHandler.operate(() -> this.out.write(packet),"Could not write data");
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
