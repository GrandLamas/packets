package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

public record NetworkAdapterData(ExceptionHandler exceptionHandler, EventHandler eventHandler, PacketRegistry registry) {

}
