package de.lama.packets.transceiver.transmitter;

import de.lama.packets.Packet;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class TimedPacketQueue implements PacketTransmitter {

    private final Socket socket;
    private final long tickDelayMs;
    private final ScheduledExecutorService pool;
    private final ExceptionHandler exceptionHandler;
    private final PacketWrapper packetWrapper;
    private final Queue<Packet> packetQueue;
    private ScheduledFuture<?> queue;

    TimedPacketQueue(Socket socket, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler, PacketWrapper packetWrapper) {
        this.socket = socket;
        this.tickDelayMs = this.calculateTickrate(tickrate);
        this.pool = pool;
        this.exceptionHandler = exceptionHandler;
        this.packetWrapper = packetWrapper;
        this.packetQueue = new ConcurrentLinkedQueue<>();
    }

    private OutputStream getOutput() {
        return ExceptionUtils.operate(this.exceptionHandler, this.socket::getOutputStream, "No outputstream defined");
    }

    private void flushOutput(OutputStream outputStream) {
        ExceptionUtils.operate(this.exceptionHandler, outputStream::flush, "Could not flush output");
    }

    private OutputStream write(Packet packet) {
        OutputStream stream = this.getOutput();
        if (stream == null) return null;
        byte[] data = this.packetWrapper.wrap(packet);
        if (!ExceptionUtils.operate(this.exceptionHandler, () -> stream.write(data),"Could not write data")) return null;
        return stream;
    }

    private void pushPackets() {
        Packet send;
        while ((send = this.packetQueue.poll()) != null) {
            this.complete(send);
        }
    }

    @Override
    public void start() {
        this.queue = this.pool.scheduleAtFixedRate(this::pushPackets, this.tickDelayMs, this.tickDelayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        this.queue.cancel(true);
        this.queue = null;
    }

    @Override
    public boolean isRunning() {
        return this.queue != null;
    }

    @Override
    public void queue(Packet packet) {
        this.packetQueue.add(packet);
    }

    @Override
    public void complete(Packet packet) {
        OutputStream stream = this.write(packet);
        if (stream == null) return;
        this.flushOutput(stream);
    }
}
