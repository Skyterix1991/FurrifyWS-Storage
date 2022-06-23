package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class NoArtistSourcesFoundForRefreshException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public NoArtistSourcesFoundForRefreshException(String message) {
        super(message);
    }

}