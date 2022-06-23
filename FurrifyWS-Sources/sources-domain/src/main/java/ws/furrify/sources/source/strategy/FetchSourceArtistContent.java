package ws.furrify.sources.source.strategy;

import lombok.NonNull;
import ws.furrify.shared.cache.CacheManager;

import java.util.HashMap;
import java.util.List;

/**
 * Strategy used to fetch content updates from different platform.
 * Ex. New posts on Patreon
 *
 * @param <M> Provider media value objects stored in cache list.
 * @param <A> Provider attachment value objects stored in cache list.
 * @author sky
 */
public interface FetchSourceArtistContent<M, A> {

    /**
     * Fetch user media using data given to source and contacting external provider or scrapping the data.
     *
     * @param data         Data saved in Source entity.
     * @param cacheManager Nullable cache manager. If null is used, values will be pulled directly from provider.
     * @return List of user posts.
     */
    List<M> fetchArtistMediaList(@NonNull final HashMap<String, String> data,
                                 final CacheManager<String, List<M>> cacheManager);

    /**
     * Fetch user attachment using data given to source and contacting external provider or scrapping the data.
     *
     * @param data         Data saved in Source entity.
     * @param cacheManager Nullable cache manager. If null is used, values will be pulled directly from provider.
     * @return List of user attachments.
     */
    List<A> fetchArtistAttachments(@NonNull final HashMap<String, String> data,
                                   final CacheManager<String, List<A>> cacheManager);

}
