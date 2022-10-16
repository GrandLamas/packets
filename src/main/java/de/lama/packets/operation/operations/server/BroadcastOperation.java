package de.lama.packets.operation.operations.server;

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.operation.Operation;

import java.util.Collection;
import java.util.function.Consumer;

public record BroadcastOperation(Collection<Client> clients, Packet packet) implements Operation {

    private void broadcast(Consumer<Operation> todo) {
        this.clients.forEach(c -> todo.accept(c.send(this.packet)));
    }

    @Override
    public Operation queue() {
        this.broadcast(Operation::queue);
        return this;
    }

    @Override
    public Operation complete() {
        this.broadcast(Operation::complete);
        return this;
    }
}
