package ws.furrify.posts.media.strategy;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jaudiotagger.audio.aiff.AiffTagReader;
import org.jaudiotagger.audio.asf.AsfFileReader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacTagReader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.mp4.Mp4TagReader;
import org.jaudiotagger.audio.ogg.OggVorbisTagReader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.aiff.AiffTag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.utils.GifDecoder;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.VideoFrameExtractionFailedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ws.furrify.posts.media.MediaExtension.EXTENSION_GIF;

/**
 * Utils class for media upload strategy.
 *
 * @author sky
 */
@Log4j2
public class MediaUploadStrategyUtils {

    private final static int PART_OF_VIDEO_TO_THUMBNAIL = 3;
    private final static int TIMOUT_FRAME_EXTRACTION_SECONDS = 10;

    private final static String TEMP_FILE_PREFIX = "furrify_temp_file_";

    /**
     * Generates thumbnail for file with differnet strategy for different filetypes.
     * For ex. Video will have extracted frame, Audio will have extracted album cover.
     * If file does not contain artwork null will be returned.
     *
     * @param extension Media extension of source file.
     * @param width     Width of thumbnail.
     * @param quality   Quality of thumbnail form 0 to 1.
     * @param source    Source file input stream.
     * @return JPG thumbnail input stream or null.
     * @throws IOException Error has occurred processing the file.
     */
    public static InputStream generateThumbnail(final MediaExtension extension,
                                                final int width,
                                                final float quality,
                                                final InputStream source) throws IOException {
        return switch (extension.getType()) {
            case IMAGE -> generateThumbnailForImage(width, quality, source);
            case VIDEO -> generateThumbnailForImage(
                    width,
                    quality,
                    extractFrameForVideo(source)
            );
            case ANIMATION -> {
                // Workaround for gif
                if (extension == EXTENSION_GIF) {
                    InputStream frame = extractFirstFrameFromGif(source);

                    yield generateThumbnailForImage(width, quality, frame);
                }

                yield null;
            }
            case AUDIO -> {
                InputStream artwork = extractAudioFileAlbumCover(source, extension);
                if (artwork != null) {
                    yield generateThumbnailForImage(
                            width,
                            quality,
                            artwork
                    );
                }

                yield null;
            }
        };
    }

    private static InputStream extractAudioFileAlbumCover(final InputStream source, final MediaExtension extension) {
        ByteArrayInputStream albumCoverInputStream = null;
        File tempFile = null;

        try {
            // Create temp file for artwork extraction
            tempFile = File.createTempFile(TEMP_FILE_PREFIX, null);
            tempFile.deleteOnExit();

            // Copy input stream to tmp file
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(source, out);
            }

            // Handle extension for each format to extract thumbnail
            switch (extension) {
                case EXTENSION_MP3 -> {
                    MP3FileReader mp3FileReader = new MP3FileReader();
                    MP3File mp3File = (MP3File) mp3FileReader.read(tempFile);

                    Artwork artwork = mp3File.getTag().getFirstArtwork();
                    if (artwork != null) {
                        albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                    }
                }

                case EXTENSION_FLAC -> {
                    FlacTagReader flacTagReader = new FlacTagReader();
                    FlacTag tag = flacTagReader.read(tempFile.toPath());

                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                    }
                }

                case EXTENSION_OGG -> {
                    try (RandomAccessFile oggRandomAccessFile = new RandomAccessFile(tempFile, "r")) {
                        OggVorbisTagReader oggVorbisTagReader = new OggVorbisTagReader();

                        VorbisCommentTag oggTag = (VorbisCommentTag) oggVorbisTagReader.read(oggRandomAccessFile);

                        Artwork artwork = oggTag.getFirstArtwork();
                        if (artwork != null) {
                            albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                        }
                    }
                }

                case EXTENSION_AIF, EXTENSION_AIFF -> {
                    AiffTagReader aiffTagReader = new AiffTagReader("furrify-aiff-tag-reader");
                    AiffTag tag = aiffTagReader.read(tempFile.toPath());

                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                    }
                }

                case EXTENSION_WMA -> {
                    AsfFileReader asfFileReader = new AsfFileReader();
                    Tag tag = asfFileReader.read(tempFile).getTag();

                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                    }
                }


                case EXTENSION_MP4_AUDIO -> {
                    Mp4TagReader mp4TagReader = new Mp4TagReader();
                    Mp4Tag tag = mp4TagReader.read(tempFile.toPath());

                    Artwork artwork = tag.getFirstArtwork();
                    if (artwork != null) {
                        albumCoverInputStream = new ByteArrayInputStream(artwork.getBinaryData());
                    }
                }
            }

        } catch (TagException | InvalidAudioFrameException | ReadOnlyFileException |
                 CannotReadException |
                 IOException e) {

            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        } finally {

            if (tempFile != null && !tempFile.delete()) {
                log.warn("Temp file [filename=" + tempFile.getName() + "] could not be deleted from temp directory.");
            }
        }

        return albumCoverInputStream;
    }

    private static InputStream extractFirstFrameFromGif(final InputStream source) throws IOException {
        /*
            TODO Fix gif when JDK is fixed
            Current JDK Gif decoder is broken. It only allows 4000 ish frames. Included workaround below.
         */
        GifDecoder.GifImage gifImage = GifDecoder.read(source);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(gifImage.getFrame(0), "png", output);

        return new ByteArrayInputStream(output.toByteArray());
    }

    private static InputStream generateThumbnailForImage(final int width,
                                                         final float quality,
                                                         final InputStream source) throws IOException {

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Thumbnails.of(source)
                    .width(width)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toOutputStream(output);

            return new ByteArrayInputStream(output.toByteArray());
        }
    }

    private static InputStream extractFrameForVideo(final InputStream source) {

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, TIMOUT_FRAME_EXTRACTION_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        Future<InputStream> inputStreamFuture = threadPool.submit(() -> {

            try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(source);
                 Java2DFrameConverter converter = new Java2DFrameConverter()) {

                frameGrabber.start();
                frameGrabber.setVideoFrameNumber(frameGrabber.getLengthInFrames() / PART_OF_VIDEO_TO_THUMBNAIL);

                Frame frame = frameGrabber.grabImage();
                BufferedImage image = converter.convert(frame);

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, "png", output);

                frameGrabber.stop();

                return new ByteArrayInputStream(output.toByteArray());
            } catch (IOException e) {
                throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
            }
        });

        try {
            return inputStreamFuture.get(TIMOUT_FRAME_EXTRACTION_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Interrupt task
            inputStreamFuture.cancel(true);

            throw new VideoFrameExtractionFailedException(Errors.VIDEO_FRAME_EXTRACTION_FAILED.getErrorMessage());
        }
    }

}
