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

package de.lama.packets.client.transceiver.transmitter;

import de.lama.packets.client.transceiver.TransceivablePacket;
import de.lama.packets.io.BufferedPacketOutputStream;
import de.lama.packets.client.transceiver.AbstractScheduledTransceiver;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

class ScheduledPacketTransmitter extends AbstractScheduledTransceiver implements PacketTransmitter {

    private final BufferedPacketOutputStream out;
    private final ExceptionHandler exceptionHandler;
    private final Queue<TransceivablePacket> packetQueue;

    ScheduledPacketTransmitter(OutputStream outputStream, int tickrate, ScheduledExecutorService pool, ExceptionHandler exceptionHandler) {
        super(pool, tickrate);
        this.exceptionHandler = exceptionHandler;
        this.packetQueue = new ConcurrentLinkedQueue<>();
        this.out = new BufferedPacketOutputStream(outputStream);
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
