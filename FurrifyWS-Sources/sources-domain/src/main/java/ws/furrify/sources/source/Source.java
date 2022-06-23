package ws.furrify.sources.source;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.lang.Nullable;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Source {
    private final Long id;
    @NonNull
    private final UUID sourceId;

    @NonNull
    private final UUID originId;

    private final UUID postId;

    @NonNull
    private final UUID ownerId;

    @NonNull
    private Map<String, String> data;

    @NonNull
    private SourceStrategy strategy;

    @NonNull
    private final SourceOriginType originType;

    private final ZonedDateTime createDate;

    static Source restore(SourceSnapshot sourceSnapshot) {
        return new Source(
                sourceSnapshot.getId(),
                sourceSnapshot.getSourceId(),
                sourceSnapshot.getOriginId(),
                sourceSnapshot.getPostId(),
                sourceSnapshot.getOwnerId(),
                new HashMap<>(sourceSnapshot.getData()),
                sourceSnapshot.getStrategy(),
                sourceSnapshot.getOriginType(),
                sourceSnapshot.getCreateDate()
        );
    }

    SourceSnapshot getSnapshot() {
        return SourceSnapshot.builder()
                .id(id)
                .originId(originId)
                .postId(postId)
                .sourceId(sourceId)
                .ownerId(ownerId)
                .data(new HashMap<>(data))
                .strategy(strategy)
                .originType(originType)
                .createDate(createDate)
                .build();
    }

    /**
     * Update data and strategy.
     * If any of the values are null the current one will be used.
     * Nullable annotation used to suppress false warning when using method in update source adapter.
     *
     * @param data     Nullable data hashmap.
     * @param strategy Nullable strategy.
     */
    void updateData(@Nullable final HashMap<String, String> data,
                    @Nullable final SourceStrategy strategy) {
        // Replace with current values if null
        final HashMap<String, String> finalData = (data != null) ? data : new HashMap<>(this.data);
        final SourceStrategy finalStrategy = (strategy != null) ? strategy : this.strategy;

        var validationResult = switch (originType) {
            case MEDIA -> finalStrategy.validateMedia(finalData);
            case ARTIST -> finalStrategy.validateArtist(finalData);
            case ATTACHMENT -> finalStrategy.validateAttachment(finalData);
        };

        if (!validationResult.isValid()) {
            throw new InvalidDataGivenException(Errors.VALIDATION_FAILED.getErrorMessage(
                    finalStrategy.getClass().getSimpleName(),
                    validationResult.getReason()
            ));
        }

        this.strategy = finalStrategy;
        // Update data with validation result data
        this.data = validationResult.getData();
    }
}
