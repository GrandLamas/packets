package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.OrderedEventExecutor;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

public abstract class AbstractNetworkAdapter implements NetworkAdapter {

    private final NetworkAdapterData data;

    public AbstractNetworkAdapter(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.data = new NetworkAdapterData(exceptionHandler, new OrderedEventExecutor(), registry);
    }

    protected abstract void executeClose();

    protected abstract void executeOpen();

    protected abstract void executeShutdown();

    @Override
    public Operation open() {
        return new SimpleOperation((async) -> {
            if (!this.isClosed()) throw new IllegalStateException("Adapter already open");
            this.executeOpen();
        });
    }

    @Override
    public Operation close() {
        return new SimpleOperation((async) -> {
            if (this.isClosed()) throw new IllegalStateException("Adapter already closed");
            this.executeClose();
        });
    }

    @Override
    public Operation shutdown() {
        return new SimpleOperation((async) -> {
            if (this.hasShutdown()) throw new IllegalStateException("Adapter already shutdown");
            if (!this.isClosed()) {
                if (async) this.close().queue();
                else this.close().complete();
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
