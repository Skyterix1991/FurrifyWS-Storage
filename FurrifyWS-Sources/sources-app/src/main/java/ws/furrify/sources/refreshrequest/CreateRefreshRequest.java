package ws.furrify.sources.refreshrequest;

import java.util.UUID;

interface CreateRefreshRequest {
    UUID createRefreshRequest(final UUID ownerId, final UUID artistId);
}
