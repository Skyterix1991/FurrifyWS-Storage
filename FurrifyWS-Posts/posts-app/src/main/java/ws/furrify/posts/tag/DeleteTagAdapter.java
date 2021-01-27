package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.TagEvent;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.tags.TagData;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
class DeleteTagAdapter implements DeleteTagPort {

    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public void deleteTag(final UUID userId, final String value) {
        if (!tagRepository.existsByOwnerIdAndValue(userId, value)) {
            throw new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(value));
        }

        // Publish delete tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // Use userId as key
                userId,
                createTagEvent(value)
        );
    }

    private TagEvent createTagEvent(final String value) {
        return TagEvent.newBuilder()
                .setState(DomainEventPublisher.TagEventType.REMOVED.name())
                .setTagValue(value)
                .setDataBuilder(TagData.newBuilder())
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}