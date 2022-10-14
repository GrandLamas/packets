package de.lama.packets.server.event;

import de.lama.packets.event.CancelContainer;
import de.lama.packets.server.PacketServer;
import de.lama.packets.server.client.ServerClient;

public record ServerClientDisconnectEvent(PacketServer server, ServerClient client, CancelContainer cancelContainer) implements ServerEvent {

    public ServerClientDisconnectEvent(PacketServer server, ServerClient client) {
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
