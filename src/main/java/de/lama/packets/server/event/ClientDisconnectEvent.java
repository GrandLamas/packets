package de.lama.packets.server.event;

import de.lama.packets.server.ServerClient;

public record ClientDisconnectEvent(ServerClient client) implements ClientEvent {
}
