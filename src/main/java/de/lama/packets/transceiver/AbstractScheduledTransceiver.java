package de.lama.packets.transceiver;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledTransceiver extends AbstractRepeatingOperation implements PacketTransceiver {

    private final ScheduledExecutorService pool;
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
    public Operation complete() {
        this.tick();
        return this;
    }

    @Override
    protected Future<?> start() {
        return this.pool.scheduleAtFixedRate(this::complete, this.tickrate, this.tickrate, TimeUnit.MILLISECONDS);
    }

    @Override
    public long getTickrate() {
        return this.tickrate;
    }
}
