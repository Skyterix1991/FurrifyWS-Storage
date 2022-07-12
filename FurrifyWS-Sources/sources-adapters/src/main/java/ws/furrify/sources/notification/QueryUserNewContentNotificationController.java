package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.shared.pageable.PageableRequest;
import ws.furrify.sources.notification.dto.query.NewContentNotificationDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/notifications")
@RequiredArgsConstructor
class QueryUserNewContentNotificationController {

    private final NewContentNotificationQueryRepository newContentNotificationQueryRepository;
    private final PagedResourcesAssembler<NewContentNotificationDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_new_content_notifications_request') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<NewContentNotificationDetailsQueryDTO>> getUserNewContentNotifications(
            @PathVariable UUID userId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        Page<NewContentNotificationDetailsQueryDTO> notificationsPage = newContentNotificationQueryRepository.findAllByOwnerId(userId, pageable);

        PagedModel<EntityModel<NewContentNotificationDetailsQueryDTO>> notifications = pagedResourcesAssembler.toModel(
                notificationsPage
        );

        notifications.forEach(this::addNewContentNotificationRelations);

        // Add hateoas relation
        var selfRel = linkTo(methodOn(QueryUserNewContentNotificationController.class).getUserNewContentNotifications(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        notifications.add(selfRel);

        return notifications;
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
                null,
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