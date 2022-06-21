package ws.furrify.artists.artist;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.artists.artist.vo.ArtistAvatar;
import ws.furrify.artists.artist.vo.ArtistNickname;
import ws.furrify.artists.artist.vo.ArtistSource;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Artist {
    private final Long id;
    @NonNull
    private final UUID artistId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private Set<ArtistNickname> nicknames;
    @NonNull
    private ArtistNickname preferredNickname;
    @NonNull
    private Set<ArtistSource> sources;

    private ArtistAvatar avatar;

    private final ZonedDateTime createDate;

    static Artist restore(ArtistSnapshot artistSnapshot) {
        return new Artist(
                artistSnapshot.getId(),
                artistSnapshot.getArtistId(),
                artistSnapshot.getOwnerId(),
                new HashSet<>(
                        artistSnapshot.getNicknames().stream()
                                .map(ArtistNickname::of)
                                .collect(Collectors.toSet())
                ),
                ArtistNickname.of(
                        artistSnapshot.getPreferredNickname()
                ),
                new HashSet<>(artistSnapshot.getSources()),
                artistSnapshot.getAvatar(),
                artistSnapshot.getCreateDate()
        );
    }

    ArtistSnapshot getSnapshot() {
        return ArtistSnapshot.builder()
                .id(id)
                .artistId(artistId)
                .ownerId(ownerId)
                .nicknames(nicknames.stream()
                        .map(ArtistNickname::getNickname)
                        .collect(Collectors.toUnmodifiableSet())
                )
                .preferredNickname(preferredNickname.getNickname())
                .sources(sources.stream().collect(Collectors.toUnmodifiableSet()))
                .avatar(avatar)
                .createDate(createDate)
                .build();
    }

    void updateNicknames(Set<ArtistNickname> newNicknames,
                         ArtistNickname newPreferredNickname,
                         @NonNull final ArtistRepository artistRepository) {
        // Check if both parameters are null
        if (newPreferredNickname == null && newNicknames == null) {
            throw new NullPointerException("Both parameters cannot be null.");
        }

        // Check if any parameters are null
        if (newNicknames == null) {
            newNicknames = new HashSet<>(this.nicknames);
        } else if (newPreferredNickname == null) {
            newPreferredNickname = this.preferredNickname;
        }

        // Copy fields to final fields
        final ArtistNickname preferredNickname = newPreferredNickname;
        final Set<ArtistNickname> nicknames = newNicknames;

        // Check if nicknames array is empty
        if (nicknames.size() == 0) {
            throw new InvalidDataGivenException(Errors.NICKNAMES_CANNOT_BE_EMPTY.getErrorMessage());
        }

        // Verify if preferred nickname is in nicknames array
        boolean isPreferredNicknameValid = nicknames.stream()
                .anyMatch(nick -> nick.equals(preferredNickname));

        if (!isPreferredNicknameValid) {
            throw new
                    InvalidDataGivenException(Errors.PREFERRED_NICKNAME_IS_NOT_VALID.getErrorMessage(preferredNickname.getNickname()));
        }

        // Verify is preferred nickname is already taken
        boolean isPreferredNicknameTaken =
                // Is nickname selected as preferred in other artists
                artistRepository.existsByOwnerIdAndPreferredNickname(ownerId, preferredNickname.getNickname());

        if (isPreferredNicknameTaken) {
            throw new
                    RecordAlreadyExistsException(Errors.PREFERRED_NICKNAME_IS_TAKEN.getErrorMessage(preferredNickname.getNickname()));
        }

        this.nicknames = nicknames;
        this.preferredNickname = preferredNickname;
    }

    void addSource(@NonNull final ArtistSource artistSource) {
        this.sources.add(artistSource);
    }

    void deleteSource(final UUID sourceId) {
        this.sources = sources.stream()
                .filter(source -> !source.getSourceId().equals(sourceId))
                .collect(Collectors.toSet());
    }

    void updateSourceDataInSources(@NonNull final ArtistSource artistSource) {
        // Filter sourceSet to find if source exists by sourceId.
        this.sources.stream()
                .filter(source -> source.getSourceId().equals(artistSource.getSourceId()))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original source [sourceId=" + artistSource.getSourceId() + "] was not found.");

                    return new IllegalStateException("Original sourceId was not found.");
                });

        // Filter sourceSet to get all without old source
        Set<ArtistSource> filteredSourceSet = this.sources.stream()
                .filter(source -> !source.getSourceId().equals(artistSource.getSourceId()))
                .collect(Collectors.toSet());
        // Add updated source to sourceSet
        filteredSourceSet.add(artistSource);

        this.sources = filteredSourceSet;
    }

    void addAvatar(@NonNull final ArtistAvatar artistAvatar) {
        this.avatar = artistAvatar;
    }

    void deleteAvatar() {
        this.avatar = null;
    }
}
