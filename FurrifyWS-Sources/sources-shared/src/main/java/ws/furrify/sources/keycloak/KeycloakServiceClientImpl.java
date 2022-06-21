package ws.furrify.sources.keycloak;

import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.ExternalProviderTokenExpiredException;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;

import java.util.Objects;

/**
 * Implementation of Keycloak communication service using Feign.
 *
 * @author Skyte
 */
@Slf4j
public class KeycloakServiceClientImpl implements KeycloakServiceClient {

    private final KeycloakServiceClient keycloakServiceClient;

    @SneakyThrows
    public KeycloakServiceClientImpl() {
        FeignDecorators decorators = FeignDecorators.builder()
                .withFallbackFactory(KeycloakServiceClientFallback::new)
                .build();

        this.keycloakServiceClient = Resilience4jFeign.builder(decorators)
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(KeycloakServiceClient.class))
                .logLevel(Logger.Level.FULL)
                .target(KeycloakServiceClient.class, PropertyHolder.AUTH_SERVER);
    }

    public KeycloakServiceClientImpl(KeycloakServiceClient keycloakServiceClient) {
        this.keycloakServiceClient = keycloakServiceClient;
    }

    @Override
    public KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderToken(String bearerToken, final String realm, final String broker) {
        if (bearerToken == null) {
            bearerToken = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization");
        }

        return keycloakServiceClient.getKeycloakIdentityProviderToken(bearerToken, realm, broker);
    }


    public static class KeycloakServiceClientFallback implements KeycloakServiceClient {
        private final Exception exception;

        public KeycloakServiceClientFallback(Exception exception) {
            this.exception = exception;
        }

        @Override
        public KeycloakIdpTokenQueryDTO getKeycloakIdentityProviderToken(String bearerToken, final String realm, final String broker) {
            var feignException = (FeignException) this.exception;

            HttpStatus status = HttpStatus.of(feignException.status());

            if (status == HttpStatus.BAD_REQUEST) {
                throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage(broker));
            }

            log.error("Keycloak identity provider endpoint returned status " + status.getStatus() + ".");

            throw feignException;
        }
    }
}
