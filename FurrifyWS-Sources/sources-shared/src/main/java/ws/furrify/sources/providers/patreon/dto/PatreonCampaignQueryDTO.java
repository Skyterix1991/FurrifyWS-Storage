package ws.furrify.sources.providers.patreon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Campaign dto received from Patreon API.
 *
 * @author Skyte
 */
@Data
public class PatreonCampaignQueryDTO {
    /**
     * Data object.
     */
    @JsonProperty("data")
    private CampaignData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignData {
        /**
         * Requested campaign attributes.
         */
        @JsonProperty("attributes")
        private CampaignAttributes attributes;
    }

    @Data
    public static class CampaignAttributes {
    }

}
