package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDtoFactory;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class RefreshRequestFacade {

    private final CreateRefreshRequest createRefreshRequestImpl;
    private final HandleRefreshRequest handleRefreshRequestImpl;
    private final RefreshRequestRepository refreshRequestRepository;
    private final RefreshRequestDtoFactory refreshRequestDtoFactory;

    private final RefreshRequestFactory refreshRequestFactory;

    /**
     * Handle incoming refresh request events.
     *
     * @param refreshRequestEvent Refresh request event instance received from kafka.
     */
    public void handleEvent(final UUID key, final RefreshRequestEvent refreshRequestEvent) {
        RefreshRequestDTO refreshRequestDTO = refreshRequestDtoFactory.from(key, refreshRequestEvent);

        switch (DomainEventPublisher.RefreshRequestEventType.valueOf(refreshRequestEvent.getState())) {
            case CREATED, UPDATED -> saveRefreshRequestToDatabase(refreshRequestDTO);

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + refreshRequestEvent.getState() + " Topic=refresh_request_events");
        }
    }

    /**
     * Creates refresh request.
     *
     * @param ownerId  Owner of refresh request to be created.
     * @param artistId Artist UUID.
     * @return Created record UUID.
     */
    public UUID createRefreshRequest(UUID ownerId, UUID artistId) {
        return createRefreshRequestImpl.createRefreshRequest(ownerId, artistId);
    }

    /**
     * Handle refresh request. Contact API's and do all necessary things to get new content from other platforms for particular artist.
     *
     * @param refreshRequestEvent Refresh request event to handle.
     */
    public void handleRefreshRequest(final UUID key, final RefreshRequestEvent refreshRequestEvent) {
        RefreshRequestDTO refreshRequestDTO = refreshRequestDtoFactory.from(key, refreshRequestEvent);
        // Start only if request is not handled yet
        if (refreshRequestDTO.getStatus() == RefreshRequestStatus.PENDING) {
            handleRefreshRequestImpl.handleRefreshRequest(refreshRequestDTO);
        }
    }

    private void saveRefreshRequestToDatabase(final RefreshRequestDTO refreshRequestDTO) {
        refreshRequestRepository.save(refreshRequestFactory.from(refreshRequestDTO));
    }

}
