package de.lama.packets.server.client;

import de.lama.packets.Packet;
import de.lama.packets.operation.Operation;

public class ServerClientPacketSendOperation implements Operation {

    private final PacketTransmitter transmitter;
    private final Packet packet;

    public ServerClientPacketSendOperation(PacketTransmitter transmitter, Packet packet) {
        this.transmitter = transmitter;
        this.packet = packet;
    }

    @Override
    public void queue() {
        this.transmitter.queue(this.packet);
    }

    @Override
    public void complete() {
        this.transmitter.complete(this.packet);
    }
}
