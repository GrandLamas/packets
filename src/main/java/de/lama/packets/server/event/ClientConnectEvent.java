package de.lama.packets.server.event;

import de.lama.packets.server.ServerClient;

public record ClientConnectEvent(ServerClient client) implements ClientEvent {
}
