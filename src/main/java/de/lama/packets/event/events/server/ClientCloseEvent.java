package de.lama.packets.event.events.server;

import de.lama.packets.client.Client;
import de.lama.packets.event.CancelContainer;
import de.lama.packets.server.Server;

public record ClientCloseEvent(Server server, Client client, CancelContainer cancelContainer) implements ServerEvent {

    public ClientCloseEvent(Server server, Client client) {
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
