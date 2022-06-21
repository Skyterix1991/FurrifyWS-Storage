package ws.furrify.posts.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Supported attachment extensions by system.
 *
 * @author Skyte
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AttachmentExtension {
    /**
     * File extensions
     */
    EXTENSION_MID("MID", AttachmentType.MUSIC_DIGITAL_INTERFACE, "application/vnd.microsoft.portable-executable"),
    EXTENSION_MIDI("MIDI", AttachmentType.MUSIC_DIGITAL_INTERFACE, "application/java-archive"),

    EXTENSION_EXE("EXE", AttachmentType.PROGRAM, "application/vnd.microsoft.portable-executable"),
    EXTENSION_JAR("JAR", AttachmentType.PROGRAM, "application/java-archive"),

    EXTENSION_PPT("PPT", AttachmentType.PRESENTATION, "application/vnd.ms-powerpoint"),
    EXTENSION_PPTX("PPTX", AttachmentType.PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    EXTENSION_ODT("ODT", AttachmentType.TEXT_DOCUMENT, "application/vnd.oasis.opendocument.text"),
    EXTENSION_TXT("TXT", AttachmentType.TEXT_DOCUMENT, "text/plain"),
    EXTENSION_DOC("DOC", AttachmentType.TEXT_DOCUMENT, "application/msword"),
    EXTENSION_DOCX("DOCX", AttachmentType.TEXT_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    EXTENSION_TORRENT("TORRENT", AttachmentType.TORRENT, "application/x-bittorrent"),

    EXTENSION_AZW3("AZW3", AttachmentType.EBOOK, "application/vnd.amazon.mobi8-ebook"),
    EXTENSION_MOBI("MOBI", AttachmentType.EBOOK, "application/x-mobipocket-ebook"),
    EXTENSION_EPUB("EPUB", AttachmentType.EBOOK, "application/epub+zip"),

    EXTENSION_PDF("PDF", AttachmentType.ADOBE_DOCUMENT, "application/pdf"),

    EXTENSION_SAI("SAI", AttachmentType.PAINTTOOL_SAI_DOCUMENT, "application/octet-stream"),

    EXTENSION_ZIP("ZIP", AttachmentType.COMPRESSED_ARCHIVE, "application/zip"),
    EXTENSION_RAR("RAR", AttachmentType.COMPRESSED_ARCHIVE, "application/vnd.rar"),
    EXTENSION_BZIP("BZIP", AttachmentType.COMPRESSED_ARCHIVE, "application/x-bzip"),
    EXTENSION_BZIP2("BZIP2", AttachmentType.COMPRESSED_ARCHIVE, "application/x-bzip2"),

    EXTENSION_TAR("TAR", AttachmentType.ARCHIVE, "application/x-tar"),

    EXTENSION_GZ("GZ", AttachmentType.COMPRESSED, "application/gzip"),

    EXTENSION_SWF("SWF", AttachmentType.ADOBE_FLASH, "application/x-shockwave-flash"),

    EXTENSION_PSD("PSD", AttachmentType.PHOTOSHOP_DOCUMENT, "image/vnd.adobe.photoshop"),

    EXTENSION_BLEND("BLEND", AttachmentType.BLENDER_PROJECT, "application/octet-stream");

    /**
     * File extension in uppercase.
     */
    private final String extension;

    /**
     * Mime type of extension.
     */
    private final String[] mimeTypes;

    /**
     * Attachment type ex. PHOTOSHOP_DOCUMENT.
     */
    private final AttachmentType type;

    private final static Pattern FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9](?:[a-zA-Z0-9() ._-]*[a-zA-Z0-9() ._-])?\\.[a-zA-Z0-9_-]+$");

    private final static short MAX_FILENAME_LENGTH = 255;

    AttachmentExtension(final String extension, final AttachmentType type, final String... mimeTypes) {
        this.extension = extension;
        this.mimeTypes = mimeTypes;
        this.type = type;
    }

    public static boolean isFileContentValid(String filename,
                                             MultipartFile file,
                                             AttachmentExtension attachmentExtension) {
        try {
            // Get file mimetype
            String mimeType = FileUtils.getMimeType(filename, file.getInputStream());

            return Arrays.asList(attachmentExtension.getMimeTypes()).contains(mimeType);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFilenameValid(String filename) {
        return filename != null && FILENAME_PATTERN.matcher(filename).matches() && filename.length() <= MAX_FILENAME_LENGTH;
    }

    /**
     * Attachment type file represents.
     */
    private enum AttachmentType {
        /**
         * Presentation file
         */
        PRESENTATION,
        /**
         * Musical Instrument Digital Interface (MIDI)
         */
        MUSIC_DIGITAL_INTERFACE,
        /**
         * Program file
         */
        PROGRAM,
        /**
         * Torrent file
         */
        TORRENT,
        /**
         * Text document
         */
        TEXT_DOCUMENT,
        /**
         * eBook format
         */
        EBOOK,
        /**
         * PDF document
         */
        ADOBE_DOCUMENT,
        /**
         * PaintTool SAI document
         */
        PAINTTOOL_SAI_DOCUMENT,
        /**
         * Photoshop Document
         */
        PHOTOSHOP_DOCUMENT,
        /**
         * Blender project file
         */
        BLENDER_PROJECT,
        /**
         * Adobe flash file
         */
        ADOBE_FLASH,
        /**
         * Compressed archive file
         */
        COMPRESSED_ARCHIVE,
        /**
         * Archive file
         */
        ARCHIVE,
        /**
         * Compressed file
         */
        COMPRESSED
    }
}
