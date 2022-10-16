package de.lama.packets.transceiver;

import de.lama.packets.util.ExceptionHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractTransceiverBuilder {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(10);

    protected ScheduledExecutorService pool = SERVICE;
    protected int tickrate = 16;
    protected ExceptionHandler exceptionHandler;

}
