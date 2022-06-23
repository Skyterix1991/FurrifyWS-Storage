package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class RefreshRequestFacade {

    private final CreateRefreshRequest createRefreshRequestImpl;
    private final RefreshRequestRepository refreshRequestRepository;
    private final RefreshRequestFactory refreshRequestFactory;
    private final RefreshRequestDtoFactory refreshRequestDtoFactory;

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

    private void saveRefreshRequestToDatabase(final RefreshRequestDTO refreshRequestDTO) {
        refreshRequestRepository.save(
                RefreshRequest.restore(
                        RefreshRequestSnapshot.builder()
                                .id(refreshRequestDTO.getId())
                                .refreshRequestId(refreshRequestDTO.getRefreshRequestId())
                                .artistId(refreshRequestDTO.getArtistId())
                                .ownerId(refreshRequestDTO.getOwnerId())
                                .status(refreshRequestDTO.getStatus())
                                .createDate(refreshRequestDTO.getCreateDate())
                                .build()
                )
        );
    }

}
