/*
 * MIT License
 *
 * Copyright (c) 2023 Cuuky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.event.EventHandler;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.CompletableFutureUtil;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HandshakeClient implements Client {

    private final Client client;
    private final UUID handshakeUuid;
    private final HandshakePacket handshakePacket;
    private final AtomicBoolean handshake;
    private final boolean initiator;

    public HandshakeClient(Client client, boolean initiator) {
        this.client = client;
        this.initiator = initiator;
        this.handshakePacket = this.buildPacket();
        this.handshake = new AtomicBoolean();

        this.getRegistry().registerPacket(HandshakePacket.ID, HandshakePacket.class);
        this.handshakeUuid = this.client.getEventHandler().subscribe(PacketReceiveEvent.class, this::onEvent);
    }

    private CompletableFuture<Void> sendHandshake() {
        return this.client.send(this.handshakePacket);
    }

    private CompletableFuture<Void> awaitHandshake(Void v) {
        if (this.initiator) {
            return this.sendHandshake().thenApply(n -> this.awaitHandshake());
        }
        return CompletableFutureUtil.supplyAsync(this::awaitHandshake);
    }

    private Void awaitHandshake() {
        if (this.handshake.get()) return null;
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                this.awaitHandshake();
            }
        }
        return null;
    }

    private <T> T waitOperation(T operation) {
        this.awaitHandshake();
        return operation;
    }

    protected HandshakePacket buildPacket() {
        return new HandshakePacket(HandshakePacket.VERSION);
    }

    protected boolean validateHandshakePacket(HandshakePacket packet) {
        return packet.version().equals(this.handshakePacket.version());
    }

    protected void onEvent(PacketReceiveEvent event) {
        this.client.getEventHandler().unsubscribe(this.handshakeUuid);
        this.handshake.set(event.packetId() == HandshakePacket.ID && this.validateHandshakePacket((HandshakePacket) event.packet()));
        if (!this.handshake.get()) {
            this.client.disconnect().join();
        } else {
            if (!this.initiator) {
                try {
                    this.sendHandshake().get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return this.waitOperation(this.client.send(packet));
    }

    @Override
    public CompletableFuture<Void> connect() {
        return this.client.connect().thenCompose(this::awaitHandshake);
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return this.waitOperation(this.client.disconnect());
    }

    @Override
    public boolean isConnected() {
        return this.client.isConnected();
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
    public EventHandler getEventHandler() {
        return this.client.getEventHandler();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.client.getRegistry();
    }
}
