package balbucio.htmlmetadataeditor.model;

public class MetaTag {

    private String key;
    private String content;
    private MetaTagType type;

    public MetaTag(String key, String content, MetaTagType type) {
        this.key = key;
        this.content = content;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MetaTagType getType() {
        return type;
    }

    public void setType(MetaTagType type) {
        this.type = type;
    }
}
