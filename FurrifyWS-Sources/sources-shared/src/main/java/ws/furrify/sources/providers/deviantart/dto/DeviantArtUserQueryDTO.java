package ws.furrify.sources.providers.deviantart.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Deviation dto received from DeviantArt API.
 *
 * @author Skyte
 */
@Data
public class DeviantArtUserQueryDTO {
    /**
     * User object.
     */
    @JsonProperty("user")
    private User user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        /**
         * User unique id.
         */
        @JsonProperty("userid")
        private String userId;

        /**
         * User username.
         */
        @JsonProperty("username")
        private String username;
    }

}
