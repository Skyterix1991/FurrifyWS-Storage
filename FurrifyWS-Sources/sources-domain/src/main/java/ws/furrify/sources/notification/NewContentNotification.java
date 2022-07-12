package ws.furrify.sources.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.sources.vo.RemoteContent;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class NewContentNotification {

    @NonNull
    private final Long id;

    @NonNull
    private final UUID notificationId;
    @NonNull
    private final UUID sourceId;
    @NonNull
    private final UUID ownerId;

    @NonNull
    private List<RemoteContent> newRemoteContentList;

    private boolean viewed;

    private final ZonedDateTime createDate;

    static NewContentNotification restore(NewContentNotificationSnapshot refreshRequestSnapshot) {
        return new NewContentNotification(
                refreshRequestSnapshot.getId(),
                refreshRequestSnapshot.getNotificationId(),
                refreshRequestSnapshot.getSourceId(),
                refreshRequestSnapshot.getOwnerId(),
                new ArrayList<>(refreshRequestSnapshot.getNewRemoteContentList()),
                refreshRequestSnapshot.isViewed(),
                refreshRequestSnapshot.getCreateDate()
        );
    }

    NewContentNotificationSnapshot getSnapshot() {
        return NewContentNotificationSnapshot.builder()
                .id(id)
                .notificationId(notificationId)
                .sourceId(sourceId)
                .ownerId(ownerId)
                .newRemoteContentList(new ArrayList<>(newRemoteContentList))
                .viewed(viewed)
                .createDate(createDate)
                .build();
    }

    /**
     * Mark notification as viewed. Or throw exception.
     */
    void markAsViewed() {
        if (viewed) {
            throw new IllegalStateException("Notification was already marked as viewed.");
        }

        this.viewed = true;
    }
}
