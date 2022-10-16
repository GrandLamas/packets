package de.lama.packets.operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractRepeatingOperation implements RepeatingOperation {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    protected Future<?> task;

    @Override
    public Operation queue() {
        this.task = POOL.submit(this::complete);
        return this;
    }

    @Override
    public void stop() {
        if (!this.isRunning()) throw new IllegalStateException("Operation not running");
        this.task.cancel(true);
        this.task = null;
    }

    @Override
    public boolean isRunning() {
        return this.task != null;
    }

    @Override
    public int hashCode() {
        return this.task.hashCode();
    }
}
