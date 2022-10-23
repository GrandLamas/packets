package de.lama.packets.registry;

import de.lama.packets.Packet;

public record RegistryEntry<T extends Packet>(long id, Class<T> clazz) {
}
