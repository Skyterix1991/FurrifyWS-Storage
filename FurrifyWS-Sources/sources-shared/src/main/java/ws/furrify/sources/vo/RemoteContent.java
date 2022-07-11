package ws.furrify.sources.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.net.URI;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


/**
 * Remote content entity.
 */
@Data
@Setter(value = PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = PROTECTED)
public class RemoteContent {

    /**
     * Needs to be excluded for content compare to work in refresh request handling
     */
    @EqualsAndHashCode.Exclude
    private Long id;

    @NonNull
    private String contentIdentifier;

    @NonNull
    @EqualsAndHashCode.Exclude
    private URI uri;

}