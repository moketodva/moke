package cn.moke.generator.enums;

/**
 * @author moke
 */
public enum FileType {

    /**
     * JPEG
     */
    JPEG("FFD8FF", ".jpg"),

    /**
     * PNG
     */
    PNG("89504E47", ".png"),

    /**
     * GIF
     */
    GIF("47494638", ".gif"),

    /**
     * TIFF
     */
    TIFF("49492A00", ".tif"),

    /**
     * Windows bitmap
     */
    BMP("424D", ".bmp"),

    /**
     * XML
     */
    XML("3C3F786D6C", ".xml"),

    /**
     * HTML
     */
    HTML("68746D6C3E", ".html"),

    /**
     * Microsoft Word/Excel（特殊：.doc or .xls）
     */
    XLS_DOC("D0CF11E0"),

    /**
     * Adobe Acrobat
     */
    PDF("255044462D312E", ".pdf"),

    /**
     * ZIP Archive
     */
    ZIP("504B0304", ".zip"),

    /**
     * ARAR Archive
     */
    RAR("52617221", ".rar"),

    /**
     * WAVE
     */
    WAV("57415645", ".wav"),

    /**
     * AVI
     */
    AVI("41564920", ".avi"),

    /**
     * Real Audio
     */
    RAM("2E7261FD", ".ram"),

    /**
     * Real Media
     */
    RM("2E524D46", ".rm"),

    /**
     * Quicktime
     */
    MOV("6D6F6F76", ".mov");

    /**
     * 文件头（16进制）
     */
    private String value;
    /**
     * 后缀名
     */
    private String suffix;

    FileType(String value) {
        this.value = value;
    }

    FileType(String value, String suffix) {
        this(value);
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getValue() {
        return value;
    }
}