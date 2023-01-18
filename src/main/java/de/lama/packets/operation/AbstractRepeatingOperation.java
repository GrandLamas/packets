package de.lama.packets.operation;

import java.util.concurrent.Future;

public abstract class AbstractRepeatingOperation implements RepeatingOperation {

    private Future<?> task;

    protected abstract Future<?> start();

    @Override
    public Operation queue() {
        if (this.isRunning()) {
            throw new IllegalStateException("Already running");
        }

        this.task = this.start();
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
