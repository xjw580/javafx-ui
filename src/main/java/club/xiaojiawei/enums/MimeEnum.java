package club.xiaojiawei.enums;

import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/10 18:28
 */
public enum MimeEnum {
    // 文本文件
    TEXT_PLAIN("Text", "text/plain"),
    TEXT_HTML("HTML", "text/html"),

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

    // PDF文件
    APPLICATION_PDF("PDF Document", "application/pdf"),

    // Word文档
    APPLICATION_DOC("Word Document", "application/msword"),
    APPLICATION_DOCX("Word Document (OpenXML)", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    // 视频文件
    VIDEO_MP4("MP4 Video", "video/mp4"),
    VIDEO_MPEG("MPEG Video", "video/mpeg"),

    // 音频文件
    AUDIO_MP3("MP3 Audio", "audio/mp3"),
    AUDIO_WAV("WAV Audio", "audio/wav"),

    // 压缩文件
    APPLICATION_ZIP("ZIP Archive", "application/zip"),
    APPLICATION_TAR("Tar Archive", "application/x-tar"),

    // 添加更多文件类型

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

    public String getDescription() {
        return description;
    }

    public String getMimeType() {
        return mimeType;
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

