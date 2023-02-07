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

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.PacketSendEvent;
import de.lama.packets.io.CachedIoPacket;
import de.lama.packets.io.IoPacket;
import de.lama.packets.io.stream.receiver.PacketReceiver;
import de.lama.packets.io.stream.transmitter.PacketTransmitter;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

class ConnectedClient extends AbstractNetworkAdapter implements Client {

    private final Socket socket;
    private final PacketWrapper wrapper;
    private final PacketTransmitter transmitter;
    private final PacketReceiver receiver;

    public ConnectedClient(Socket socket, PacketRegistry registry, PacketWrapper wrapper, PacketTransmitter transmitter,
                           PacketReceiver receiver, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.wrapper = wrapper;
        this.transmitter = transmitter;
        this.receiver = receiver;

        this.receiver.subscribe(this::packetReceived);
        this.transmitter.start();
    }

    private PacketReceiveEvent wrapEvent(IoPacket ioPacket) {
        return new PacketReceiveEvent(this, ioPacket.id(), this.parsePacket(ioPacket));
    }

    private void packetReceived(IoPacket ioPacket) {
        this.getEventHandler().notify(this.wrapEvent(ioPacket));
    }

    protected Packet parsePacket(IoPacket packet) {
        return this.wrapper.unwrap(packet.id(), packet.data());
    }

    protected IoPacket parsePacket(long packetId, Packet packet) {
        return new CachedIoPacket(packetId, this.wrapper.wrap(packetId, packet));
    }

    @Override
    protected void executeOpen() {
        this.receiver.start();
    }

    @Override
    protected void executeClose() {
        this.receiver.stop();
    }

    @Override
    protected void executeShutdown() {
        this.transmitter.stop();
        this.getExceptionHandler().operate(this.socket::close, "Failed to close socket");
    }

    @Override
    public int hashCode() {
        return this.socket.hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        Objects.requireNonNull(packet);
        return new SimpleOperation((async) -> {
            if (this.hasShutdown()) {
                this.handle(new IllegalStateException("Client already closed"));
                return;
            }

            long packetId = this.getRegistry().parseId(packet.getClass());
            if (this.getEventHandler().isCancelled(new PacketSendEvent(this, packetId, packet))) return;
            IoPacket ioPacket = this.parsePacket(packetId, packet);
            if (async) this.transmitter.queue(ioPacket);
            else this.transmitter.complete(ioPacket);
        });
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown() || this.receiver == null || !this.receiver.isRunning();
    }

    @Override
    public boolean hasShutdown() {
        return !this.transmitter.isRunning() || this.socket.isClosed();
    }

    @Override
    public PacketReceiveEvent awaitPacket(long timeoutInMillis) {
        if (timeoutInMillis < 0) {
            throw new IllegalArgumentException("Timeout may not be lower than 0");
        }

        return this.wrapEvent(this.receiver.awaitPacket(timeoutInMillis));
    }

    @Override
    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return this.socket.getPort();
    }
}
