package balbucio.htmlmetadataeditor.model;

public class PreviewData {

    private final String title;
    private final String description;
    private final String imageUrl;
    private final String siteName;
    private final String url;

    public PreviewData(String title, String description, String imageUrl, String siteName, String url) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.siteName = siteName;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUrl() {
        return url;
    }
}
