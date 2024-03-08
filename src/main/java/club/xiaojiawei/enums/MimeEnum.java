package club.xiaojiawei.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 文件类型枚举
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/10 18:28
 */
@Getter
public enum MimeEnum {

    // 文本文件
    TEXT_PLAIN("Text", "text/plain"),
    TEXT_HTML("HTML", "text/html"),
    TEXT_JAVA("JAVA", "text/x-java-source"),
    TEXT_ALL("TEXT_ALL", "text/"),

    // 图片文件
    IMAGE_JPEG("JPEG Image", "image/jpeg"),
    IMAGE_PNG("PNG Image", "image/png"),
    IMAGE_GIF("GIF Image", "image/gif"),
    IMAGE_BMP("BMP Image", "image/bmp"),
    IMAGE_TIFF("TIFF Image", "image/tiff"),
    IMAGE_WEBP("WEBP Image", "image/webp"),
    IMAGE_SVG("SVG Image", "image/svg+xml"),
    IMAGE_ICON("ICO Image", "image/vnd.microsoft.icon"),
    IMAGE_ICO("Icon Image", "image/x-icon"),
    IMAGE_PSD("Photoshop Document", "image/vnd.adobe.photoshop"),
    IMAGE_ALL("Photoshop Document", "image/"),

    // PDF文件
    APPLICATION_PDF("PDF Document", "application/pdf"),

    // Word文档
    APPLICATION_DOC("Word Document", "application/msword"),
    APPLICATION_DOCX("Word Document (OpenXML)", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    // 视频文件
    VIDEO_MP4("MP4 Video", "video/mp4"),
    VIDEO_MPEG("MPEG Video", "video/mpeg"),
    VIDEO_WEBM("MPEG Video", "video/webm"),
    VIDEO_X_MSVIDEO("MPEG Video", "video/x-msvideo"),
    VIDEO_OGG("MPEG Video", "video/ogg"),
    VIDEO_ALL("VIDEO_ALL", "video/"),

    // 音频文件
    AUDIO_MP3("MP3 Audio", "audio/mp3"),
    AUDIO_MPEG("MP3 Audio", "audio/mpeg"),
    AUDIO_WAV("WAV Audio", "audio/wav"),
    AUDIO_OGG("WAV Audio", "audio/ogg"),
    AUDIO_FLAC("WAV Audio", "audio/flac"),
    AUDIO_AAC("WAV Audio", "audio/aac"),
    AUDIO_ALL("AUDIO_ALL", "audio/"),

    // 压缩文件
    APPLICATION_ZIP("ZIP Archive", "application/zip"),
    APPLICATION_TAR("Tar Archive", "application/x-tar"),
    APPLICATION_GZIP("Tar Archive", "application/gzip"),
    APPLICATION_7Z("Tar Archive", "application/x-7z-compressed"),
    APPLICATION_BZIP2("Tar Archive", "application/x-bzip2"),

    // 默认的“其他”文件类型
    APPLICATION_OCTET_STREAM("Other", "application/octet-stream"),

    // 所有类型
    ALL("All", "all"),
    ;

    private final String description;

    private final String mimeType;

    MimeEnum(String description, String mimeType) {
        this.description = description;
        this.mimeType = mimeType;
    }

    public static MimeEnum getMimeTypeByExtension(String mimeType) {
        for (MimeEnum mimeEnum : MimeEnum.values()) {
            if (Objects.equals(mimeEnum.mimeType, mimeType.toLowerCase())) {
                return mimeEnum;
            }
        }
        return APPLICATION_OCTET_STREAM;
    }
}

