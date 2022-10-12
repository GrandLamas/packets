package de.lama.packets;

import com.google.gson.annotations.SerializedName;

/**
 * Immutable
 */
public interface Packet extends Cloneable {

    @SerializedName("id")
    long getId();

}
