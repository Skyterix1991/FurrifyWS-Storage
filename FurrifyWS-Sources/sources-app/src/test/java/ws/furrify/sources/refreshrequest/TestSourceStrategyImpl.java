package ws.furrify.sources.refreshrequest;

import lombok.NonNull;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.sources.source.strategy.SourceArtistContentFetcher;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.vo.RemoteContent;

import java.util.HashMap;
import java.util.Set;

class TestSourceStrategyImpl implements SourceStrategy, SourceArtistContentFetcher {
    @Override
    public Set<RemoteContent> fetchArtistContent(final @NonNull HashMap<String, String> data, final @NonNull String bearerToken, final CacheManager cacheManager) {
        return null;
    }

    @Override
    public ValidationResult validateMedia(final HashMap<String, String> data) {
        return null;
    }

    @Override
    public ValidationResult validateArtist(final HashMap<String, String> data) {
        return null;
    }

    @Override
    public ValidationResult validateAttachment(final HashMap<String, String> data) {
        return null;
    }

    @Override
    public String getRemoteArtistIdentifier(final HashMap<String, String> data) {
        return null;
    }
}
