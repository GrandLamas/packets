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
import de.lama.packets.event.events.AdapterCloseEvent;
import de.lama.packets.event.events.AdapterOpenEvent;
import de.lama.packets.event.events.AdapterShutdownEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

public abstract class AbstractNetworkAdapter implements NetworkAdapter {

    private final NetworkAdapterData data;

    public AbstractNetworkAdapter(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.data = new NetworkAdapterData(exceptionHandler, new OrderedEventExecutor(), registry);
    }

    protected void handle(Exception exception) {
        this.getExceptionHandler().accept(exception);
    }

    protected abstract void executeClose();

    protected abstract void executeOpen();

    protected abstract void executeShutdown();

    @Override
    public Operation open() {
        return new SimpleOperation((async) -> {
            if (!this.isClosed()) {
                this.handle(new IllegalStateException("Adapter already open"));
                return;
            }

            if (this.getEventHandler().isCancelled(new AdapterOpenEvent(this))) return;
            this.executeOpen();
        });
    }

    @Override
    public Operation close() {
        return new SimpleOperation((async) -> {
            if (this.isClosed()) {
                this.handle(new IllegalStateException("Adapter already closed"));
                return;
            }

            if (this.getEventHandler().isCancelled(new AdapterCloseEvent(this))) return;
            this.executeClose();
        });
    }

    @Override
    public Operation shutdown() {
        return new SimpleOperation((async) -> {
            if (this.hasShutdown()) {
                this.handle(new IllegalStateException("Adapter already shutdown"));
                return;
            }

            this.data.eventHandler().notify(new AdapterShutdownEvent(this));
            if (!this.isClosed()) this.close().complete();
            this.executeShutdown();
        });
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return this.data.exceptionHandler();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.data.eventHandler();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.data.registry();
    }
}
