package ws.furrify.artists.avatar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.artists.avatar.dto.AvatarDTO;
import ws.furrify.artists.avatar.strategy.AvatarUploadStrategy;
import ws.furrify.artists.avatar.vo.AvatarFile;
import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
final class UpdateAvatarImpl implements UpdateAvatar {

    private final DomainEventPublisher<AvatarEvent> domainEventPublisher;
    private final AvatarRepository avatarRepository;
    private final AvatarUploadStrategy avatarUploadStrategy;

    @Override
    public void updateAvatar(@NonNull final UUID userId,
                             @NonNull final UUID artistId,
                             @NonNull final UUID avatarId,
                             @NonNull final AvatarDTO avatarDTO,
                             final MultipartFile avatarFile) {
        Avatar avatar = avatarRepository.findByOwnerIdAndArtistIdAndAvatarId(userId, artistId, avatarId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(avatarId.toString())));

        // Update changed fields in attachment

        // If new attachment file provided with extension
        if (avatarFile != null && !avatarFile.isEmpty()) {

            // Generate hash from file
            final String md5 = AvatarFileUtils.generateMd5FromFile(avatarFile);

            // Validate avatar file
            AvatarFileUtils.validateAvatar(
                    avatarDTO.getExtension(),
                    avatarFile
            );

            // Upload attachment file
            AvatarUploadStrategy.UploadedAvatarFile uploadedAvatarFile = avatarUploadStrategy.uploadAvatarWithGeneratedThumbnail(
                    avatarId,
                    avatarFile
            );

            avatar.replaceAvatarFile(
                    AvatarFile.builder()
                            .filename(Objects.requireNonNull(avatarFile.getOriginalFilename()))
                            .fileUri(uploadedAvatarFile.getFileUri())
                            .thumbnailUri(uploadedAvatarFile.getThumbnailUri())
                            .extension(avatarDTO.getExtension())
                            .md5(md5)
                            .build()
            );
        }

        // Publish update attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.AVATAR,
                // User userId as key
                userId,
                AvatarUtils.createAvatarEvent(
                        DomainEventPublisher.AvatarEventType.UPDATED,
                        avatar
                )
        );
    }


}
