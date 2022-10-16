package de.lama.packets.server;

import de.lama.packets.client.Client;
import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;

import java.util.function.Consumer;

public class ClientCloseOperation extends AbstractThreadedOperation {

    private final Client client;
    private final Consumer<Client> onFinish;

    public ClientCloseOperation(Client client, Consumer<Client> onFinish) {
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
