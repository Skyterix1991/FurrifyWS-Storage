package ws.furrify.sources.source.strategy;

import lombok.NonNull;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.sources.source.vo.RemoteContent;

import java.util.HashMap;
import java.util.Set;

/**
 * Strategy used to fetch content updates from different platform.
 * Ex. New posts on Patreon
 *
 * @author sky
 */
public interface SourceArtistContentFetcher {

    /**
     * Fetch user content using data given to source and contacting external provider or scrapping the data.
     *
     * @param data         Data saved in Source entity.
     * @param cacheManager Nullable cache manager. If null is used, values will be pulled directly from provider.
     * @return List of user posts.
     */
    Set<RemoteContent> fetchArtistContent(@NonNull final HashMap<String, String> data,
                                          @NonNull final String bearerToken,
                                          final CacheManager cacheManager);
}