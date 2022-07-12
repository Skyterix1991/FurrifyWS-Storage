package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.sources.notification.dto.query.NewContentNotificationDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/sources/{sourceId}/notifications")
@RequiredArgsConstructor
class QueryNewContentNotificationController {

    private final NewContentNotificationQueryRepository newContentNotificationQueryRepository;
    private final PagedResourcesAssembler<NewContentNotificationDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping("/{notificationId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_new_content_notification_request') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<NewContentNotificationDetailsQueryDTO> getUserNewContentNotification(@PathVariable UUID userId,
                                                                                            @PathVariable UUID sourceId,
                                                                                            @PathVariable UUID notificationId,
                                                                                            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        NewContentNotificationDetailsQueryDTO notificationDetailsQueryDTO = newContentNotificationQueryRepository.findByOwnerIdAndSourceIdAndNotificationId(userId, sourceId, notificationId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(notificationId)));

        return addNewContentNotificationRelations(
                EntityModel.of(notificationDetailsQueryDTO)
        );
    }

    private EntityModel<NewContentNotificationDetailsQueryDTO> addNewContentNotificationRelations(EntityModel<NewContentNotificationDetailsQueryDTO> newContentNotificationQueryDtoModel) {
        var queryDto = newContentNotificationQueryDtoModel.getContent();
        // Check if model content is empty
        if (queryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryNewContentNotificationController.class).getUserNewContentNotification(
                queryDto.getOwnerId(),
                queryDto.getSourceId(),
                queryDto.getNotificationId(),
                null
        )).withSelfRel();

        var notificationsRel = linkTo(methodOn(QueryUserNewContentNotificationController.class).getUserNewContentNotifications(
                queryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("newContentNotifications");

        newContentNotificationQueryDtoModel.add(selfRel);
        newContentNotificationQueryDtoModel.add(notificationsRel);

        return newContentNotificationQueryDtoModel;
    }
}