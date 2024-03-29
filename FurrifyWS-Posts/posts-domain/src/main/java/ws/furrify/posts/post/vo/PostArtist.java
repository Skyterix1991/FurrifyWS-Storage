package ws.furrify.posts.post.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.net.URI;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED)
public class PostArtist {
    @NonNull
    private UUID artistId;
    private String preferredNickname;
    private URI thumbnailUri;
}