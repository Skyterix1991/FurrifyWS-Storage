package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/artists/{artistId}/sources/refreshes")
@RequiredArgsConstructor
class CommandRefreshRequestController {

    private final RefreshRequestFacade refreshRequestFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_refresh_request') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> createRefreshRequest(@PathVariable UUID userId,
                                                  @PathVariable UUID artistId,
                                                  KeycloakAuthenticationToken keycloakAuthenticationToken,
                                                  HttpServletResponse response) {
        response.addHeader("Id",
                refreshRequestFacade.createRefreshRequest(userId, artistId).toString()
        );

        return ResponseEntity.accepted().build();
    }

}
