package de.lama.packets.server;

import de.lama.packets.operation.AbstractSimpleThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.server.client.ServerClient;

import java.util.function.Consumer;

public class ClientCloseOperation extends AbstractSimpleThreadedOperation {

    private final ServerClient client;
    private final Consumer<ServerClient> onFinish;

    public ClientCloseOperation(ServerClient client, Consumer<ServerClient> onFinish) {
        this.client = client;
        this.onFinish = onFinish;
    }

    @Override
    public Operation complete() {
        this.client.close().complete();
        this.onFinish.accept(this.client);
        return this;
    }
}
