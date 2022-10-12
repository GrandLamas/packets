package de.lama.packets.server.event;

import de.lama.packets.server.client.ServerClient;

public record ServerClientDisconnectEvent(ServerClient client) implements ServerEvent {
}
