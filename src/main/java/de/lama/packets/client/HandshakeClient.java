package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.event.EventHandler;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.InetAddress;
import java.util.UUID;

class HandshakeClient implements Client {

    private static final Packet HANDSHAKE_PACKET = new HandshakePacket(HandshakePacket.VERSION);

    private final Client client;
    private final UUID handshakeUuid;
    private boolean handshake;

    public HandshakeClient(Client client) {
        this.client = client;

        this.getRegistry().registerPacket(HandshakePacket.ID, HandshakePacket.class);
        this.handshakeUuid = this.client.getEventHandler().subscribe(PacketReceiveEvent.class, new HandshakeListener(this::onHandshake));
        this.client.open().complete();
        this.client.send(HANDSHAKE_PACKET).complete();
    }

    private void awaitHandshake() {
        if (this.handshake) return;
        synchronized (this) {
            this.getExceptionHandler().operate(() -> this.wait(), "Could not wait");
        }
    }

    private void onHandshake(boolean accept) {
        this.handshake = accept;
        this.client.getEventHandler().unsubscribe(this.handshakeUuid);

        if (!this.handshake) {
            this.client.shutdown().complete();
        } else {
            this.client.close().complete();
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    private Operation waitOperation(Operation operation) {
        return new SimpleOperation((async) -> {
            this.awaitHandshake();
            if (async) operation.queue();
            else operation.complete();
        });
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
        return this.waitOperation(this.client.open());
    }

    @Override
    public Operation close() {
        return this.waitOperation(this.client.close());
    }

    @Override
    public Operation shutdown() {
        return this.waitOperation(this.client.shutdown());
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
        return this.waitOperation(this.client.send(packet));
    }

    @Override
    public PacketReceiveEvent awaitPacket(long timeoutInMillis) {
        this.awaitHandshake();
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
