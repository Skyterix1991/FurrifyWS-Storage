package ws.furrify.artists.avatar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Supported avatar extensions by system.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AvatarExtension {
    /**
     * File extensions
     */
    EXTENSION_JPEG("jpeg", MediaType.IMAGE, "image/jpeg"),
    EXTENSION_PNG("png", MediaType.IMAGE, "image/png"),
    EXTENSION_JPG("jpg", MediaType.IMAGE, "image/jpeg");

    /**
     * File extension.
     */
    private final String extension;

    /**
     * Mime type of extension.
     */
    private final String[] mimeTypes;

    /**
     * Avatar type ex. VIDEO, IMAGE.
     */
    private final MediaType type;

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9](?:[a-zA-Z0-9() ._-]*[a-zA-Z0-9() ._-])?\\.[a-zA-Z0-9_-]+$");

    private final static short MAX_FILENAME_LENGTH = 255;

    AvatarExtension(final String extension, final MediaType type, final String... mimeTypes) {
        this.extension = extension;
        this.mimeTypes = mimeTypes;
        this.type = type;
    }

    public static boolean isFileContentValid(String filename,
                                             MultipartFile file,
                                             AvatarExtension avatarExtension) {
        try {
            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return Arrays.asList(avatarExtension.getMimeTypes()).contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFilenameValid(String filename) {
        return filename != null && FILENAME_PATTERN.matcher(filename).matches() && filename.length() <= MAX_FILENAME_LENGTH;
    }

    /**
     * Avatar type file represents.
     */
    private enum MediaType {
        /**
         * Image file type.
         */
        IMAGE
    }
}
