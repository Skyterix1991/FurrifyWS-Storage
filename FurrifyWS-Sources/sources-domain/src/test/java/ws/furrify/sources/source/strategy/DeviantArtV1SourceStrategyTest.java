package ws.furrify.sources.source.strategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ws.furrify.sources.keycloak.KeycloakServiceClient;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.keycloak.dto.KeycloakIdpTokenQueryDTO;
import ws.furrify.sources.providers.deviantart.DeviantArtScrapperClient;
import ws.furrify.sources.providers.deviantart.DeviantArtServiceClient;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

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
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy.DEVIATION_ID_FIELD;
import static ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy.DEVIATION_URL_FIELD;
import static ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy.USER_ID_FIELD;
import static ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy.USER_URL_FIELD;
import static ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy.USER_USERNAME_FIELD;

class DeviantArtV1SourceStrategyTest {

    private static KeycloakServiceClient keycloakServiceClient;
    private static DeviantArtServiceClient deviantArtServiceClient;
    private static DeviantArtScrapperClient deviantArtScrapperClient;
    private static ServletRequestAttributes servletRequestAttributes;
    private UUID deviationId;
    private UUID userId;
    private String deviationUrl;
    private String userUrl;
    private String username;
    private HashMap<String, String> data;
    private DeviantArtV1SourceStrategy deviantArtV1SourceStrategy;

    @BeforeAll
    static void beforeAll() {
        keycloakServiceClient = mock(KeycloakServiceClient.class);
        deviantArtServiceClient = mock(DeviantArtServiceClient.class);
        deviantArtScrapperClient = mock(DeviantArtScrapperClient.class);

        PropertyHolder.AUTH_SERVER = "test";
        PropertyHolder.REALM = "test";

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
        deviationId = UUID.randomUUID();
        userId = UUID.randomUUID();
        deviationUrl = "https://www.deviantart.com/freak-side/art/C-h-i-l-l-i-n-911198824";
        userUrl = "https://www.deviantart.com/freak-side";
        username = "Test";
        data = new HashMap<>();

        deviantArtV1SourceStrategy = new DeviantArtV1SourceStrategy(
                keycloakServiceClient,
                deviantArtServiceClient
        );
    }

    @Test
    @DisplayName("Validate media")
    void validateMedia() throws IOException {
        // Given
        data.put("url", deviationUrl);
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(deviationId.toString());

        when(deviantArtScrapperClient.scrapDeviationId(any())).thenReturn(deviationId.toString());
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);

            SourceStrategy.ValidationResult validationResult =
                    deviantArtV1SourceStrategy.validateMedia(data);

            // Then
            assertAll(() -> {
                assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
                // Check if id was added to data
                assertNotNull(validationResult.getData().get(DEVIATION_URL_FIELD), "Validation failed with correct parameters.");
                assertNotNull(validationResult.getData().get(DEVIATION_ID_FIELD), "Validation failed with correct parameters.");
                assertEquals(2, validationResult.getData().size(), "Validation result data contains too many parameters.");
            });
        }
    }

    @Test
    @DisplayName("Validate media with empty url property")
    void validateMedia2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate media with non existing url")
    void validateMedia3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate media with invalid domain in url property")
    void validateMedia4() {
        // Given
        data.put("url", "test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted invalid url property.");
    }

    @Test
    @DisplayName("Validate media with missing art in url property")
    void validateMedia5() {
        // Given
        data.put("url", "https://deviantart.com/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted missing art in url property.");
    }

    @Test
    @DisplayName("Validate media with invalid url given for uri object")
    void validateMedia6() {
        // Given
        data.put("url", "https://de`vianta`rt.com/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted invalid url given for uri object.");
    }

    @Test
    @DisplayName("Validate media with not expected domain in url property")
    void validateMedia7() {
        // Given
        data.put("url", "test.pl");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted not expected domain in url property.");
    }

    @Test
    @DisplayName("Validate media with missing art id in url property")
    void validateMedia8() {
        // Given
        data.put("url", "https://deviantart.com/test/art/");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted missing art id in url property.");
    }

    @Test
    @DisplayName("Validate media with non existing deviation")
    void validateMedia9() {
        // Given
        data.put("url", "https://deviantart.com/test/art/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateMedia(data).isValid(), "Validation accepted non existing url property.");
    }

    @Test
    @DisplayName("Validate media and check if no extra params passed to hashmap after validation")
    void validateMedia10() throws IOException {
        // Given
        data.put("url", deviationUrl);
        data.put("test", "I shouldn't be passed.");
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(deviationId.toString());

        when(deviantArtScrapperClient.scrapDeviationId(any())).thenReturn(deviationId.toString());
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);

            SourceStrategy.ValidationResult validationResult =
                    deviantArtV1SourceStrategy.validateMedia(data);

            // Then
            assertAll(() -> {
                assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
                // Check if id was added to data
                assertNull(validationResult.getData().get("test"), "Extra parameter was not removed before validation result.");
            });
        }
    }

    @Test
    @DisplayName("Validate attachment")
    void validateAttachment() throws IOException {
        // Given
        data.put("url", deviationUrl);
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(deviationId.toString());

        when(deviantArtScrapperClient.scrapDeviationId(any())).thenReturn(deviationId.toString());
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);

            SourceStrategy.ValidationResult validationResult =
                    deviantArtV1SourceStrategy.validateAttachment(data);

            // Then
            assertAll(() -> {
                assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
                // Check if id was added to data
                assertNotNull(validationResult.getData().get(DEVIATION_URL_FIELD), "Validation failed with correct parameters.");
                assertNotNull(validationResult.getData().get(DEVIATION_ID_FIELD), "Validation failed with correct parameters.");
                assertEquals(2, validationResult.getData().size(), "Validation result data contains too many parameters.");
            });
        }
    }

    @Test
    @DisplayName("Validate attachment with empty url property")
    void validateAttachment2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate attachment with non existing url")
    void validateAttachment3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate attachment with invalid domain in url property")
    void validateAttachment4() {
        // Given
        data.put("url", "test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted invalid url property.");
    }

    @Test
    @DisplayName("Validate attachment with missing art in url property")
    void validateAttachment5() {
        // Given
        data.put("url", "https://deviantart.com/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted missing art in url property.");
    }

    @Test
    @DisplayName("Validate attachment with invalid url given for uri object")
    void validateAttachment6() {
        // Given
        data.put("url", "https://de`vianta`rt.com/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted invalid url given for uri object.");
    }

    @Test
    @DisplayName("Validate attachment with not expected domain in url property")
    void validateAttachment7() {
        // Given
        data.put("url", "test.pl");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted not expected domain in url property.");
    }

    @Test
    @DisplayName("Validate attachment with missing art id in url property")
    void validateAttachment8() {
        // Given
        data.put("url", "https://deviantart.com/test/art/");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted missing art id in url property.");
    }

    @Test
    @DisplayName("Validate attachment with non existing deviation")
    void validateAttachment9() {
        // Given
        data.put("url", "https://deviantart.com/test/art/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateAttachment(data).isValid(), "Validation accepted non existing url property.");
    }

    @Test
    @DisplayName("Validate attachment and check if no extra params passed to hashmap after validation")
    void validateAttachment10() throws IOException {
        // Given
        data.put("url", deviationUrl);
        data.put("test", "I shouldn't be passed.");
        // When
        var deviantArtResponse = new DeviantArtDeviationQueryDTO();
        deviantArtResponse.setDeviationId(deviationId.toString());

        when(deviantArtScrapperClient.scrapDeviationId(any())).thenReturn(deviationId.toString());
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getDeviation(any(), any())).thenReturn(deviantArtResponse);

        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            mock.when(RequestContextHolder::getRequestAttributes).thenReturn(servletRequestAttributes);

            SourceStrategy.ValidationResult validationResult =
                    deviantArtV1SourceStrategy.validateAttachment(data);

            // Then
            assertAll(() -> {
                assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
                // Check if id was added to data
                assertNull(validationResult.getData().get("test"), "Extra parameter was not removed before validation result.");
            });
        }
    }

    @Test
    @DisplayName("Validate user")
    void validateUser() {
        // Given
        data.put("url", userUrl);
        // When
        var deviantArtResponse = new DeviantArtUserQueryDTO();
        deviantArtResponse.setUser(
                new DeviantArtUserQueryDTO.User(userId.toString(), username)
        );

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getUser(any(), any())).thenReturn(deviantArtResponse);

        SourceStrategy.ValidationResult validationResult = deviantArtV1SourceStrategy.validateUser(data);
        // Then
        assertAll(() -> {
            assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
            assertNotNull(validationResult.getData().get(USER_URL_FIELD), "Validation failed with correct parameters.");
            assertNotNull(validationResult.getData().get(USER_ID_FIELD), "Validation failed with correct parameters.");
            assertNotNull(validationResult.getData().get(USER_USERNAME_FIELD), "Validation failed with correct parameters.");
            assertEquals(3, validationResult.getData().size(), "Validation result data contains too many parameters.");
        });
    }

    @Test
    @DisplayName("Validate user with empty url property")
    void validateUser2() {
        // Given
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate user with non existing url")
    void validateUser3() {
        // Given
        // When
        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getUser(any(), any())).thenReturn(null);
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted empty url.");
    }

    @Test
    @DisplayName("Validate user with invalid domain in url property")
    void validateUser4() {
        // Given
        data.put("url", "test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted invalid url property.");
    }

    @Test
    @DisplayName("Validate user with invalid url given for uri object")
    void validateUser5() {
        // Given
        data.put("url", "https://de`vianta`rt.com");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted invalid url given for uri object.");
    }

    @Test
    @DisplayName("Validate user with not expected domain in url property")
    void validateUser6() {
        // Given
        data.put("url", "test.pl");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted not expected domain in url property.");
    }

    @Test
    @DisplayName("Validate user with missing username in url property")
    void validateUser7() {
        // Given
        data.put("url", "https://deviantart.com/");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted missing art id in url property.");
    }

    @Test
    @DisplayName("Validate user with non existing username")
    void validateUser8() {
        // Given
        data.put("url", "https://deviantart.com/test");
        // When
        // Then
        assertFalse(deviantArtV1SourceStrategy.validateUser(data).isValid(), "Validation accepted non existing url property.");
    }

    @Test
    @DisplayName("Validate user and check if no extra params passed to hashmap after validation")
    void validateUser9() {
        // Given
        data.put("url", userUrl);
        data.put("test", "I shouldn't be passed.");
        // When
        var deviantArtResponse = new DeviantArtUserQueryDTO();
        deviantArtResponse.setUser(
                new DeviantArtUserQueryDTO.User(userId.toString(), username)
        );

        when(keycloakServiceClient.getKeycloakIdentityProviderToken(any(), any(), any())).thenReturn(new KeycloakIdpTokenQueryDTO());
        when(deviantArtServiceClient.getUser(any(), any())).thenReturn(deviantArtResponse);

        SourceStrategy.ValidationResult validationResult = deviantArtV1SourceStrategy.validateUser(data);

        // Then
        assertAll(() -> {
            assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
            assertNull(validationResult.getData().get("test"), "Extra parameter was not removed before validation result.");
        });
    }

}