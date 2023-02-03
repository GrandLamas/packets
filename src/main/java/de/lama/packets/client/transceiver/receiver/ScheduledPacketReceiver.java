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

package de.lama.packets.client.transceiver.receiver;

import de.lama.packets.client.transceiver.IoTransceivablePacket;
import de.lama.packets.client.transceiver.TransceivablePacket;
import de.lama.packets.io.BufferedPacketInputStream;
import de.lama.packets.client.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledPacketReceiver extends AbstractScheduledTransceiver implements PacketReceiver {

    private final BufferedPacketInputStream in;
    private final ExceptionHandler exceptionHandler;
    private final Map<UUID, PacketConsumer> consumer;
    private TransceivablePacket last;

    ScheduledPacketReceiver(InputStream inputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.consumer = new ConcurrentHashMap<>();
        this.in = new BufferedPacketInputStream(inputStream);
        this.exceptionHandler = exceptionHandler;
    }

    private void packetReceived(TransceivablePacket packet) {
        this.last = packet;
        synchronized (this) {
            this.notifyAll();
        }
        this.consumer.values().forEach(consumer -> consumer.accept(packet));
    }

    @Override
    protected void tick() {
        this.exceptionHandler.operate(() -> {
            while (this.in.available() > 0) {
                this.packetReceived(new IoTransceivablePacket(this.in.readPacket()));
            }
        }, "Failed to read packet");
    }

    @Override
    public TransceivablePacket awaitPacket(long timeoutInMillis) {
        synchronized (this) {this.exceptionHandler.operate(() -> this.wait(timeoutInMillis), "Could not wait");}
        return this.last;
    }

    @Override
    public UUID subscribe(PacketConsumer consumer) {
        UUID uuid = UUID.randomUUID();
        this.consumer.put(uuid, consumer);
        return uuid;
    }

    @Override
    public boolean unsubscribe(UUID uuid) {
        return this.consumer.remove(uuid) != null;
    }
}
