package de.lama.packets.server.client.event;

import de.lama.packets.Packet;
import de.lama.packets.server.client.ServerClient;

public record ClientPacketReceiveEvent(ServerClient client, Packet packet) implements ServerClientEvent {
}
