package ws.furrify.tags.pageable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Pageable constants.
 *
 * @author Skyte
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PageableConstants {

    /**
     * Default page size of accounts.ws.furrify.shared.pageable.
     */
    public final static int DEFAULT_PAGE_SIZE = 30;

    /**
     * Default if non provided.
     */
    public final static int DEFAULT_PAGE = 1;

    /**
     * Default sort option if non provided.
     */
    public final static String DEFAULT_SORT = "createDate";

    /**
     * Max page size.
     */
    public final static int MAX_PAGE_SIZE = 100;
}
