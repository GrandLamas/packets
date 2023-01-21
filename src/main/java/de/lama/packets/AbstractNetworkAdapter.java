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
            if (!this.isClosed()) {
                this.close().complete();
            }

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
