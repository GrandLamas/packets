package de.lama.packets.event.events;

import de.lama.packets.NetworkAdapter;
import de.lama.packets.event.CancelEventBehaviour;
import de.lama.packets.event.Cancellable;
import de.lama.packets.event.Event;
import de.lama.packets.event.EventBehaviour;

public record AdapterOpenEvent(NetworkAdapter adapter, EventBehaviour behaviour) implements Event, Cancellable {

    public AdapterOpenEvent(NetworkAdapter adapter) {
        this(adapter, new CancelEventBehaviour());
    }
}
