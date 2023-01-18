package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.OrderedEventExecutor;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractNetworkAdapter implements NetworkAdapter {

    private final NetworkAdapterData data;

    public AbstractNetworkAdapter(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.data = new NetworkAdapterData(exceptionHandler, new OrderedEventExecutor(), new CopyOnWriteArraySet<>(), registry);
    }

    protected void registerTask(RepeatingOperation operation) {
        this.data.repeatingOperations().add(operation);
    }

    protected void queueOperations() {
        this.data.repeatingOperations().forEach(RepeatingOperation::queue);
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
