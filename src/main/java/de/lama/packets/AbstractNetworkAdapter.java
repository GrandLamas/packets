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

package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.OrderedEventExecutor;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractNetworkAdapter<C> implements NetworkAdapter {

    private final EventHandler eventHandler;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean connected;
    private final Queue<C> queue;
    private final long schedule;
    private ScheduledFuture<?> task;

    public AbstractNetworkAdapter(int tickrate) {
        if (tickrate < 0 || tickrate > 1000) throw new IllegalArgumentException("Illegal tickrate");

        this.eventHandler = new OrderedEventExecutor();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.connected = new AtomicBoolean();
        this.queue = new ConcurrentLinkedQueue<>();
        this.schedule = tickrate == 0 ? 0 : 1000L / tickrate;
    }

    public AbstractNetworkAdapter() {
        this(DEFAULT_TICKRATE);
    }

    protected abstract Future<Boolean> process(C cached);

    protected abstract CompletableFuture<Void> implConnect();

    protected abstract CompletableFuture<Void> implDisconnect();

    protected void found(C cache) {
        if (!this.connected.get()) throw new IllegalStateException("Cannot cache right now");
        if (this.schedule == 0) {
            this.process(cache);
        } else {
            this.queue.add(cache);
        }
    }

    protected void processAll() {
        C polled;
        while ((polled = this.queue.poll()) != null) {
            this.process(polled);
        }
    }

    protected Void startTick(Void v) {
        this.queue.clear();
        if (this.schedule > 0)
            this.task = this.scheduler.scheduleAtFixedRate(this::processAll, 0, this.schedule, TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public CompletableFuture<Void> connect() {
        if (this.connected.compareAndExchange(false, true))
            throw new IllegalStateException("Adapter already connected!");

        return this.implConnect().thenApply(this::startTick);
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        if (!this.connected.compareAndExchange(true, false)) {
            throw new IllegalStateException("Adapter not connected!");
        }

        return this.implDisconnect().thenApply(unused -> {
            if (this.task != null) this.task.cancel(true);
            this.scheduler.shutdown();
            return null;
        });
    }

    @Override
    public boolean isConnected() {
        return this.connected.get();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }
}
