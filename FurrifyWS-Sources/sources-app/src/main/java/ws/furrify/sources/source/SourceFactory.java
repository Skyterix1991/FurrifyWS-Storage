package ws.furrify.sources.source;

import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.time.ZonedDateTime;
import java.util.UUID;

final class SourceFactory {

    Source from(SourceDTO sourceDTO) {
        // Validation result
        SourceStrategy strategy = sourceDTO.getStrategy();

        var validationResult = switch (sourceDTO.getOriginType()) {
            case MEDIA -> strategy.validateMedia(sourceDTO.getData());
            case ARTIST -> strategy.validateArtist(sourceDTO.getData());
            case ATTACHMENT -> strategy.validateAttachment(sourceDTO.getData());
        };

        // Was validation successful
        if (!validationResult.isValid()) {
            throw new InvalidDataGivenException(Errors.VALIDATION_FAILED.getErrorMessage(
                    strategy.getClass().getSimpleName(),
                    validationResult.getReason()
            ));
        }

        SourceSnapshot sourceSnapshot = SourceSnapshot.builder()
                .id(sourceDTO.getId())
                .postId(sourceDTO.getPostId())
                .originId(sourceDTO.getOriginId())
                .sourceId(
                        sourceDTO.getSourceId() != null ? sourceDTO.getSourceId() : UUID.randomUUID()
                )
                .ownerId(sourceDTO.getOwnerId())
                .strategy(sourceDTO.getStrategy())
                // Set updated data from validation result
                .data(validationResult.getData())
                .originType(sourceDTO.getOriginType())
                .createDate(
                        sourceDTO.getCreateDate() != null ? sourceDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Source.restore(sourceSnapshot);
    }

}
