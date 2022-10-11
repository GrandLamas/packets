package de.lama.packets.server.event;

import de.lama.packets.GamePacket;

public record ClientPacketReceiveEvent(GamePacket packet) {
}
