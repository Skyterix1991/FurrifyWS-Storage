package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.stereotype.Component;
import ws.furrify.shared.cache.CacheManager;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class CacheManagerImpl implements CacheManager {

    private final static String CACHE_NAME = "sources";
    private final RemoteCacheManager cacheManager;

    @Override
    public boolean exists(final String key) {
        return cacheManager.getCache(CACHE_NAME).get(key) != null;
    }

    @Override
    public <V> V get(final String key, final Class<V> cast) {
        return cast.cast(cacheManager.getCache(CACHE_NAME).get(key));
    }

    @Override
    public <V> void put(final String key, final V value, final Duration expiration) {
        cacheManager.getCache(CACHE_NAME).put(key, value, expiration.toMillis(), TimeUnit.MILLISECONDS);
    }
}
