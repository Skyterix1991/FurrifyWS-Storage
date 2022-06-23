package ws.furrify.sources.refreshrequest.vo;

/**
 * Status indicating what is happening in backend with refresh request.
 *
 * @author sky
 */
public enum RefreshRequestStatus {
    /**
     * Request has been accepted and is waiting for its turn.
     */
    PENDING,
    /**
     * Request is currently being fullfilled.
     */
    IN_PROGRESS,
    /**
     * Request has failed to retrieve content.
     */
    FAILED,
    /**
     * Request succeeded and Source update event should be sent.
     */
    COMPLETED
}
