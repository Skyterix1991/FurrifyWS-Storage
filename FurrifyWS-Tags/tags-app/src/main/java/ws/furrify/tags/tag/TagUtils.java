package ws.furrify.tags.tag;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.tags.tag.vo.TagData;

import java.time.Instant;
import java.util.UUID;

/**
 * Utils class regarding Tag entity.
 *
 * @author Skyte
 */
class TagUtils {

    /**
     * Create TagEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param tag       Tag aggregate to build tag event from.
     * @return Created tag event.
     */
    public static TagEvent createTagEvent(final DomainEventPublisher.TagEventType eventType,
                                          final Tag tag) {
        TagSnapshot tagSnapshot = tag.getSnapshot();

        return TagEvent.newBuilder()
                .setState(eventType.name())
                .setTagValue(tagSnapshot.getValue())
                .setDataBuilder(
                        TagData.newBuilder()
                                .setValue(tagSnapshot.getValue())
                                .setTitle(tagSnapshot.getTitle())
                                .setDescription(tagSnapshot.getDescription())
                                .setOwnerId(tagSnapshot.getOwnerId().toString())
                                .setType(tagSnapshot.getType().name())
                                .setCreateDate(
                                        (tagSnapshot.getCreateDate() != null) ?
                                                tagSnapshot.getCreateDate().toInstant() :
                                                null
                                )
                )
                .setOccurredOn(Instant.now())
                .build();
    }

    /**
     * Create TagEvent with REMOVE state.
     *
     * @param value Tag value the delete event will regard.
     * @return Created tag event.
     */
    public static TagEvent deleteTagEvent(final UUID ownerId,
                                          final String value) {
        return TagEvent.newBuilder()
                .setState(DomainEventPublisher.TagEventType.REMOVED.name())
                .setTagValue(value)
                .setDataBuilder(
                        TagData.newBuilder()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
