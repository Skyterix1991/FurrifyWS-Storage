package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ExternalProviderTooManyRequestsException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;

    public ExternalProviderTooManyRequestsException(String message) {
        super(message);
    }

}