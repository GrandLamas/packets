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
import de.lama.packets.event.EventHandler;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.InetAddress;
import java.util.UUID;

public class HandshakeClient implements Client {

    private final Client client;
    private final UUID handshakeUuid;
    private final HandshakePacket sent;
    private volatile boolean handshake;

    public HandshakeClient(Client client) {
        this.client = client;
        this.sent = this.buildPacket();

        this.getRegistry().registerPacket(HandshakePacket.ID, HandshakePacket.class);
        this.handshakeUuid = this.client.getEventHandler().subscribe(PacketReceiveEvent.class, this::onEvent);
        this.client.open().complete();
        this.client.send(this.sent).complete();
    }

    private Operation waitOperation(Operation operation) {
        return new SimpleOperation((async) -> {
            this.awaitHandshake();
            if (async) operation.queue();
            else operation.complete();
        });
    }

    private void awaitHandshake() {
        if (this.handshake) return;
        synchronized (this) {
            this.getExceptionHandler().operate(() -> this.wait(), "Could not wait");
        }
    }

    protected HandshakePacket buildPacket() {
        return new HandshakePacket(HandshakePacket.VERSION);
    }

    protected boolean validateHandshakePacket(HandshakePacket packet) {
        return packet.version().equals(this.sent.version());
    }

    protected void onEvent(PacketReceiveEvent event) {
        this.client.getEventHandler().unsubscribe(this.handshakeUuid);
        this.handshake = event.packetId() == HandshakePacket.ID && this.validateHandshakePacket((HandshakePacket) event.packet());
        if (!this.handshake) {
            this.client.shutdown().complete();
        } else {
            this.client.close().complete();
            synchronized (this) {
                this.notifyAll();
            }
        }
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
