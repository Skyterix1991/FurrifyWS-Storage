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

        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var attachmentId = UUID.fromString("566548cf-fb1d-4552-a880-c741a1eb9d0e");

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
                                                .thumbnailUri(new URI("/artist/9551e7e0-4550-41b9-8c4a-57943642fa00/avatar/4d482df8-7380-4164-96ef-58f3796d8f27/thumbnail_image.jpg"))
                                                .build()
                                ))
                                .attachments(Set.of(
                                        PostAttachment.builder()
                                                .attachmentId(
                                                        attachmentId
                                                )
                                                .filename("test.psd")
                                                .extension("PSD")
                                                .fileUri(new URI("/attachment/566548cf-fb1d-4552-a880-c741a1eb9d0e/furrify-horizontal.png"))
                                                .build()
                                ))
                                .mediaSet(Set.of(
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("19c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("EXTENSION_PNG")
                                                .fileUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                .thumbnailUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                .build(),
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(2)
                                                .extension("EXTENSION_PNG")
                                                .fileUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
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
                                                .value("walking1")
                                                .type("ACTION")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking2")
                                                .type("AGE")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking3")
                                                .type("AMOUNT")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking4")
                                                .type("BACKGROUND")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking5")
                                                .type("BODY")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking6")
                                                .type("CHARACTER")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking7")
                                                .type("COMPANY")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking8")
                                                .type("MOVIE")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking9")
                                                .type("SEX")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking10")
                                                .type("SPECIE")
                                                .build(),
                                        PostTag.builder()
                                                .value("walking11")
                                                .type("UNIVERSE")
                                                .build()
                                ))
                                .artists(Set.of(
                                        PostArtist.builder()
                                                .artistId(
                                                        UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00")
                                                )
                                                .preferredNickname("test_nickname")
                                                .thumbnailUri(new URI("/artist/9551e7e0-4550-41b9-8c4a-57943642fa00/avatar/4d482df8-7380-4164-96ef-58f3796d8f27/thumbnail_image.jpg"))
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
                                                .extension("EXTENSION_PNG")
                                                .fileUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                .thumbnailUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                .build(),
                                        PostMedia.builder()
                                                .mediaId(
                                                        UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                )
                                                .priority(1)
                                                .extension("EXTENSION_PNG")
                                                .fileUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
                                                .thumbnailUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/thumbnail_123.png"))
                                                .build()
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        for (int i = 0; i < 100; i++) {
            sqlPostRepository.save(
                    postFactory.from(
                            PostDTO.builder()
                                    .postId(UUID.randomUUID())
                                    .ownerId(userId)
                                    .title("title2")
                                    .description("desc")
                                    .tags(Set.of(
                                            PostTag.builder()
                                                    .value("walking1")
                                                    .type("ACTION")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking2")
                                                    .type("AGE")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking3")
                                                    .type("AMOUNT")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking4")
                                                    .type("BACKGROUND")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking5")
                                                    .type("BODY")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking6")
                                                    .type("CHARACTER")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking7")
                                                    .type("COMPANY")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking8")
                                                    .type("MOVIE")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking9")
                                                    .type("SEX")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking10")
                                                    .type("SPECIE")
                                                    .build(),
                                            PostTag.builder()
                                                    .value("walking11")
                                                    .type("UNIVERSE")
                                                    .build()
                                    ))
                                    .artists(Set.of(
                                            PostArtist.builder()
                                                    .artistId(
                                                            UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00")
                                                    )
                                                    .preferredNickname("test_nickname")
                                                    .thumbnailUri(new URI("/artist/9551e7e0-4550-41b9-8c4a-57943642fa00/avatar/4d482df8-7380-4164-96ef-58f3796d8f27/thumbnail_image.jpg"))
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
                                                    .extension("EXTENSION_PNG")
                                                    .fileUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/2620245.png"))
                                                    .thumbnailUri(new URI("/media/19c02f53-486e-4205-b1b7-74977ae13941/thumbnail_2620245.png"))
                                                    .build(),
                                            PostMedia.builder()
                                                    .mediaId(
                                                            UUID.fromString("29c02f53-486e-4205-b1b7-74977ae13941")
                                                    )
                                                    .priority(1)
                                                    .extension("EXTENSION_PNG")
                                                    .fileUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/123.png"))
                                                    .thumbnailUri(new URI("/media/29c02f53-486e-4205-b1b7-74977ae13941/thumbnail_123.png"))
                                                    .build()
                                    ))
                                    .createDate(ZonedDateTime.now())
                                    .build()
                    ).getSnapshot()
            );
        }

        System.out.println("UserId: " + userId);
        System.out.println("PostId: " + postId);
    }

}
