package ws.furrify.sources.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.sources.vo.RemoteContent;

import java.time.ZonedDateTime;
import java.util.List;
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
class NewContentNotificationSnapshot {
    private Long id;

    private UUID notificationId;
    private UUID sourceId;
    private UUID ownerId;

    private List<RemoteContent> newRemoteContentList;

    private boolean viewed;

    private ZonedDateTime createDate;
}