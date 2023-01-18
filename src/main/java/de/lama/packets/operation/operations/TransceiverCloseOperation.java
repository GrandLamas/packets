package de.lama.packets.operation.operations;

import de.lama.packets.operation.AbstractOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.util.exception.ExceptionHandler;

import java.util.Collection;

public class TransceiverCloseOperation extends AbstractOperation implements Operation {

    private final Collection<RepeatingOperation> operations;
    private final ExceptionHandler exceptionHandler;

    public TransceiverCloseOperation(Collection<RepeatingOperation> operations, ExceptionHandler exceptionHandler) {
        this.operations = operations;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        this.operations.forEach(operation -> this.exceptionHandler.operate(operation::stop, "Could not close transceiver"));
        return this;
    }
}
