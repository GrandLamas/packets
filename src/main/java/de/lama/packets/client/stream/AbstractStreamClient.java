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

package de.lama.packets.client.stream;

import de.lama.packets.Packet;
import de.lama.packets.client.AbstractClient;
import de.lama.packets.client.Client;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.stream.IoPacket;
import de.lama.packets.stream.transceiver.receiver.PacketReceiver;
import de.lama.packets.stream.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.IOException;

public abstract class AbstractStreamClient extends AbstractClient implements Client {

    private final PacketTransmitter transmitter;
    private final PacketReceiver receiver;

    public AbstractStreamClient(PacketRegistry registry, PacketWrapper wrapper, PacketTransmitter transmitter,
                                PacketReceiver receiver, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry, wrapper);
        this.transmitter = transmitter;
        this.receiver = receiver;

        this.receiver.subscribe(this::packetReceived);
        this.transmitter.start();
    }

    protected abstract void shutdownConnection() throws IOException;

    @Override
    protected void executeShutdown() {
        this.transmitter.stop();
        this.getExceptionHandler().operate(this::shutdownConnection, "Failed to close connection");
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
    protected void executeSend(boolean async, long packetId, Packet packet) {
        IoPacket ioPacket = this.parsePacket(packetId, packet);
        if (async) this.transmitter.queue(ioPacket);
        else this.transmitter.complete(ioPacket);
    }

    @Override
    public boolean isClosed() {
        return super.isClosed() || this.receiver == null || !this.receiver.isRunning();
    }

    @Override
    public boolean hasShutdown() {
        return !this.transmitter.isRunning();
    }

    @Override
    public PacketReceiveEvent awaitPacket(long timeoutInMillis) {
        if (timeoutInMillis < 0) {
            throw new IllegalArgumentException("Timeout may not be lower than 0");
        }

        return this.wrapEvent(this.receiver.awaitPacket(timeoutInMillis));
    }
}
