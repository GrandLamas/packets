package de.lama.packets.transceiver;

import de.lama.packets.operation.ThreadedOperation;

public interface PacketTransceiver extends ThreadedOperation {

    long getTickrate();

}
