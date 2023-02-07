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

package de.lama.packets.stream.transceiver;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledTransceiver extends AbstractRepeatingOperation implements PacketTransceiver {

    private final ScheduledExecutorService pool;
    private final long tickrate;

    protected AbstractScheduledTransceiver(ScheduledExecutorService pool, int tickrate) {
        this.pool = pool;
        this.tickrate = this.calculateTickrate(tickrate);
    }

    private long calculateTickrate(int tickrate) {
        return 1000L / tickrate;
    }

    protected abstract void tick();

    @Override
    public Operation complete() {
        this.tick();
        return this;
    }

    @Override
    public Operation queue() {
        this.pool.submit(this::complete);
        return this;
    }

    @Override
    protected Future<?> createRepeatingTask() {
        return this.pool.scheduleAtFixedRate(this::complete, this.tickrate, this.tickrate, TimeUnit.MILLISECONDS);
    }

    @Override
    public long getTickrate() {
        return this.tickrate;
    }
}
