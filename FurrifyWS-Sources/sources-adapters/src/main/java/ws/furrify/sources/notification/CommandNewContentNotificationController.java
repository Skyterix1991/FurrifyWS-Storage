package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/sources/{sourceId}/notifications/{notificationId}")
@RequiredArgsConstructor
class CommandNewContentNotificationController {

    private final NewContentNotificationFacade newContentNotificationFacade;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('mark_new_content_notification_viewed_request') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> markNewContentNotificationAsViewed(@PathVariable UUID userId,
                                                                @PathVariable UUID sourceId,
                                                                @PathVariable UUID notificationId,
                                                                KeycloakAuthenticationToken keycloakAuthenticationToken) {

        newContentNotificationFacade.markAsViewed(userId, sourceId, notificationId);

        return ResponseEntity.accepted().build();
    }

}
