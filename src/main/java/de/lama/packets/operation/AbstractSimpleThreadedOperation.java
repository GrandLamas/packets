package de.lama.packets.operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractSimpleThreadedOperation implements Operation {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    @Override
    public void queue() {
        POOL.submit(this::complete);
    }
}
