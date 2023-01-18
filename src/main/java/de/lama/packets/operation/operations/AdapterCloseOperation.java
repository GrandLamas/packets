package de.lama.packets.operation.operations;

import de.lama.packets.NetworkAdapter;
import de.lama.packets.operation.AbstractOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.Closeable;
import java.util.Set;

public class AdapterCloseOperation() extends AbstractOperation implements Operation {

    private final NetworkAdapter adapter;
    private final ExceptionHandler exceptionHandler;
    private final ParallelOperationExecutor operationCloser;
    private final TransceiverCloseOperation operation;

    public AdapterCloseOperation(NetworkAdapter adapter, Set<RepeatingOperation> operations, ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.operation = new TransceiverCloseOperation(operations, exceptionHandler);
    }

    @Override
    public Operation complete() {
        this.operationCloser.complete()
        this.exceptionHandler.operate(this.adapter::close, "Could not close socket");
        return this;
    }
}
