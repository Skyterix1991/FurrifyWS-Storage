package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.vo.ArtistAvatar;
import ws.furrify.artists.artist.vo.ArtistSource;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ArtistTest implements CommandLineRunner {

    private final SqlArtistRepository sqlArtistRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingArtists());
    }

    @SneakyThrows
    private void createTestingArtists() {
        var artistFactory = new ArtistFactory();

        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
        var artistId = UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00");
        var avatarId = UUID.fromString("4d482df8-7380-4164-96ef-58f3796d8f27");
        var sourceId = UUID.fromString("02038a77-9717-4de8-a21b-3a722f158be2");

        sqlArtistRepository.save(
                artistFactory.from(
                        ArtistDTO.builder()
                                .artistId(artistId)
                                .ownerId(userId)
                                .nicknames(Collections.singleton("test_nickname"))
                                .preferredNickname("test_nickname")
                                .sources(Collections.singleton(
                                        new ArtistSource(
                                                sourceId,
                                                "DeviantArtV1SourceStrategy",
                                                new HashMap<>(2) {{
                                                    put("id", "123");
                                                }}
                                        )
                                ))
                                .avatar(
                                        ArtistAvatar.builder()
                                                .avatarId(avatarId)
                                                .extension("EXTENSION_PNG")
                                                .fileUri(new URI("/artist/9551e7e0-4550-41b9-8c4a-57943642fa00/avatar/4d482df8-7380-4164-96ef-58f3796d8f27/image.jpg"))
                                                .thumbnailUri(new URI("/artist/9551e7e0-4550-41b9-8c4a-57943642fa00/avatar/4d482df8-7380-4164-96ef-58f3796d8f27/thumbnail_image.jpg"))
                                                .build()
                                )
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("ArtistId: " + artistId);
    }

}
