package ws.furrify.sources.source.strategy;

import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.KeycloakServiceClientImpl;
import ws.furrify.sources.providers.patreon.PatreonScrapperClient;
import ws.furrify.sources.providers.patreon.PatreonScrapperClientImpl;
import ws.furrify.sources.providers.patreon.PatreonServiceClient;
import ws.furrify.sources.providers.patreon.PatreonServiceClientImpl;

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

    private final static String DOMAIN = "patreon.com";
    private final static String WWW_SUBDOMAIN = "www.";
    private final static String POST_URL_FIELD = "url";
    private final static String CAMPAIGN_URL_FIELD = "url";

    private final static String POST_PATH = "posts";
    private final static String CAMPAIGN_USER_PATH = "/user/?u=";

    private final static String CAMPAIGN_ID_FIELD = "campaign_id";
    private final static String POST_ID_FIELD = "post_id";

    private final static byte POSTS_PATH_POSITION_IN_URI = 1;
    private final static byte POST_ID_PATH_POSITION_IN_URI = 2;

    private final static byte POST_PATH_SEGMENTS = 3;
    private final static byte CAMPAIGN_USERNAME_PATH_SEGMENTS = 2;

    private final PatreonScrapperClient patreonScrapperClient;
    private final KeycloakServiceClient keycloakService;
    private final PatreonServiceClient patreonService;

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
    public ValidationResult validateMedia(final HashMap<String, String> requestData) {

        if (requestData.get(POST_URL_FIELD) == null || requestData.get(POST_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("Post url is required.");
        }

        URI uri;

        // Check if URL is valid
        try {
            uri = new URI(requestData.get(POST_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(requestData.get(POST_URL_FIELD), "Domain is missing.");
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
        if (path.length < POST_PATH_SEGMENTS || !path[POSTS_PATH_POSITION_IN_URI].equals(POST_PATH)) {
            return ValidationResult.invalid("Post url is invalid.");
        }

        // Extract post id from path
        String postIdRaw = path[POST_ID_PATH_POSITION_IN_URI];

        int postId;

        // Sanitize the id (patreon does this thing that adds letters to id for whatever purpose)
        try {
            postId = Integer.parseInt(postIdRaw.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return ValidationResult.invalid("Post url is invalid.");
        }


        try {
            // If post with that id doesn't exist
            if (!patreonScrapperClient.existsPost(postId)) {
                return ValidationResult.invalid("Post doesn't exist.");
            }
        } catch (IOException e) {
            return ValidationResult.invalid("Unknown error has occurred while communicating with patreon.");
        }

        // Save post id to data
        HashMap<String, String> data = new HashMap<>(1);
        data.put(POST_ID_FIELD, Integer.toString(postId));

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateUser(final HashMap<String, String> requestData) {

        if (requestData.get(CAMPAIGN_URL_FIELD) == null || requestData.get(CAMPAIGN_URL_FIELD).isBlank()) {
            return ValidationResult.invalid("Campaign url is required.");
        }

        URI uri;

        // Check if URL is valid
        try {
            uri = new URI(requestData.get(CAMPAIGN_URL_FIELD));
            if (uri.getHost() == null) {
                throw new URISyntaxException(requestData.get(CAMPAIGN_URL_FIELD), "Domain is missing.");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        // If url does not contain correct domain
        if (!uri.getHost().replace(WWW_SUBDOMAIN, "").equalsIgnoreCase(DOMAIN)) {
            return ValidationResult.invalid("Campaign url is invalid.");
        }

        // Extracted campaignId
        int campaignId;

        // If matches /user?u=number_here pattern
        if (uri.getPath().contains(CAMPAIGN_USER_PATH)) {
            int idQueryParamIndex = uri.getPath().indexOf("?u=");

            // Cut url to start position of query param id index
            String tmpCampaignId = uri.getPath().substring(idQueryParamIndex);

            // Check if another query param is present in url
            int idQueryParamEndIndex = tmpCampaignId.indexOf("&");
            if (idQueryParamEndIndex != -1) {
                // Remove all following params and "&" symbol
                tmpCampaignId = tmpCampaignId.substring(0, idQueryParamEndIndex - 1);
            }

            // Convert tmp id form url to campaign id int
            try {
                campaignId = Integer.parseInt(tmpCampaignId);
            } catch (NumberFormatException e) {
                return ValidationResult.invalid("Campaign url is invalid.");
            }

            // If matches /username_here pattern
        } else {
            String[] path = uri.getPath().split("[/\\\\]");

            // Check if slash is present between domain and username ex. patreon.com/username
            if (path.length < CAMPAIGN_USERNAME_PATH_SEGMENTS) {
                return ValidationResult.invalid("Campaign url is invalid.");
            }

            // Currently the only way to get campaign id from weird patreon url is scrapping it from their website...
            try {
                campaignId = patreonScrapperClient.getCampaignId(requestData.get(CAMPAIGN_URL_FIELD));
            } catch (IOException e) {
                return ValidationResult.invalid("Campaign url is invalid.");
            }

        }

        // Save campaign id to data
        HashMap<String, String> data = new HashMap<>(1);
        data.put(CAMPAIGN_ID_FIELD, Integer.toString(campaignId));

        return ValidationResult.valid(data);
    }

    @Override
    public ValidationResult validateAttachment(final HashMap<String, String> data) {
        return validateMedia(data);
    }
}
