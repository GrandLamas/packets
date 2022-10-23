package de.lama.packets.wrapper.cache;

public record CachedObject<T>(T object, long lastAccess) {

}
