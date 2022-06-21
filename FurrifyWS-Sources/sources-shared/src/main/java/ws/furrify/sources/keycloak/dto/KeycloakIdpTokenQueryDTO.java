package ws.furrify.sources.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Identity provider token details from current user.
 *
 * @author Skyte
 */
@Data
public class KeycloakIdpTokenQueryDTO {
    /**
     * Identity provider access token.
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Token type ex. Bearer.
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Expire time in seconds.
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * Refresh token.
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Scopes in the token.
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * Has it succeeded? ex. success
     */
    @JsonProperty("status")
    private String status;
}
