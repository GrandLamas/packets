package de.lama.packets.server.client;

import de.lama.packets.AbstractPacketComponent;
import de.lama.packets.Packet;
import de.lama.packets.event.PacketSendEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.server.PacketServer;
import de.lama.packets.transceiver.receiver.DefaultPacketReceiver;
import de.lama.packets.transceiver.receiver.PacketReceiver;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.transceiver.transmitter.PacketTransmitterBuilder;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.GsonWrapper;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerGsonClient extends AbstractPacketComponent implements ServerClient {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(10);

    private final PacketServer server;
    private final Socket socket;
    private final PacketTransmitter transmitter;
    private final PacketReceiver receiver;

    ServerGsonClient(PacketServer server, Socket socket, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, server.getRegistry());
        this.server = server;
        this.socket = socket;
        PacketWrapper packetWrapper = new GsonWrapper(server.getRegistry());
        this.transmitter = new PacketTransmitterBuilder().packetWrapper(packetWrapper)
                .threadPool(SERVICE).tickrate(server.getTickrate()).exceptionHandler(exceptionHandler).build(socket);
        try {
            this.receiver = new DefaultPacketReceiver(socket.getInputStream(), server.getTickrate(), SERVICE, packetWrapper, exceptionHandler);
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }

        this.transmitter.start();
        this.receiver.start();
    }

    @Override
    public int hashCode() {
        return this.getAddress().hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        if (this.eventHandler.isCancelled(new PacketSendEvent(packet))) return null;
        return new ServerClientPacketSendOperation(this.transmitter, packet);
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already closed");
        this.transmitter.stop();
        this.receiver.stop();
        // TODO: Stop packet transmitter
        return new ServerClientCloseOperation(this.socket, this.exceptionHandler);
    }

    @Override
    public Packet awaitPacket(long timeoutInMs) {
        return this.receiver.awaitPacket(timeoutInMs);
    }

    @Override
    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return this.socket.getPort();
    }

    @Override
    public PacketServer getServer() {
        return this.server;
    }
}
