package ws.furrify.sources.providers.deviantart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;

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

    /**
     * Deviation direct URL.
     */
    @JsonProperty("url")
    private URI uri;
}
