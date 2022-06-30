package ws.furrify.sources.source.strategy;

import lombok.NonNull;

import java.util.HashMap;

/**
 * Version 1 of Patreon Art Strategy.
 * Represents Patreon.com website for creator posts.
 *
 * @author sky
 */
public class PatreonV1SourceStrategy implements SourceStrategy {

    @Override
    public ValidationResult validateMedia(@NonNull final HashMap<String, String> data) {
        // TODO Implement me

        return ValidationResult.valid(new HashMap<>(0));
    }

    @Override
    public ValidationResult validateArtist(@NonNull final HashMap<String, String> data) {
        // TODO Implement me

        return ValidationResult.valid(new HashMap<>(0));
    }

    @Override
    public ValidationResult validateAttachment(@NonNull final HashMap<String, String> data) {
        // TODO Implement me

        return ValidationResult.valid(new HashMap<>(0));
    }

    @Override
    public String getRemoteArtistIdentifier(final HashMap<String, String> data) {
        return null;
    }
}
