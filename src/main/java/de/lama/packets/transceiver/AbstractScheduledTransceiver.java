package de.lama.packets.transceiver;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledTransceiver implements PacketTransceiver {

    private final ScheduledExecutorService pool;
    private ScheduledFuture<?> task;
    private final long tickrate;

    protected AbstractScheduledTransceiver(ScheduledExecutorService pool, int tickrate) {
        this.pool = pool;
        this.tickrate = this.calculateTickrate(tickrate);
    }

    private long calculateTickrate(int tickrate) {
        return 1000L / tickrate;
    }

    protected abstract void tick();

    @Override
    public void start() {
        this.task = this.pool.scheduleAtFixedRate(this::tick, this.tickrate, this.tickrate, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        this.task.cancel(true);
        this.task = null;
    }

    @Override
    public boolean isRunning() {
        return this.task != null;
    }

    @Override
    public long getTickrate() {
        return this.tickrate;
    }
}
