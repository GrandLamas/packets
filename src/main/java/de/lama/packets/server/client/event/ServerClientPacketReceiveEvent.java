package de.lama.packets.server.client.event;

import de.lama.packets.Packet;
import de.lama.packets.server.client.ServerClient;

public record ServerClientPacketReceiveEvent(ServerClient client, Packet packet) implements ServerClientEvent {
}
