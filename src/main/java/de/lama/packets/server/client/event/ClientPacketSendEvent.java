package de.lama.packets.server.client.event;

import de.lama.packets.Packet;
import de.lama.packets.event.CancelContainer;
import de.lama.packets.event.Cancellable;
import de.lama.packets.server.client.ServerClient;

public record ClientPacketSendEvent(ServerClient client, Packet packet, CancelContainer cancelContainer) implements ServerClientEvent, Cancellable {

    public ClientPacketSendEvent(ServerClient client, Packet packet) {
        this(client, packet, new CancelContainer());
    }

    @Override
    public boolean isCancelled() {
        return this.cancelContainer.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelContainer.setCancelled(cancelled);
    }
}
