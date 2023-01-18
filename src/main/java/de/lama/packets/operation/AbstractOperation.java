package de.lama.packets.operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractOperation implements Operation {

    private final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    @Override
    public Operation queue() {
        THREAD_POOL.submit(this::complete);
        return this;
    }
}
