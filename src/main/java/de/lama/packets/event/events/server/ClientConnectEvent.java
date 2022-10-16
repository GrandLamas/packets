package de.lama.packets.event.events.server;

import de.lama.packets.client.Client;
import de.lama.packets.event.CancelContainer;
import de.lama.packets.server.PacketServer;

public record ClientConnectEvent(PacketServer server, Client client, CancelContainer cancelContainer) implements ServerEvent {

    public ClientConnectEvent(PacketServer server, Client client) {
        this(server, client, new CancelContainer());
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelContainer.setCancelled(cancelled);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelContainer.isCancelled();
    }
}
