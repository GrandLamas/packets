package de.lama.packets.transceiver;

import de.lama.packets.operation.RepeatingOperation;

public interface PacketTransceiver extends RepeatingOperation {

    long getTickrate();

}
