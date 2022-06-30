package ws.furrify.shared.cache;

import java.time.Duration;

public interface CacheManager {
    boolean exists(final String key);

    <V> V get(final String key, final Class<V> cast);

    <V> void put(final String key, final V value, final Duration expiration);
}
