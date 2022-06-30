package ws.furrify.sources.refreshrequest;

import lombok.NonNull;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;

interface HandleRefreshRequest {

    void handleRefreshRequest(@NonNull final RefreshRequestDTO refreshRequestDTO);

}
