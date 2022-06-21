package ws.furrify.posts.post.dto.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * VO for query string search.
 * It is a parser for query string transforming it into pojo fields.
 * Note that artist with and without uses preferred nickname.
 *
 * @author Skyte
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class PostQuerySearchDTO implements Serializable {

    private final static Pattern WORDS_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*");

    private final static String WITH_ARTIST_SYMBOL = "@";
    private final static String WITHOUT_ARTIST_SYMBOL = "-@";

    private final static String WITH_TAG_SYMBOL = "#";
    private final static String WITHOUT_TAG_SYMBOL = "-#";

    Set<String> words;

    Set<String> withArtists;
    Set<String> withoutArtists;

    Set<String> withTags;
    Set<String> withoutTags;

    public static PostQuerySearchDTO from(final String query) {
        String[] queryParams = query.toLowerCase().split("\\s+");

        return new PostQuerySearchDTO(
                // Title words
                extractValuesFromQueryParamsByPattern(queryParams, WORDS_PATTERN),
                // With artists
                extractValuesFromQueryParamsBySymbols(queryParams, WITH_ARTIST_SYMBOL),
                // Without artists
                extractValuesFromQueryParamsBySymbols(queryParams, WITHOUT_ARTIST_SYMBOL),
                // With tags
                extractValuesFromQueryParamsBySymbols(queryParams, WITH_TAG_SYMBOL),
                // Without tags
                extractValuesFromQueryParamsBySymbols(queryParams, WITHOUT_TAG_SYMBOL)
        );
    }

    private static Set<String> extractValuesFromQueryParamsBySymbols(final String[] queryParams, final String symbol) {
        return Arrays.stream(queryParams)
                /* Filter all params that contain symbols from queryParams at position 0
                   so params symbols cannot overlap */
                .filter(param -> param.indexOf(symbol) == 0)
                // Replace symbol
                .map(param -> param.replace(symbol, ""))
                .collect(Collectors.toSet());
    }

    private static Set<String> extractValuesFromQueryParamsByPattern(final String[] queryParams, final Pattern pattern) {
        return Arrays.stream(queryParams)
                /* Filter all params that meet regex pattern */
                .filter(param -> pattern.matcher(param).matches())
                .collect(Collectors.toSet());
    }
}
