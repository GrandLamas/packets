package de.lama.packets.large;

import de.lama.packets.ConnectionTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SendLargePacketTest extends ConnectionTest {

    public SendLargePacketTest() throws IOException {
        super();

        this.client.getRegistry().registerPacket(0, LargePacket.class);
        this.server.getRegistry().registerPacket(0, LargePacket.class);
    }

    @Test
    public void send() {
        this.client.send(new LargePacket()).complete();
        System.out.println("Sent!");
        this.server.shutdown().complete();
    }
}
