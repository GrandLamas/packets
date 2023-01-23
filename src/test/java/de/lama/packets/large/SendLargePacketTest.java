package de.lama.packets.large;

import de.lama.packets.ConnectionTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SendLargePacketTest extends ConnectionTest {

    public SendLargePacketTest() throws IOException {
        super();

        this.client.getRegistry().registerPacket(LargePacket.ID, LargePacket.class);
        this.server.getRegistry().registerPacket(LargePacket.ID, LargePacket.class);
    }

    @Test
    public void send() {
        this.client.send(new LargePacket(1)).complete();
        System.out.println("Sent!");
        this.server.shutdown().complete();
    }
}
