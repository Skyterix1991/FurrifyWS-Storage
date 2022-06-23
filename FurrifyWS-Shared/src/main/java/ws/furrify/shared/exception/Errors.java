package ws.furrify.shared.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

/**
 * Class contains error messages enum which can be accessed using getErrorMessage().
 * Messages can contain parenthesis which can be filled using ex. Your id is {0}! and use method getErrorMessage(3);
 * Messages also can contain multiple parenthesis all can be filled using ex. Your id is {0} and your name is {1}! and use method getErrorMessage("3", "John")
 * It would be nice to also indicate what value was filled for ex. [uuid={0}].
 * <p>
 * Each exception that wants to use those error messages should be registered in RestExceptionControllerAdvice.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public enum Errors {
    /**
     * Exception types.
     */

    NO_RECORD_FOUND("Record [uuid={0}] was not found."),
    STRATEGY_NOT_FOUND("Strategy [strategy={0}] was not found."),
    NO_TAG_FOUND("Tag [value={0}] was not found."),
    RECORD_ALREADY_EXISTS("Record [uuid={0}] already exists."),
    TAG_ALREADY_EXISTS("Tag [value={0}] already exists."),
    BAD_REQUEST("Given request data is invalid."),
    CHAIN_OF_REQUESTS_BROKEN("There was an exception when contacting [name={0}] microservice."),
    CHAIN_OF_REQUESTS_UNAUTHORIZED("Chain of requests was broken with unauthorized call to [name={0}] microservice. " +
            "Make sure you have all roles required for this request."),
    PREFERRED_NICKNAME_IS_NOT_VALID("Preferred nickname [nickname={0}] is not in nicknames array."),
    PREFERRED_NICKNAME_IS_TAKEN("Preferred nickname [nickname={0}] is already taken."),
    NICKNAMES_CANNOT_BE_EMPTY("Nicknames array must not be empty."),
    FILE_UPLOAD_FAILED("File upload to remote storage has failed."),
    FILE_UPLOAD_CANNOT_CREATE_PATH("File upload can't create path to file."),
    FILE_CONTENT_IS_CORRUPTED("File content is corrupted."),
    FILE_EXTENSION_IS_NOT_MATCHING_CONTENT("File extension is not matching provided file content or filename is invalid."),
    THUMBNAIL_CONTENT_IS_INVALID("Thumbnail extension must be .jpg or .jpeg."),
    MISSING_STRATEGY("Strategy was not found in given package [class={0}]."),
    VALIDATION_FAILED("Validation failed for [strategy={0}] with [error={1}]."),
    FILENAME_IS_INVALID("Given filename [filename={0}] is invalid."),
    VIDEO_FRAME_EXTRACTION_FAILED("Video frame extraction for thumbnail has failed."),
    EXTERNAL_PROVIDER_SERVER_SIDE_ERROR("External provider [provider={0}] has encountered a server error on their side. Try again."),
    EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED("External provider [provider={0}] token has expired. You will need to reconnect it in account options."),
    EXTERNAL_PROVIDER_TOO_MANY_REQUESTS("External provider [provider={0}] API is overloaded. Please try again later."),
    HARD_LIMIT_FOR_ENTITY_TYPE("Hard limit of [limit={0}] has been reached for [entity={1}], further create requests will not be accepted."),
    UNIDENTIFIED("Unknown error occurred."),
    FILE_HASH_DUPLICATE_IN_POST("File with this [md5={0}] hash already exists in this post with [uuid={1}]."),
    NO_ARTIST_SOURCES_FOUND_FOR_REFRESH("Artist [uuid={0}] contains no sources to be refreshed.");


    private final String errorMessage;

    public String getErrorMessage(Object... data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage(Object data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}