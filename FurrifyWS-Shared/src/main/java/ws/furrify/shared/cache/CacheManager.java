package ws.furrify.shared.cache;

import java.time.Duration;

public interface CacheManager<K, V> {
    boolean exists(K key);

    V get(K key);

    void put(K key, V value, Duration expiration);
}
