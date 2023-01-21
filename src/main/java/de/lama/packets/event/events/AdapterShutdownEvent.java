package de.lama.packets.event.events;

import de.lama.packets.NetworkAdapter;
import de.lama.packets.event.Event;

public record AdapterShutdownEvent(NetworkAdapter adapter) implements Event {
}
