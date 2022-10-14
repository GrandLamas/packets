package de.lama.packets.operation;

public interface Operation {

    Operation queue();

    Operation complete();

}
