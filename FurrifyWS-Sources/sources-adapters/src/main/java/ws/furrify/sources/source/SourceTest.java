package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class SourceTest implements CommandLineRunner {

    private final SqlSourceRepository sqlSourceRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingSources());
    }

    private void createTestingSources() {
        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");

        var artistSourceId = UUID.fromString("02038a77-9717-4de8-a21b-3a722f158be2");
        var artistOriginId = UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00");

        var mediaSourceId = UUID.fromString("482b628f-4ca9-4c96-a199-bf25e21b5bca");
        var mediaOriginId = UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941");

        var attachmentSourceId = UUID.fromString("87a5d0b2-bba8-4e94-b7d3-c9ad51431dd5");
        var attachmentOriginId = UUID.fromString("566548cf-fb1d-4552-a880-c741a1eb9d0e");

        sqlSourceRepository.save(
                SourceSnapshot.builder()
                        .originId(artistOriginId)
                        .postId(null)
                        .sourceId(artistSourceId)
                        .ownerId(userId)
                        .strategy(new DeviantArtV1SourceStrategy())
                        .data(new HashMap<>(2) {{
                            put("url", "https://www.deviantart.com/freak-side");
                            put("user_id", "EC0AAF26-D129-8165-7E5A-1B3E0B2BBF82");
                            put("username", "freak-side");
                        }})
                        .originType(SourceOriginType.ARTIST)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        sqlSourceRepository.save(
                SourceSnapshot.builder()
                        .originId(mediaOriginId)
                        .postId(postId)
                        .sourceId(mediaSourceId)
                        .ownerId(userId)
                        .strategy(new DeviantArtV1SourceStrategy())
                        .data(new HashMap<>(2) {{
                            put("url", "https://www.deviantart.com/freak-side/art/C-h-i-l-l-i-n-911198824");
                            put("deviation_id", "EC0AAF26-D129-8165-7E5A-1B3E0B2BBF82");
                        }})
                        .originType(SourceOriginType.MEDIA)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        sqlSourceRepository.save(
                SourceSnapshot.builder()
                        .originId(attachmentOriginId)
                        .postId(postId)
                        .sourceId(attachmentSourceId)
                        .ownerId(userId)
                        .strategy(new DeviantArtV1SourceStrategy())
                        .data(new HashMap<>(2) {{
                            put("url", "https://www.deviantart.com/freak-side/art/C-h-i-l-l-i-n-911198824");
                            put("deviation_id", "EC0AAF26-D129-8165-7E5A-1B3E0B2BBF82");
                        }})
                        .originType(SourceOriginType.ATTACHMENT)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        System.out.println("ArtistSourceId: " + artistSourceId);
        System.out.println("MediaSourceId: " + mediaSourceId);
        System.out.println("AttachmentSourceId: " + attachmentSourceId);
    }

}
