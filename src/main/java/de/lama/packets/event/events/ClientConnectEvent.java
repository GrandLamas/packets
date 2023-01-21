package de.lama.packets.event.events;

import de.lama.packets.client.Client;
import de.lama.packets.event.CancelEventBehaviour;
import de.lama.packets.event.Cancellable;
import de.lama.packets.event.Event;
import de.lama.packets.event.EventBehaviour;
import de.lama.packets.server.Server;

public record ClientConnectEvent(Server server, Client client, EventBehaviour behaviour) implements Event, Cancellable {

    public ClientConnectEvent(Server server, Client client) {
        this(server, client, new CancelEventBehaviour());
    }
}
