package ws.furrify.sources.providers.patreon;

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
import ws.furrify.shared.exception.ExternalProviderForbiddenException;
import ws.furrify.shared.exception.ExternalProviderServerSideErrorException;
import ws.furrify.shared.exception.ExternalProviderTokenExpiredException;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.providers.patreon.dto.PatreonCampaignQueryDTO;
import ws.furrify.sources.providers.patreon.dto.PatreonPostQueryDTO;

/**
 * Implementation of Patreon communication service using Feign.
 *
 * @author Skyte
 */
@Slf4j
public class PatreonServiceClientImpl implements PatreonServiceClient {

    private final PatreonServiceClient patreonServiceClient;

    private final static String ID = "patreon";

    public PatreonServiceClientImpl() {
        FeignDecorators decorators = FeignDecorators.builder()
                .withFallbackFactory(PatreonServiceClientFallback::new)
                .build();

        this.patreonServiceClient = Resilience4jFeign.builder(decorators)
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(PatreonServiceClient.class))
                .logLevel(Logger.Level.FULL)
                .target(PatreonServiceClient.class, "https://www.patreon.com/api/oauth2/v2/");
    }

    @Override
    public PatreonCampaignQueryDTO getCampaign(final String bearerToken, final int campaignId) {
        return patreonServiceClient.getCampaign(bearerToken, campaignId);
    }

    @Override
    public PatreonPostQueryDTO getPost(final String bearerToken, final int postId) {
        return patreonServiceClient.getPost(bearerToken, postId);
    }


    public static class PatreonServiceClientFallback implements PatreonServiceClient {
        private final Exception exception;

        public PatreonServiceClientFallback(Exception exception) {
            this.exception = exception;
        }

        @Override
        public PatreonCampaignQueryDTO getCampaign(final String bearerToken, final int campaignId) {
            var feignException = (FeignException) this.exception;

            HttpStatus status = HttpStatus.of(feignException.status());

            switch (status) {
                case NOT_FOUND -> {
                    return null;
                }

                case INTERNAL_SERVER_ERROR -> throw new ExternalProviderServerSideErrorException(Errors.EXTERNAL_PROVIDER_SERVER_SIDE_ERROR.getErrorMessage(ID));

                case UNAUTHORIZED -> throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage(ID));

                default -> {
                    log.error("Patreon identity provider endpoint returned unhandled status " + status.getStatus() + ".");

                    throw feignException;
                }
            }
        }

        @Override
        public PatreonPostQueryDTO getPost(final String bearerToken, final int postId) {
            var feignException = (FeignException) this.exception;

            HttpStatus status = HttpStatus.of(feignException.status());

            switch (status) {
                case NOT_FOUND -> {
                    return null;
                }

                case INTERNAL_SERVER_ERROR -> throw new ExternalProviderServerSideErrorException(Errors.EXTERNAL_PROVIDER_SERVER_SIDE_ERROR.getErrorMessage(ID));

                case UNAUTHORIZED -> throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage(ID));

                case FORBIDDEN -> throw new ExternalProviderForbiddenException(Errors.EXTERNAL_PROVIDER_FORBIDDEN.getErrorMessage(ID));

                default -> {
                    log.error("Patreon identity provider endpoint returned unhandled status " + status.getStatus() + ".");

                    throw feignException;
                }
            }
        }
    }
}
