package ws.furrify.sources.source.strategy;

import lombok.NonNull;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.KeycloakServiceClientImpl;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.providers.deviantart.DeviantArtScrapperClient;
import ws.furrify.sources.providers.deviantart.DeviantArtScrapperClientImpl;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClientImpl;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserDeviationsQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Version 1 of Deviant Art Strategy.
 * Represents DeviantArt.com website for artists and content.
 *
 * @author sky
 */
public class DeviantArtV1SourceStrategy implements
        SourceStrategy,
        FetchSourceArtistContent<DeviantArtDeviationQueryDTO, DeviantArtDeviationQueryDTO> {
    final static String BROKER_ID = "deviantart";

    final static String PROTOCOL = "https://";
    final static String DOMAIN = "deviantart.com";
    final static String WWW_SUBDOMAIN = "www.";
    final static String DEVIATION_URL_FIELD = "url";
    final static String USER_URL_FIELD = "url";
    final static String DEVIATION_ART_PATH = "art";
    final static String DEVIATION_ID_FIELD = "deviation_id";
    final static String USER_ID_FIELD = "userid";
    final static String USER_USERNAME_FIELD = "username";

    private final static byte DEVIATION_ART_PATH_POSITION_IN_URI = 2;
    private final static byte USERNAME_PATH_POSITION_IN_URI = 1;
    private final static byte DEVIATION_PATH_SEGMENTS = 4;
    private final static byte USER_PATH_SEGMENTS = 2;

    private final KeycloakServiceClient keycloakService;
    private final DeviantArtServiceClient deviantArtService;
    private final DeviantArtScrapperClient deviantArtScrapperClient;

    public DeviantArtV1SourceStrategy() {
        this.keycloakService = new KeycloakServiceClientImpl();
        this.deviantArtService = new DeviantArtServiceClientImpl();
        this.deviantArtScrapperClient = new DeviantArtScrapperClientImpl();
    }

    public DeviantArtV1SourceStrategy(@NonNull final KeycloakServiceClient keycloakService,
                                      @NonNull final DeviantArtServiceClient deviantArtService) {
        this.keycloakService = keycloakService;
        this.deviantArtService = deviantArtService;
        this.deviantArtScrapperClient = new DeviantArtScrapperClientImpl();
    }

    @Override
    public ValidationResult validateMedia(@NonNull final HashMap<String, String> requestData) {
        if (requestData.get(DEVIATION_URL_FIELD) == null || requestData.get(DEVIATION_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("Deviation url is required.");
        }

        URI uri;

        try {
            uri = new URI(requestData.get(DEVIATION_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(requestData.get(DEVIATION_URL_FIELD), "Domain is missing");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("Deviation url is invalid.");
        }

        // If url does not contain correct domain
        if (!uri.getHost().replace(WWW_SUBDOMAIN, "").equalsIgnoreCase(DOMAIN)) {
            return ValidationResult.invalid("Deviation url is invalid.");
        }

        String[] path = uri.getPath().split("[/\\\\]");
        // If there are not enough path params in url or art path is present
        if (path.length < DEVIATION_PATH_SEGMENTS || !path[DEVIATION_ART_PATH_POSITION_IN_URI].equals(DEVIATION_ART_PATH)) {
            return ValidationResult.invalid("Deviation url is invalid.");
        }

        String deviationId;

        try {
            /* Extract deviation id using scrapper cause deviant art api
               is weird and doesn't allow getting deviation by id in url */
            deviationId = deviantArtScrapperClient.scrapDeviationId(PROTOCOL + DOMAIN + uri.getPath());
        } catch (IOException e) {
            return ValidationResult.invalid("Deviation not found.");
        }

        DeviantArtDeviationQueryDTO deviationQueryDTO =
                deviantArtService.getDeviation(getProviderBearerToken(), deviationId);
        if (deviationQueryDTO == null) {
            return ValidationResult.invalid("Deviation not found.");
        }

        HashMap<String, String> data = new HashMap<>(2);
        data.put(DEVIATION_URL_FIELD, requestData.get(DEVIATION_URL_FIELD));
        // Save deviation id to data
        data.put(DEVIATION_ID_FIELD, deviationId);

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateArtist(@NonNull final HashMap<String, String> requestData) {
        if (requestData.get(USER_URL_FIELD) == null || requestData.get(USER_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("User url is required.");
        }

        URI uri;

        try {
            uri = new URI(requestData.get(USER_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(requestData.get(USER_URL_FIELD), "Domain is missing");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("User url is invalid.");
        }

        // If url does not contain correct domain
        if (!uri.getHost().replace(WWW_SUBDOMAIN, "").equalsIgnoreCase(DOMAIN)) {
            return ValidationResult.invalid("User url is invalid.");
        }

        String[] path = uri.getPath().split("[/\\\\]");
        // If there are not enough path params
        if (path.length < USER_PATH_SEGMENTS) {
            return ValidationResult.invalid("User url is invalid.");
        }

        DeviantArtUserQueryDTO userQueryDTO =
                deviantArtService.getUser(getProviderBearerToken(), path[USERNAME_PATH_POSITION_IN_URI]);
        if (userQueryDTO == null) {
            return ValidationResult.invalid("User not found.");
        }


        HashMap<String, String> data = new HashMap<>(2);
        data.put(USER_URL_FIELD, requestData.get(USER_URL_FIELD));
        data.put(USER_ID_FIELD, userQueryDTO.getUser().getUserId());
        data.put(USER_USERNAME_FIELD, userQueryDTO.getUser().getUsername());

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateAttachment(final HashMap<String, String> data) {
        return validateMedia(data);
    }

    @Override
    public List<DeviantArtDeviationQueryDTO> fetchArtistMediaList(@NonNull final HashMap<String, String> data,
                                                                  final CacheManager<String, List<DeviantArtDeviationQueryDTO>> cacheManager) {
        // Extract user name from source data
        String username = data.get(USER_USERNAME_FIELD);

        // Sanity check
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Source data doesn't contain required values.");
        }

        // Use cached response if user exists in cache
        if (cacheManager != null && cacheManager.exists(username)) {
            return cacheManager.get(username);
        }

        boolean hasMore = true;
        int nextOffset = 0;

        List<DeviantArtDeviationQueryDTO> deviations = new ArrayList<>();

        while (hasMore) {
            DeviantArtUserDeviationsQueryDTO response =
                    deviantArtService.getUserDeviations(
                            getProviderBearerToken(),
                            username,
                            DeviantArtServiceClient.API_DEVIATIONS_FETCH_LIMIT,
                            nextOffset
                    );

            hasMore = response.getHasMore();
            nextOffset = response.getNextOffset();

            // Add chunk of deviations received from API
            deviations.addAll(response.getDeviations());
        }

        if (cacheManager != null) {
            cacheManager.put(username, deviations, Duration.ofDays(1));
        }

        return deviations;
    }

    @Override
    public List<DeviantArtDeviationQueryDTO> fetchArtistAttachments(@NonNull final HashMap<String, String> data,
                                                                    final CacheManager<String, List<DeviantArtDeviationQueryDTO>> cacheManager) {
        return fetchArtistMediaList(data, cacheManager);
    }

    private String getProviderBearerToken() {
        return "Bearer " + keycloakService.getKeycloakIdentityProviderToken(null, PropertyHolder.REALM, BROKER_ID).getAccessToken();
    }
}
