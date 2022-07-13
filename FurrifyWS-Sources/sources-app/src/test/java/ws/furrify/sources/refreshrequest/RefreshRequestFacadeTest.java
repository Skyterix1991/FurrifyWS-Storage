package ws.furrify.sources.refreshrequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.shared.exception.NoArtistSourcesFoundForRefreshException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.artists.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.notification.NewContentNotificationEvent;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDtoFactory;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;
import ws.furrify.sources.source.SourceQueryRepository;
import ws.furrify.sources.source.SourceRemoteContentEvent;
import ws.furrify.sources.source.SourceRemoteContentQueryRepository;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.vo.RemoteContent;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RefreshRequestFacadeTest {

    private static RefreshRequestRepository refreshRequestRepository;
    private static SourceRemoteContentQueryRepository sourceRemoteContentQueryRepository;
    private static RefreshRequestFacade refreshRequestFacade;
    private static ArtistServiceClient artistServiceClient;
    private static CacheManager cacheManager;
    private static ServletRequestAttributes servletRequestAttributes;
    private static SourceQueryRepository sourceQueryRepository;
    private static TestSourceStrategyImpl testSourceStrategy;
    private static DomainEventPublisher<RefreshRequestEvent> refreshRequestEventDomainEventPublisher;
    private static DomainEventPublisher<SourceRemoteContentEvent> sourceRemoteContentEventDomainEventPublisher;
    private static DomainEventPublisher<NewContentNotificationEvent> newContentNotificationDomainPublisher;

    private RefreshRequestDTO refreshRequestDTO;
    private RefreshRequest refreshRequest;
    private RefreshRequestSnapshot refreshRequestSnapshot;
    private List<SourceDetailsQueryDTO> artistSources;

    @BeforeAll
    static void beforeAll() {
        artistServiceClient = mock(ArtistServiceClient.class);
        refreshRequestRepository = mock(RefreshRequestRepository.class);
        sourceQueryRepository = mock(SourceQueryRepository.class);
        sourceRemoteContentQueryRepository = mock(SourceRemoteContentQueryRepository.class);
        cacheManager = mock(CacheManager.class);

        var refreshRequestQueryRepository = mock(RefreshRequestQueryRepository.class);

        var refreshRequestFactory = new RefreshRequestFactory();
        var refreshRequestDTOFactory = new RefreshRequestDtoFactory(refreshRequestQueryRepository);
        sourceRemoteContentEventDomainEventPublisher = (DomainEventPublisher<SourceRemoteContentEvent>) mock(DomainEventPublisher.class);
        newContentNotificationDomainPublisher = (DomainEventPublisher<NewContentNotificationEvent>) mock(DomainEventPublisher.class);
        refreshRequestEventDomainEventPublisher = (DomainEventPublisher<RefreshRequestEvent>) mock(DomainEventPublisher.class);

        refreshRequestFacade = new RefreshRequestFacade(
                new CreateRefreshRequestImpl(
                        refreshRequestFactory,
                        refreshRequestEventDomainEventPublisher,
                        artistServiceClient,
                        sourceQueryRepository
                ),
                new HandleRefreshRequestImpl(
                        refreshRequestFactory,
                        sourceRemoteContentEventDomainEventPublisher,
                        refreshRequestEventDomainEventPublisher,
                        newContentNotificationDomainPublisher,
                        sourceQueryRepository,
                        sourceRemoteContentQueryRepository,
                        cacheManager
                ),
                refreshRequestRepository,
                refreshRequestDTOFactory,
                refreshRequestFactory);

        servletRequestAttributes = new ServletRequestAttributes(new HttpServletRequest() {
            @Override
            public Object getAttribute(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(final String s) throws UnsupportedEncodingException {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public String getParameter(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(final String s) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(final String s, final Object o) {

            }

            @Override
            public void removeAttribute(final String s) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final String s) {
                return null;
            }

            @Override
            public String getRealPath(final String s) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(final String s) {
                return 0;
            }

            @Override
            public String getHeader(final String s) {
                return "dsa";
            }

            @Override
            public Enumeration<String> getHeaders(final String s) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(final String s) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(final String s) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(final boolean b) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;
            }

            @Override
            public boolean authenticate(final HttpServletResponse httpServletResponse) throws IOException, ServletException {
                return false;
            }

            @Override
            public void login(final String s, final String s1) throws ServletException {

            }

            @Override
            public void logout() throws ServletException {

            }

            @Override
            public Collection<Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public Part getPart(final String s) throws IOException, ServletException {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(final Class<T> aClass) throws IOException, ServletException {
                return null;
            }
        });
    }

    @BeforeEach
    void setUp() {
        PropertyHolder.AUTH_SERVER = "test";

        refreshRequestDTO = RefreshRequestDTO.builder().ownerId(UUID.randomUUID()).refreshRequestId(UUID.randomUUID()).artistId(UUID.randomUUID()).status(RefreshRequestStatus.PENDING).bearerToken("Bearer test").createDate(ZonedDateTime.now()).build();

        refreshRequest = new RefreshRequestFactory().from(refreshRequestDTO);
        refreshRequestSnapshot = refreshRequest.getSnapshot();

        /* FIXME Fix for wierd .thenThrow() behaviour, for some reason it carries over to another tests???
         Anyway this just cancels all the current when()'s so it's fine again. If you know how to fix it, you are welcome to.*/
        testSourceStrategy = mock(TestSourceStrategyImpl.class);


        artistSources = List.of(new SourceDetailsQueryDTO() {
            @Override
            public UUID getOriginId() {
                return UUID.randomUUID();
            }

            @Override
            public UUID getSourceId() {
                return UUID.randomUUID();
            }

            @Override
            public UUID getPostId() {
                return UUID.randomUUID();
            }

            @Override
            public UUID getOwnerId() {
                return UUID.randomUUID();
            }

            @Override
            public HashMap<String, String> getData() {
                return new HashMap<>();
            }

            @Override
            public SourceStrategy getStrategy() {
                return testSourceStrategy;
            }

            @Override
            public SourceOriginType getOriginType() {
                return SourceOriginType.ARTIST;
            }

            @Override
            public ZonedDateTime getCreateDate() {
                return ZonedDateTime.now();
            }
        });
    }

    @Test
    @DisplayName("Create refreshRequest for artist")
    void createArtistRefreshRequest() {
        // Given ownerId, artistId
        UUID userId = refreshRequestSnapshot.getOwnerId();
        UUID artistId = refreshRequestSnapshot.getArtistId();

        // When createRefreshRequest() method called
        ArtistDetailsQueryDTO artistDetailsQueryDTO = new ArtistDetailsQueryDTO(artistId);

        when(artistServiceClient.getUserArtist(userId, artistId)).thenReturn(artistDetailsQueryDTO);
        when(sourceQueryRepository.countByOwnerIdAndOriginId(userId, artistId)).thenReturn(3L);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);
            // Then create refreshRequest and return generated uuid
            assertNotNull(refreshRequestFacade.createRefreshRequest(userId, artistId), "RefreshRequestId was not returned.");
        }
    }

    @Test
    @DisplayName("Create refreshRequest for non existing artist")
    void createArtistRefreshRequest2() {
        // Given ownerId, non-existing artistId
        UUID userId = refreshRequestSnapshot.getOwnerId();
        UUID artistId = refreshRequestSnapshot.getArtistId();

        // When createRefreshRequest() method called
        when(artistServiceClient.getUserArtist(userId, artistId)).thenReturn(null);
        // Then throw exception
        assertThrows(RecordNotFoundException.class, () -> refreshRequestFacade.createRefreshRequest(userId, artistId), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Create refreshRequest for artist with no sources")
    void createArtistRefreshRequest3() {
        // Given ownerId, artistId to artist with no sources
        UUID userId = refreshRequestSnapshot.getOwnerId();
        UUID artistId = refreshRequestSnapshot.getArtistId();

        // When createRefreshRequest() method called
        ArtistDetailsQueryDTO artistDetailsQueryDTO = new ArtistDetailsQueryDTO(artistId);

        when(artistServiceClient.getUserArtist(userId, artistId)).thenReturn(artistDetailsQueryDTO);
        when(sourceQueryRepository.countByOwnerIdAndOriginId(userId, artistId)).thenReturn(0L);
        // Then throw exception
        assertThrows(NoArtistSourcesFoundForRefreshException.class, () -> refreshRequestFacade.createRefreshRequest(userId, artistId), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Handle refreshRequest for artist")
    void handleArtistRefreshRequest() throws URISyntaxException {
        // Given refreshRequestDTO
        // When handleRefreshRequest() method called
        when(sourceQueryRepository.findAllByOwnerIdAndArtistId(any(), any())).thenReturn(artistSources);
        when(testSourceStrategy.fetchArtistContent(any(), any(), any())).thenReturn(
                Collections.singleton(new RemoteContent(null, "dsaw", new URI("https://example.com")))
        );
        when(sourceRemoteContentQueryRepository.findAllByOwnerIdAndSourceId(any(), any())).thenReturn(
                List.of(new RemoteContent(null, "dsa1", new URI("https://example.com")))
        );

        // Capture sent refresh request status update event
        ArgumentCaptor<RefreshRequestEvent> refreshRequestEventCapture = ArgumentCaptor.forClass(RefreshRequestEvent.class);
        doNothing().when(refreshRequestEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), refreshRequestEventCapture.capture());
        // Capture sent source remote content update event
        ArgumentCaptor<SourceRemoteContentEvent> sourceRemoteContentEventCapture = ArgumentCaptor.forClass(SourceRemoteContentEvent.class);
        doNothing().when(sourceRemoteContentEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), sourceRemoteContentEventCapture.capture());
        // Capture sent new content notification create event.
        ArgumentCaptor<NewContentNotificationEvent> newContentNotificationEventCapture = ArgumentCaptor.forClass(NewContentNotificationEvent.class);
        doNothing().when(newContentNotificationDomainPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), newContentNotificationEventCapture.capture());

        // Then
        assertAll(() -> {
            assertDoesNotThrow(() -> refreshRequestFacade.handleRefreshRequest(refreshRequestDTO), "Exception was thrown.");
            assertSame(RefreshRequestStatus.COMPLETED, RefreshRequestStatus.valueOf(refreshRequestEventCapture.getValue().getData().getStatus()), "COMPLETED status should be sent with event.");
            assertSame(DomainEventPublisher.SourceRemoteContentEventType.REPLACED, DomainEventPublisher.SourceRemoteContentEventType.valueOf(sourceRemoteContentEventCapture.getValue().getState()), "REPLACED source remote content event should be sent.");
            assertSame(DomainEventPublisher.NewContentNotificationEventType.CREATED, DomainEventPublisher.NewContentNotificationEventType.valueOf(newContentNotificationEventCapture.getValue().getState()), "CREATED new content notification event should be sent.");
        });
    }

    @Test
    @DisplayName("Handle refreshRequest for artist with no sources")
    void handleArtistRefreshRequest2() {
        // Given refreshRequestDTO
        // When handleRefreshRequest() method called
        when(sourceQueryRepository.findAllByOwnerIdAndArtistId(any(), any())).thenReturn(new ArrayList<>());

        // Capture sent event
        ArgumentCaptor<RefreshRequestEvent> eventCapture = ArgumentCaptor.forClass(RefreshRequestEvent.class);
        doNothing().when(refreshRequestEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), eventCapture.capture());

        refreshRequestFacade.handleRefreshRequest(refreshRequestDTO);

        var status = RefreshRequestStatus.valueOf(eventCapture.getValue().getData().getStatus());

        assertSame(RefreshRequestStatus.FAILED, status, "FAILED status should be sent with event.");
    }

    @Test
    @DisplayName("Handle refreshRequest for sources with strategy that doesn't implement content fetching")
    void handleArtistRefreshRequest3() throws URISyntaxException {
        // Given refreshRequestDTO with sources that doesn't contain strategy with new content checking
        artistSources = List.of(new SourceDetailsQueryDTO() {
            @Override
            public UUID getOriginId() {
                return null;
            }

            @Override
            public UUID getSourceId() {
                return null;
            }

            @Override
            public UUID getPostId() {
                return null;
            }

            @Override
            public UUID getOwnerId() {
                return null;
            }

            @Override
            public HashMap<String, String> getData() {
                return null;
            }

            @Override
            public SourceStrategy getStrategy() {
                return new NoContentFetchingTestSourceStrategyImpl();
            }

            @Override
            public SourceOriginType getOriginType() {
                return null;
            }

            @Override
            public ZonedDateTime getCreateDate() {
                return null;
            }
        });
        // When handleRefreshRequest() method called
        when(sourceQueryRepository.findAllByOwnerIdAndArtistId(any(), any())).thenReturn(artistSources);
        when(testSourceStrategy.fetchArtistContent(any(), any(), any())).thenReturn(Collections.singleton(new RemoteContent(null, "dsaw", new URI("https://example.com"))));
        when(sourceRemoteContentQueryRepository.findAllByOwnerIdAndSourceId(any(), any())).thenReturn(List.of(new RemoteContent(null, "dsa1", new URI("https://example.com"))));
        // Capture sent event
        ArgumentCaptor<RefreshRequestEvent> eventCapture = ArgumentCaptor.forClass(RefreshRequestEvent.class);
        doNothing().when(refreshRequestEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), eventCapture.capture());

        // Then
        refreshRequestFacade.handleRefreshRequest(refreshRequestDTO);

        var status = RefreshRequestStatus.valueOf(eventCapture.getValue().getData().getStatus());

        assertSame(RefreshRequestStatus.COMPLETED, status, "COMPLETED status should be sent with event.");
    }

    @Test
    @DisplayName("Handle refreshRequest for artist with failed API contact with external provider")
    void handleArtistRefreshRequest4() {
        // Given refreshRequestDTO
        // When handleRefreshRequest() method called
        when(sourceQueryRepository.findAllByOwnerIdAndArtistId(any(), any())).thenReturn(artistSources);
        when(testSourceStrategy.fetchArtistContent(any(), any(), any())).thenThrow(RuntimeException.class);

        // Capture sent event
        ArgumentCaptor<RefreshRequestEvent> eventCapture = ArgumentCaptor.forClass(RefreshRequestEvent.class);
        doNothing().when(refreshRequestEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), eventCapture.capture());

        // Then
        assertAll(() -> {
            assertDoesNotThrow(() -> refreshRequestFacade.handleRefreshRequest(refreshRequestDTO), "Exception was thrown.");
            assertSame(RefreshRequestStatus.FAILED, RefreshRequestStatus.valueOf(eventCapture.getValue().getData().getStatus()), "FAILED status should be sent with event.");
        });
    }

    @Test
    @DisplayName("Handle refreshRequest for artist check if new content is the same as source and don't send update event")
    void handleArtistRefreshRequest5() throws URISyntaxException {
        // Given refreshRequestDTO
        // When handleRefreshRequest() method called

        // User the same remote content for current and fetched remote content
        RemoteContent remoteContent = new RemoteContent(null, "dsaw", new URI("https://example.com"));

        when(sourceQueryRepository.findAllByOwnerIdAndArtistId(any(), any())).thenReturn(artistSources);
        when(testSourceStrategy.fetchArtistContent(any(), any(), any())).thenReturn(Collections.singleton(remoteContent));
        when(sourceRemoteContentQueryRepository.findAllByOwnerIdAndSourceId(any(), any())).thenReturn(List.of(remoteContent));
        // Capture sent event
        ArgumentCaptor<SourceRemoteContentEvent> eventCapture = ArgumentCaptor.forClass(SourceRemoteContentEvent.class);
        doNothing().when(sourceRemoteContentEventDomainEventPublisher).publish(any(DomainEventPublisher.Topic.class), any(UUID.class), eventCapture.capture());

        // Then
        assertAll(() -> {
            assertDoesNotThrow(() -> refreshRequestFacade.handleRefreshRequest(refreshRequestDTO), "Exception was thrown.");
            assertDoesNotThrow(eventCapture::getValue, "Source remote content update was sent.");
        });
    }

}