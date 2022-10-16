package de.lama.packets.operation.operations;

import de.lama.packets.operation.Operation;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;

public class PacketSendOperation implements Operation {

    private final PacketTransmitter transmitter;
    private final TransceivablePacket packet;

    public PacketSendOperation(PacketTransmitter transmitter, TransceivablePacket packet) {
        this.transmitter = transmitter;
        this.packet = packet;
    }

    @Override
    public Operation queue() {
        this.transmitter.queue(this.packet);
        return this;
    }

    @Override
    public Operation complete() {
        this.transmitter.complete(this.packet);
        return this;
    }
}
