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

package de.lama.packets.io.stream.transmitter;

import de.lama.packets.io.stream.AbstractTransceiverBuilder;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

public class PacketTransmitterBuilder extends AbstractTransceiverBuilder {

    public PacketTransmitterBuilder tickrate(int tickrate) {
        this.tickrate = tickrate;
        return this;
    }

    public PacketTransmitterBuilder threadPool(ScheduledExecutorService pool) {
        this.pool = pool;
        return this;
    }

    public PacketTransmitterBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public PacketTransmitter build(OutputStream out) {
        return new ScheduledPacketTransmitter(Objects.requireNonNull(out), this.tickrate, Objects.requireNonNull(this.pool),
                Objects.requireNonNull(this.exceptionHandler));
    }
}
