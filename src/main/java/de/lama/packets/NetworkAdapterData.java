package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.util.Set;

public record NetworkAdapterData(ExceptionHandler exceptionHandler, EventHandler eventHandler, Set<RepeatingOperation> repeatingOperations, PacketRegistry registry) {

}
