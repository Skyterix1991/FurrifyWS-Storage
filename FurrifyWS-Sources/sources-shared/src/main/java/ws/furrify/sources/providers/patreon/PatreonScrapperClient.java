package ws.furrify.sources.providers.patreon;
import java.io.IOException;

/**
 * Scrapper interface for Patreon site.
 *
 * @author Skyte
 */
public interface PatreonScrapperClient {
    /**
     * Check if Patreon campaign exists from url.
     *
     * @param url Url with Patreon campaign.
     * @return Campaign id from url.
     * @throws IOException Patreon campaign not found.
     */
    int getCampaignId(String url) throws IOException;

    /**
     * Check if Patreon post exists.
     *
     * @param postId Post id.
     * @return Post exists boolean value.
     * @throws IOException Patreon campaign not found.
     */
    boolean existsPost(int postId) throws IOException;
}