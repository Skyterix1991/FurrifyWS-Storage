package ws.furrify.sources.refreshrequest.vo;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

/**
 * Bearer token wrapper.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class RefreshRequestBearerToken {

    @NonNull
    String bearerToken;

    /**
     * Create bearer token from string.
     * Validate given value.
     *
     * @param bearerToken Tag title.
     * @return Bearer token instance.
     */
    public static RefreshRequestBearerToken of(@NonNull String bearerToken) {
        if (bearerToken.isBlank()) {
            throw new IllegalStateException("Bearer token [bearer_token=" + bearerToken + "] can't be blank.");
        }

        return new RefreshRequestBearerToken(bearerToken);
    }
}
