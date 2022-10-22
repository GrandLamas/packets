package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.OrderedEventExecutor;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractPacketIOComponent implements PacketIOComponent {

    protected final EventHandler eventHandler;
    protected final ExceptionHandler exceptionHandler;
    protected final Set<RepeatingOperation> repeatingOperations;
    protected final PacketRegistry registry;

    public AbstractPacketIOComponent(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.exceptionHandler = exceptionHandler;
        this.eventHandler = new OrderedEventExecutor();
        this.repeatingOperations = new CopyOnWriteArraySet<>();
        this.registry = registry;
    }

    protected void registerTask(RepeatingOperation operation) {
        this.repeatingOperations.add(operation);
    }

    protected void queueOperations() {
        this.repeatingOperations.forEach(RepeatingOperation::queue);
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.registry;
    }
}
