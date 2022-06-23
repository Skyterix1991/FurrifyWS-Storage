package ws.furrify.sources.providers.deviantart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * User Deviations dto received from DeviantArt API.
 *
 * @author Skyte
 */
@Data
public class DeviantArtUserDeviationsQueryDTO {
    /**
     * Does user still has more deviations than returned with this query (Not including skipped ones with offset).
     */
    @JsonProperty("has_more")
    private Boolean hasMore;

    /**
     * Next offset to be used to receive further deviations.
     */
    @JsonProperty("next_offset")
    private Integer nextOffset;

    /**
     * List of deviations in this response.
     */
    @JsonProperty("results")
    private List<DeviantArtDeviationQueryDTO> deviations;
}
