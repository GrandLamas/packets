package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.event.EventHandler;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.InetAddress;
import java.util.UUID;

class HandshakeClient implements Client {

    private final Client client;
    private boolean handshake;
    private UUID handshakeSubscription;

    public HandshakeClient(Client client) {
        this.client = client;
    }

    @Override
    public InetAddress getAddress() {
        return this.client.getAddress();
    }

    @Override
    public int getPort() {
        return this.client.getPort();
    }

    @Override
    public Operation open() {
        return this.client.open();
    }

    @Override
    public Operation close() {
        return this.client.close();
    }

    @Override
    public Operation shutdown() {
        return this.client.shutdown();
    }

    @Override
    public boolean isClosed() {
        return this.client.isClosed();
    }

    @Override
    public boolean hasShutdown() {
        return this.client.hasShutdown();
    }

    @Override
    public Operation send(Packet packet) {
        return this.client.send(packet);
    }

    @Override
    public PacketReceiveEvent awaitPacket(long timeoutInMillis) {
        return this.client.awaitPacket(timeoutInMillis);
    }

    @Override
    public EventHandler getEventHandler() {
        return this.client.getEventHandler();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.client.getRegistry();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return this.client.getExceptionHandler();
    }
}
