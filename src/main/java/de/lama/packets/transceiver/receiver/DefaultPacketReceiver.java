package de.lama.packets.transceiver.receiver;

import de.lama.packets.Packet;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultPacketReceiver implements PacketReceiver {

    private static final int BYTE_BUFFER = 2048;

    private final Socket socket;
    private final long tickrateInMillis;
    private final ExceptionHandler exceptionHandler;

    public DefaultPacketReceiver(Socket socket, int tickrate, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.tickrateInMillis = this.calculateTickrate(tickrate);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void start() {
//        char[] buffer = new char[2048];
//        int charsRead;
//        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        while ((charsRead = in.read(buffer)) != -1) {
//            String message = new String(buffer).substring(0, charsRead);
//            Packet packet = new GsonWrapper(server.getRegistry()).unwrapString(message);
//            System.out.println(((HandshakePacket) packet).getVersion());
//        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Packet awaitPacket(long timeoutInMillis) {
        AtomicReference<Packet> packet = new AtomicReference<>();
        long over = System.currentTimeMillis() + timeoutInMillis;
        while (packet.get() == null) {
            ExceptionUtils.operate(this.exceptionHandler, () -> Thread.sleep(this.tickrateInMillis), "Could not sleep");
            if (over <= System.currentTimeMillis()) return null;
        }

        return packet.get();
    }
}
