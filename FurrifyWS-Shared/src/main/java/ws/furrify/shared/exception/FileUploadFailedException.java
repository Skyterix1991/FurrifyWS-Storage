package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class FileUploadFailedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public FileUploadFailedException(String message) {
        super(message);
    }

}