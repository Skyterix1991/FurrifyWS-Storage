package ws.furrify.sources.source.strategy;

import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.KeycloakServiceClientImpl;
import ws.furrify.sources.providers.patreon.PatreonScrapperClient;
import ws.furrify.sources.providers.patreon.PatreonScrapperClientImpl;
import ws.furrify.sources.providers.patreon.PatreonServiceClient;
import ws.furrify.sources.providers.patreon.PatreonServiceClientImpl;
import ws.furrify.sources.providers.patreon.dto.PatreonCampaignQueryDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Version 1 of Patreon Art Strategy.
 * Represents Patreon.com website for creator posts.
 *
 * @author sky
 */
public class PatreonV1SourceStrategy implements SourceStrategy {

    private final static String BROKER_ID = "patreon";
    private final static String DOMAIN = "patreon.com";
    private final static String WWW_SUBDOMAIN = "www.";
    private final static String POST_URL_FIELD = "url";
    private final static String CAMPAIGN_URL_FIELD = "url";

    private final static String POST_PATH = "posts";
    private final static byte POSTS_PATH_POSITION_IN_URI = 1;
    private final static byte POST_ID_PATH_POSITION_IN_URI = 2;
    private final static byte CAMPAIGN_NAME_PATH_POSITION_IN_URI = 1;

    private final static byte POST_PATH_SEGMENTS = 3;
    private final static byte CAMPAIGN_PATH_SEGMENTS = 2;

    private final KeycloakServiceClient keycloakService;
    private final PatreonServiceClient patreonService;
    private final PatreonScrapperClient patreonScrapperClient;

    public PatreonV1SourceStrategy() {
        this.keycloakService = new KeycloakServiceClientImpl();
        this.patreonService = new PatreonServiceClientImpl();
        this.patreonScrapperClient = new PatreonScrapperClientImpl();
    }

    public PatreonV1SourceStrategy(final KeycloakServiceClient keycloakService,
                                      final PatreonServiceClient patreonService) {
        this.keycloakService = keycloakService;
        this.patreonService = patreonService;
        this.patreonScrapperClient = new PatreonScrapperClientImpl();
    }

    @Override
    public ValidationResult validateMedia(final HashMap<String, String> data) {
        if (data.get(POST_URL_FIELD) == null || data.get(POST_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("Post url is required.");
        }

        URI uri;

        try {
            uri = new URI(data.get(POST_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(data.get(POST_URL_FIELD), "Domain is missing.");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("Post url is invalid.");
        }

        // If url does not contain correct domain
        if (!uri.getHost().replace(WWW_SUBDOMAIN, "").equalsIgnoreCase(DOMAIN)) {
            return ValidationResult.invalid("Post url is invalid.");
        }

        String[] path = uri.getPath().split("[/\\\\]");
        // If there are not enough path params in url or post path is not present
        if (path.length < POST_PATH_SEGMENTS ||
                !path[POSTS_PATH_POSITION_IN_URI].equals(POST_PATH)) {
            return ValidationResult.invalid("Post url is invalid.");
        }

        // Extract post id from path
        String postIdRaw = path[POST_ID_PATH_POSITION_IN_URI];
        // Sanitize the id (patreon does this thing that adds letters to id for whatever purpose)
        int postId = Integer.parseInt(postIdRaw.replaceAll("[^0-9.]", ""));

        // TODO Uncomment when patreon api updated to support post public information fetch
        // Get post from patreon api
/*        PatreonPostQueryDTO postQueryDTO = patreonService.getPost(
                SourceStrategy.getKeycloakBearerToken(keycloakService, BROKER_ID),
                postId
        );
        if (postQueryDTO == null) {
            return ValidationResult.invalid("Post was not found.");
        }*/

        // Save post id to data
        data.put("postId", Integer.toString(postId));

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateUser(final HashMap<String, String> data) {
        if (data.get(CAMPAIGN_URL_FIELD) == null || data.get(CAMPAIGN_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("Campaign url is required.");
        }

        URI uri;

        try {
            uri = new URI(data.get(CAMPAIGN_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(data.get(CAMPAIGN_URL_FIELD), "Domain is missing.");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        // If url does not contain correct domain
        if (!uri.getHost().replace(WWW_SUBDOMAIN, "").equalsIgnoreCase(DOMAIN)) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        String[] path = uri.getPath().split("[/\\\\]");
        // If there are not enough path params in url
        if (path.length < CAMPAIGN_PATH_SEGMENTS) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        int campaignId;

        try {
            // TODO There is no cleaner way for now to get correct id from weird links that work
            campaignId = patreonScrapperClient.getCampaignId(data.get(CAMPAIGN_URL_FIELD));
        } catch (IOException e) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        // Get campaign from patreon api
        PatreonCampaignQueryDTO campaignQueryDTO = patreonService.getCampaign(
                SourceStrategy.getKeycloakBearerToken(keycloakService, BROKER_ID),
                campaignId
        );
        if (campaignQueryDTO == null) {
            return ValidationResult.invalid("Campaign was not found.");
        }

        // Save campaign id to data
        data.put("campaignId", Integer.toString(campaignId));

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateAttachment(final HashMap<String, String> data) {
        return validateMedia(data);
    }
}
