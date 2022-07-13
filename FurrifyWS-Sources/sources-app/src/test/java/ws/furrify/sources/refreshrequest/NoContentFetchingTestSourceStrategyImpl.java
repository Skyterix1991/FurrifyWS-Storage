package ws.furrify.sources.refreshrequest;

import ws.furrify.sources.source.strategy.SourceStrategy;

import java.util.HashMap;

class NoContentFetchingTestSourceStrategyImpl implements SourceStrategy {
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
