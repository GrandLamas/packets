package de.lama.packets;

import com.google.gson.annotations.SerializedName;

public interface GamePacket {

    @SerializedName("id")
    long getId();

}
