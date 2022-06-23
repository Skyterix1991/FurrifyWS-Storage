package ws.furrify.sources.refreshrequest;

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
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;
import ws.furrify.sources.refreshrequest.dto.query.RefreshRequestDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/sources/refreshes")
@RequiredArgsConstructor
class QueryRefreshRequestController {

    private final SqlRefreshRequestQueryRepositoryImpl refreshRequestQueryRepository;
    private final PagedResourcesAssembler<RefreshRequestDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_refresh_requests') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<RefreshRequestDetailsQueryDTO>> getUserRefreshRequests(
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

        Page<RefreshRequestDetailsQueryDTO> refreshRequestsPage = refreshRequestQueryRepository.findAllByOwnerId(userId, pageable);

        PagedModel<EntityModel<RefreshRequestDetailsQueryDTO>> refreshRequests = pagedResourcesAssembler.toModel(
                refreshRequestsPage
        );

        refreshRequests.forEach(this::addRefreshRequestRelations);

        // Add hateoas relation
        var selfRel = linkTo(methodOn(QueryRefreshRequestController.class).getUserRefreshRequests(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        refreshRequests.add(selfRel);

        return refreshRequests;
    }

    @GetMapping("/{refreshRequestId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_refresh_request') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<RefreshRequestDetailsQueryDTO> getUserRefreshRequest(@PathVariable UUID userId,
                                                                            @PathVariable UUID refreshRequestId,
                                                                            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        RefreshRequestDetailsQueryDTO refreshRequestQueryDTO = refreshRequestQueryRepository.findByOwnerIdAndRefreshRequestId(userId, refreshRequestId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(refreshRequestId)));

        return addRefreshRequestRelations(
                EntityModel.of(refreshRequestQueryDTO)
        );
    }

    private EntityModel<RefreshRequestDetailsQueryDTO> addRefreshRequestRelations(EntityModel<RefreshRequestDetailsQueryDTO> refreshRequestQueryDtoModel) {
        var queryDto = refreshRequestQueryDtoModel.getContent();
        // Check if model content is empty
        if (queryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QueryRefreshRequestController.class).getUserRefreshRequest(
                queryDto.getOwnerId(),
                queryDto.getRefreshRequestId(),
                null
        )).withSelfRel();

        var refreshRequestsRel = linkTo(methodOn(QueryRefreshRequestController.class).getUserRefreshRequests(
                queryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userRefreshRequests");

        refreshRequestQueryDtoModel.add(selfRel);
        refreshRequestQueryDtoModel.add(refreshRequestsRel);

        return refreshRequestQueryDtoModel;
    }
}