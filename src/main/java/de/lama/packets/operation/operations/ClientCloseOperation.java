package de.lama.packets.operation.operations;

import de.lama.packets.client.Client;
import de.lama.packets.operation.AbstractOperation;
import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;

import java.util.function.Consumer;

public class ClientCloseOperation extends AbstractOperation implements Operation {

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
