package balbucio.htmlmetadataeditor.service;

import java.util.List;

import balbucio.htmlmetadataeditor.model.MetaTag;
import org.jsoup.nodes.Document;

public class HtmlDocument {

    private String title;
    private List<MetaTag> tags;
    private Document document;

    public HtmlDocument(String title, List<MetaTag> tags, Document document) {
        this.title = title;
        this.tags = tags;
        this.document = document;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MetaTag> getTags() {
        return tags;
    }

    public void setTags(List<MetaTag> tags) {
        this.tags = tags;
    }

    public Document getDocument() {
        return document;
    }
}
