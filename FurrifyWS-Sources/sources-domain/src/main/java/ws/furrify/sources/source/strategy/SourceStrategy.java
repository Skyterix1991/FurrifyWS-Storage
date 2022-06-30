package ws.furrify.sources.source.strategy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import ws.furrify.sources.source.jackson.SourceStrategyDeserializer;
import ws.furrify.sources.source.jackson.SourceStrategySerializer;

import java.util.HashMap;

/**
 * Source strategy used to manage and update content using provided unique identifiers.
 *
 * @author sky
 */
@JsonSerialize(using = SourceStrategySerializer.class)
@JsonDeserialize(using = SourceStrategyDeserializer.class)
public interface SourceStrategy {

    /**
     * Validate if given data is matching strategy requirements for remote media.
     *
     * @param data Data given in form of hash map containing information to access ex. username.
     * @return Instance of Validation Result.
     */
    ValidationResult validateMedia(HashMap<String, String> data);

    /**
     * Validate if given data is matching strategy requirements for remote artist.
     *
     * @param data Data given in form of hash map containing information to access ex. id.
     * @return Instance of Validation Result.
     */
    ValidationResult validateArtist(HashMap<String, String> data);

    /**
     * Validate if given data is matching strategy requirements for remote attachment.
     *
     * @param data Data given in form of hash map containing information to access ex. id.
     * @return Instance of Validation Result.
     */
    ValidationResult validateAttachment(HashMap<String, String> data);

    /**
     * Convert values from data to unique string.
     *
     * @param data Data given in form of hash map containing information to access ex. id.
     * @return Unique string identifying creator on given platform.
     */
    String getRemoteArtistIdentifier(HashMap<String, String> data);

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class ValidationResult {
        boolean valid;
        String reason;
        HashMap<String, String> data;

        /**
         * @return Instance of ValidationResult with valid field set as true.
         */
        public static ValidationResult valid(final HashMap<String, String> data) {
            return new ValidationResult(true, null, data);
        }

        /**
         * @return Instance of ValidationResult with valid field set as false and given reason.
         */
        public static ValidationResult invalid(final String reason) {
            return new ValidationResult(false, reason, null);
        }
    }
}
