package ws.furrify.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Http status codes in enum.
 *
 * @author Skyte
 */
@Getter
@RequiredArgsConstructor
public enum HttpStatus {
    /**
     * Http status codes
     */

    TIMED_OUT(-1),
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    RESET_CONTENT(205),
    PARTIAL_CONTENT(206),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    NOT_ACCEPTABLE(406),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(502),
    TOO_MANY_REQUESTS(429),
    SERVICE_UNAVAILABLE(503);


    private final int status;

    /**
     * Convert http status code to enum instance.
     *
     * @param status Http status code ex. 404.
     * @return HttpStatus instance corresponding to given status code.
     */
    public static HttpStatus of(int status) {
        return switch (status) {
            case -1 -> TIMED_OUT;
            case 200 -> OK;
            case 201 -> CREATED;
            case 202 -> ACCEPTED;
            case 204 -> NO_CONTENT;
            case 205 -> RESET_CONTENT;
            case 206 -> PARTIAL_CONTENT;
            case 400 -> BAD_REQUEST;
            case 401 -> UNAUTHORIZED;
            case 403 -> FORBIDDEN;
            case 404 -> NOT_FOUND;
            case 406 -> NOT_ACCEPTABLE;
            case 429 -> TOO_MANY_REQUESTS;
            case 500 -> INTERNAL_SERVER_ERROR;
            case 502 -> NOT_IMPLEMENTED;
            case 503 -> SERVICE_UNAVAILABLE;
            default -> null;
        };
    }

}
