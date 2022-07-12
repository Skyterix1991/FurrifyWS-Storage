/*
 * MIT License
 * <p>
 * Copyright (c) 2019 Bruno Leite
 */
package ws.furrify.shared.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Global REST Controller Advice.
 *
 * @author Skyte
 */
public class RestExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected @NonNull
    ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
    ) {
        return responseEntity(new ApiError(BAD_REQUEST, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
    ) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(Errors.BAD_REQUEST.getErrorMessage());
        apiError.addValidationErrors(exception.getBindingResult().getFieldErrors());
        apiError.addValidationError(exception.getBindingResult().getGlobalErrors());

        return responseEntity(apiError);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException exception
    ) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(Errors.BAD_REQUEST.getErrorMessage());
        apiError.addValidationErrors(exception.getConstraintViolations());

        return responseEntity(apiError);
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatus status,
                                                        @NonNull WebRequest request) {
        return responseEntity(new ApiError(BAD_REQUEST, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException exception,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatus status,
                                                        @NonNull WebRequest request) {
        return responseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, Errors.BAD_REQUEST.getErrorMessage(), exception));
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("Method %s was not found for endpoint %s", exception.getHttpMethod(), exception.getRequestURL()));
        apiError.setDebugMessage(exception.getMessage());
        return responseEntity(apiError);
    }

    @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException exception) {
        return responseEntity(new ApiError(HttpStatus.NOT_FOUND, exception));
    }

    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(java.lang.IllegalArgumentException exception) {
        return responseEntity(new ApiError(BAD_REQUEST, exception));
    }

    @ExceptionHandler(java.lang.IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(java.lang.IllegalStateException exception) {
        return responseEntity(new ApiError(BAD_REQUEST, exception));
    }

    @ExceptionHandler(RequestRejectedException.class)
    protected ResponseEntity<Object> handleRequestRejectedException(RequestRejectedException exception) {
        return responseEntity(new ApiError(BAD_REQUEST, exception));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException exception,
                                                                  WebRequest request) {
        if (exception.getCause() instanceof ConstraintViolationException) {
            return responseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", exception.getCause()));
        }
        return responseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);

        apiError.setMessage(String.format("Parameter '%s' of field '%s' couldn't be converted to '%s'", exception.getName(), exception.getValue(), Objects.requireNonNull(exception.getRequiredType()).getSimpleName()));
        apiError.setDebugMessage(exception.getMessage());
        return responseEntity(apiError);
    }

    @ExceptionHandler(MultipartException.class)
    protected ResponseEntity<Object> handleMultipartException(
            MultipartException exception
    ) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(Errors.BAD_REQUEST.getErrorMessage());

        return responseEntity(apiError);
    }

    @ExceptionHandler({
            RecordNotFoundException.class,
            RecordAlreadyExistsException.class,
            ChainOfRequestsUnauthorizedException.class,
            ChainOfRequestsBrokenException.class,
            InvalidDataGivenException.class,
            FileContentIsCorruptedException.class,
            FileUploadFailedException.class,
            FileExtensionIsNotMatchingContentException.class,
            FileUploadCannotCreatePathException.class,
            StrategyNotFoundException.class,
            FilenameIsInvalidException.class,
            VideoFrameExtractionFailedException.class,
            ExternalProviderTokenExpiredException.class,
            ExternalProviderServerSideErrorException.class,
            HardLimitForEntityTypeException.class,
            NoArtistSourcesFoundForRefreshException.class,
            NotificationAlreadyViewedException.class
    })
    protected ResponseEntity<Object> handleException(
            RestException exception) {
        // Convert HttpStatus from shared module to spring HttpStatus
        HttpStatus httpStatus = HttpStatus.valueOf(exception.getStatus().getStatus());

        ApiError apiError = new ApiError(httpStatus, exception.getMessage(), (Throwable) exception);

        return responseEntity(apiError);
    }

    private ResponseEntity<Object> responseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}