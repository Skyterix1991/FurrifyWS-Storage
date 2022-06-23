package ws.furrify.sources.refreshrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PACKAGE)
class RefreshRequestSnapshot {
    private Long id;

    private UUID refreshRequestId;
    private UUID artistId;
    private UUID ownerId;

    private RefreshRequestStatus status;

    private ZonedDateTime createDate;
}