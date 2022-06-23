package ws.furrify.sources.providers.deviantart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Deviation dto received from DeviantArt API.
 *
 * @author Skyte
 */
@Data
public class DeviantArtDeviationQueryDTO {
    /**
     * Deviation unique id.
     */
    @JsonProperty("deviationid")
    private String deviationId;

}
