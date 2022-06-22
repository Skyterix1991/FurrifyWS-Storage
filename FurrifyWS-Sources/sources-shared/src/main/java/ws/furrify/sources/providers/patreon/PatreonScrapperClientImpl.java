package ws.furrify.sources.providers.patreon;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ws.furrify.shared.exception.HttpStatus;

import java.io.IOException;

/**
 * Implementation of Patreon scrapper client using JSoup.
 *
 * @author Skyte
 */
public class PatreonScrapperClientImpl implements PatreonScrapperClient {

    private final static String POST_URL_BASE = "https://www.patreon.com/api/posts/";

    private final static String CAMPAIGN_IMAGE_PROPERTY = "og:image";
    private final static String CAMPAIGN_IN_IMAGE_URL = "campaign";

    @Override
    public int getCampaignId(final String url) throws IOException {
        Document document = addParamsToJsoupConnection(
                Jsoup.connect(url)
        ).get();

        Elements metaTags = document.head().getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String propertyTag = metaTag.attr("property");
            if (propertyTag.equals(CAMPAIGN_IMAGE_PROPERTY)) {
                // Get image url and split it into chunks by "/"
                String[] imageUrl = metaTag.attr("content").split("/");
                int campaignIdPositionInUrl = -1;

                for (int i = 0; i <= imageUrl.length - 1; i++) {
                    // Search for 'campaign' in image url
                    if (imageUrl[i].equals(CAMPAIGN_IN_IMAGE_URL)) {
                        // Save campaign id position in array
                        campaignIdPositionInUrl = i + 1;

                        break;
                    }
                }

                // If campaign id was not found
                if (campaignIdPositionInUrl == -1) {
                    throw new IOException("Campaign not found.");
                }

                return Integer.parseInt(imageUrl[campaignIdPositionInUrl]);
            }
        }

        // If meta tag not found
        throw new IOException("Campaign not found.");
    }


    @Override
    public boolean existsPost(final int postId) throws IOException {
        try {
            addParamsToJsoupConnection(
                    Jsoup.connect(POST_URL_BASE + postId)
            ).get();

            // Connection successful
            return true;

        } catch (HttpStatusException e) {
            // If post not found
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.getStatus()) {
                return false;
            }

            throw new IOException("Unhandled http error has occurred.");
        }
    }

    private Connection addParamsToJsoupConnection(Connection connection) {
        return connection
                .header("Accept", "*/*")
                .ignoreContentType(true);
    }

}
