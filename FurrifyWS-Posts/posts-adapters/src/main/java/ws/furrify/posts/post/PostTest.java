package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class PostTest implements CommandLineRunner {

    private final SqlPostRepository sqlPostRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingPosts());
    }

    @SneakyThrows
    private void createTestingPosts() {
        var postFactory = new PostFactory();

        var userId = UUID.fromString("e440a70e-d9af-42c4-a4f1-38677c0c950d");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");

        sqlPostRepository.save(
                postFactory.from(
                        PostDTO.builder()
                                .postId(postId)
                                .ownerId(userId)
                                .title("title1")
                                .description("desc")
                                .tags(Set.of(
                                        PostTag.builder()
                                                .value("walking1")
                                                .type("ACTION")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking2")
                                                .type("ACTION")
                                                .build()
                                ))
                                .artists(Set.of(
                                        PostArtist.builder()
                                                .artistId(
                                                        UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00")
                                                )
                                                .preferredNickname("test_nickname")
                                                .build()
                                ))
                                .attachments(Set.of(
                                        PostAttachment.builder()
                                                .attachmentId(
                                                        UUID.fromString("14925445-f5dc-43b9-a1a0-230cb0f10e51")
                                                )
                                                .filename("test.psd")
                                                .extension("PSD")
                                                .fileUri(new URI("/attachment/14925445-f5dc-43b9-a1a0-230cb0f10e51/furrify-horizontal.png"))
                                                .build()
                                ))
                                .mediaSet(Set.of(
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("PNG")
                                                .fileUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                .thumbnailUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                .build(),
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(2)
                                                .extension("PNG")
                                                .fileUri(new URI("/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
                                                .thumbnailUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/thumbnail_123.png"))
                                                .build()
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        sqlPostRepository.save(
                postFactory.from(
                        PostDTO.builder()
                                .postId(UUID.randomUUID())
                                .ownerId(userId)
                                .title("title2")
                                .description("desc")
                                .tags(Set.of(
                                        PostTag.builder()
                                                .value("walking2")
                                                .type("ACTION")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking3")
                                                .type("ACTION")
                                                .build()
                                ))
                                .artists(Set.of(
                                        PostArtist.builder()
                                                .artistId(
                                                        UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00")
                                                )
                                                .preferredNickname("test_nickname")
                                                .build()
                                ))
                                .attachments(Set.of(
                                        PostAttachment.builder()
                                                .attachmentId(
                                                        UUID.fromString("14925445-f5dc-43b9-a1a0-230cb0f10e51")
                                                )
                                                .filename("test.psd")
                                                .extension("PSD")
                                                .fileUri(new URI("/attachment/14925445-f5dc-43b9-a1a0-230cb0f10e51/furrify-horizontal.png"))
                                                .build()
                                ))
                                .mediaSet(Set.of(
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(2)
                                                .extension("PNG")
                                                .fileUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                .thumbnailUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                .build(),
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("PNG")
                                                .fileUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
                                                .thumbnailUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/thumbnail_123.png"))
                                                .build()
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("UserId: " + userId);
        System.out.println("PostId: " + postId);
    }

}
