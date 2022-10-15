package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.operation.Operation;

import java.util.function.Consumer;

class BroadcastOperation implements Operation {

    private final PacketServer server;
    private final Packet packet;

    BroadcastOperation(PacketServer server, Packet packet) {
        this.server = server;
        this.packet = packet;
    }

    private void broadcast(Consumer<Operation> todo) {
        this.server.getClients().forEach(c -> todo.accept(c.send(this.packet)));
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
