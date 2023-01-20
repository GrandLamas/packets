package de.lama.packets.operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SimpleOperation implements Operation {

    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final Consumer<Boolean> async;

    public SimpleOperation(Consumer<Boolean> async) {
        this.async = async;
    }

    @Override
    public Operation complete() {
        this.async.accept(false);
        return this;
    }

    @Override
    public Operation queue() {
        this.service.submit(() -> this.async.accept(true));
        return this;
    }
}
