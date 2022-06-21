package ws.furrify.sources.providers.deviantart;

import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.extern.slf4j.Slf4j;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.ExternalProviderServerSideErrorException;
import ws.furrify.shared.exception.ExternalProviderTokenExpiredException;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

/**
 * Implementation of DeviantArt communication service using Feign.
 *
 * @author Skyte
 */
@Slf4j
public class DeviantArtServiceClientImpl implements DeviantArtServiceClient {

    private final DeviantArtServiceClient deviantArtServiceClient;

    private final static String ID = "deviantart";

    public DeviantArtServiceClientImpl() {
        FeignDecorators decorators = FeignDecorators.builder()
                .withFallbackFactory(DeviantArtServiceClientFallback::new)
                .build();

        this.deviantArtServiceClient = Resilience4jFeign.builder(decorators)
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(DeviantArtServiceClient.class))
                .logLevel(Logger.Level.FULL)
                .target(DeviantArtServiceClient.class, "https://www.deviantart.com/api/v1/oauth2");
    }

    @Override
    public DeviantArtDeviationQueryDTO getDeviation(final String bearerToken, final String deviationId) {
        return deviantArtServiceClient.getDeviation(bearerToken, deviationId);
    }

    @Override
    public DeviantArtUserQueryDTO getUser(final String bearerToken, final String username) {
        return deviantArtServiceClient.getUser(bearerToken, username);
    }

    public static class DeviantArtServiceClientFallback implements DeviantArtServiceClient {
        private final Exception exception;

        public DeviantArtServiceClientFallback(Exception exception) {
            this.exception = exception;
        }

        @Override
        public DeviantArtDeviationQueryDTO getDeviation(final String bearerToken, final String deviationId) {
            var feignException = (FeignException) this.exception;

            HttpStatus status = HttpStatus.of(feignException.status());

            switch (status) {
                case NOT_FOUND -> {
                    return null;
                }

                case INTERNAL_SERVER_ERROR -> throw new ExternalProviderServerSideErrorException(Errors.EXTERNAL_PROVIDER_SERVER_SIDE_ERROR.getErrorMessage(ID));

                case UNAUTHORIZED -> throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage(ID));

                default -> {
                    log.error("DeviantArt identity provider endpoint returned unhandled status " + status.getStatus() + ".");

                    throw feignException;
                }
            }
        }

        @Override
        public DeviantArtUserQueryDTO getUser(final String bearerToken, final String username) {
            var feignException = (FeignException) this.exception;

            HttpStatus status = HttpStatus.of(feignException.status());

            switch (status) {
                case NOT_FOUND -> {
                    return null;
                }

                case INTERNAL_SERVER_ERROR -> throw new ExternalProviderServerSideErrorException(Errors.EXTERNAL_PROVIDER_SERVER_SIDE_ERROR.getErrorMessage(ID));

                case UNAUTHORIZED -> throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage(ID));

                default -> {
                    log.error("DeviantArt identity provider endpoint returned unhandled status " + status.getStatus() + ".");

                    throw feignException;
                }
            }
        }
    }
}
