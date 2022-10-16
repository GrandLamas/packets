package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.OrderedEventExecutor;
import de.lama.packets.operation.ThreadedOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractPacketIOComponent implements PacketIOComponent {

    protected final EventHandler eventHandler;
    protected final PacketRegistry registry;
    protected final ExceptionHandler exceptionHandler;
    protected final Set<ThreadedOperation> threadedOperations;

    public AbstractPacketIOComponent(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.exceptionHandler = exceptionHandler;
        this.registry = registry;
        this.eventHandler = new OrderedEventExecutor();
        this.threadedOperations = new CopyOnWriteArraySet<>();
    }

    protected void registerOperation(ThreadedOperation operation) {
        this.threadedOperations.add(operation);
    }

    protected void queueOperations() {
        this.threadedOperations.forEach(ThreadedOperation::queue);
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }
}
