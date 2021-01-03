package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.TagEvent;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.tag.dto.TagDTO;
import ws.furrify.tags.TagData;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class ReplaceTagAdapter implements ReplaceTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void replaceTag(final UUID userId, final String value, final TagDTO tagDTO) {
        Tag tag = tagRepository.findByOwnerIdAndValue(userId, value)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value)));

        // Update fields in tag
        tag.updateValue(tagDTO.getValue(), tagRepository);
        tag.updateType(tagDTO.getType());

        // Publish replace tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                createTagEvent(value, tag)
        );
    }

    private TagEvent createTagEvent(final String oldTagValue, final Tag tag) {
        TagSnapshot tagSnapshot = tag.getSnapshot();

        return TagEvent.newBuilder()
                .setState(DomainEventPublisher.TagEventType.REPLACED.name())
                .setTagId(tagSnapshot.getId())
                .setTagValue(oldTagValue)
                .setDataBuilder(
                        TagData.newBuilder()
                                .setValue(tagSnapshot.getValue())
                                .setOwnerId(tagSnapshot.getOwnerId().toString())
                                .setType(tagSnapshot.getType().name())
                                .setCreateDate(tagSnapshot.getCreateDate().toInstant().toEpochMilli())
                )
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
