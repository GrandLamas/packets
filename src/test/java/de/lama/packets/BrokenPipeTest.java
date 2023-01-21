package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.client.HandshakePacket;
import de.lama.packets.server.Server;
import de.lama.packets.server.ServerBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BrokenPipeTest {

    @Test
    public void clientClosedTest() throws IOException {
        Server server = new ServerBuilder().build(3999);
        server.open().complete();

        Client client = new ClientBuilder().exceptionHandler(throwable ->
                System.out.println("Successfully failed to send packet")).build("localhost", 3999);
        client.open().complete();

        client.shutdown().complete();
        client.send(new HandshakePacket("AMOGUS")).complete();

        server.shutdown().complete();
    }

    @Test
    public void serverClosedTest() throws IOException {
        Server server = new ServerBuilder().build(3999);
        server.open().complete();

        Client client = new ClientBuilder().exceptionHandler(throwable ->
                System.out.println("Successfully threw exception")).build("localhost", 3999);
        client.open().complete();

        server.shutdown().complete();
        client.send(new HandshakePacket("AMOGUS")).complete();
    }
}
