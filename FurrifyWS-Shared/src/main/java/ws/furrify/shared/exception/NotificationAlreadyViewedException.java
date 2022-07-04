package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class NotificationAlreadyViewedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public NotificationAlreadyViewedException(String message) {
        super(message);
    }

}