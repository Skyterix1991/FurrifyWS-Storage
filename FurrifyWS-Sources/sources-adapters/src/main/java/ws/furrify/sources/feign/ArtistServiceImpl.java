package ws.furrify.sources.feign;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import ws.furrify.shared.exception.ChainOfRequestsBrokenException;
import ws.furrify.shared.exception.ChainOfRequestsUnauthorizedException;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.artists.dto.query.ArtistDetailsQueryDTO;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistServiceClient {

    private final ArtistsServiceClientImpl artistsServiceClient;

    private final static String NAME = "artists";

    @Bulkhead(name = "getUserArtist", fallbackMethod = "getUserArtistFallback")
    @Override
    public ArtistDetailsQueryDTO getUserArtist(final UUID userId, final UUID artistId) {
        return artistsServiceClient.getUserArtist(userId, artistId);
    }

    private ArtistDetailsQueryDTO getUserArtistFallback(Throwable throwable) {
        var exception = (FeignException) throwable;

        HttpStatus status = HttpStatus.of(exception.status());

        switch (status) {
            case NOT_FOUND -> {
                return null;
            }

            case FORBIDDEN -> throw new ChainOfRequestsUnauthorizedException(Errors.CHAIN_OF_REQUESTS_UNAUTHORIZED.getErrorMessage(NAME));

            default -> throw new ChainOfRequestsBrokenException(Errors.CHAIN_OF_REQUESTS_BROKEN.getErrorMessage(NAME));
        }
    }

    /**
     * Implements Artists Service Client as a Feign Client.
     */
    @FeignClient(name = "ARTISTS-SERVICE")
    private interface ArtistsServiceClientImpl extends ArtistServiceClient {
    }
}
