package de.lama.packets.operation;

import de.lama.packets.Packet;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;

public class PacketSendOperation implements Operation {

    private final PacketTransmitter transmitter;
    private final Packet packet;

    public PacketSendOperation(PacketTransmitter transmitter, Packet packet) {
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
