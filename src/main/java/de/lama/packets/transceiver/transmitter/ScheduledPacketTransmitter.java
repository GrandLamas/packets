package de.lama.packets.transceiver.transmitter;

import de.lama.packets.io.DirectPacketOutputStream;
import de.lama.packets.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.util.ExceptionHandler;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

class ScheduledPacketTransmitter extends AbstractScheduledTransceiver implements PacketTransmitter {

    private final DirectPacketOutputStream out;
    private final ExceptionHandler exceptionHandler;
    private final Queue<TransceivablePacket> packetQueue;

    ScheduledPacketTransmitter(OutputStream outputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.exceptionHandler = exceptionHandler;
        this.packetQueue = new ConcurrentLinkedQueue<>();
        this.out = new DirectPacketOutputStream(new DataOutputStream(new BufferedOutputStream(outputStream)));
    }

    private void flush() {
        this.exceptionHandler.operate(this.out::flush, "Could not flush output");
    }

    private void write(TransceivablePacket packet) {
        this.exceptionHandler.operate(() -> this.out.write(packet),"Could not write data");
    }

    @Override
    protected void tick() {
        TransceivablePacket send;
        while ((send = this.packetQueue.poll()) != null) {
            this.complete(send);
        }
    }

    @Override
    public void queue(TransceivablePacket packet) {
        this.packetQueue.add(packet);
    }

    @Override
    public void complete(TransceivablePacket packet) {
        this.write(packet);
        this.flush();
    }
}