package de.lama.packets.server.client.event;

import de.lama.packets.Packet;

public record ClientPacketReceiveEvent(Packet packet) implements ServerClientEvent {
}
