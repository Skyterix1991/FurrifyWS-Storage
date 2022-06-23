package ws.furrify.sources.providers.deviantart;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserDeviationsQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

/**
 * Communication interface with DeviantArt API.
 *
 * @author Skyte
 */
public interface DeviantArtServiceClient {

    /**
     * Maximum deviations fetch limit
     */
    byte API_DEVIATIONS_FETCH_LIMIT = 24;

    /**
     * Get DeviantArt deviation.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param deviationId Deviation id.
     * @return Deviation query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /deviation/{deviationId}")
    DeviantArtDeviationQueryDTO getDeviation(@Param("bearerToken") String bearerToken, @Param("deviationId") String deviationId);

    /**
     * Get DeviantArt user.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param username    Username.
     * @return User query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /user/profile/{username}")
    DeviantArtUserQueryDTO getUser(@Param("bearerToken") String bearerToken, @Param("username") String username);

    /**
     * Get DeviantArt user.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param username    Username.
     * @param offset      Offset the next results in set (min: 0 max: 50000 default: 0).
     * @param limit       Limit of max pagination (min: 1 max: 24 default: 10).
     * @return List of deviations.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /api/v1/oauth2/collections/all?username={username}&offset={offset}&limit={limit}")
    DeviantArtUserDeviationsQueryDTO getUserDeviations(@Param("bearerToken") String bearerToken,
                                                       @Param("username") String username,
                                                       @Param("limit") byte limit,
                                                       @Param("offset") int offset);
}