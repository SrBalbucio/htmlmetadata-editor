package balbucio.htmlmetadataeditor.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import balbucio.htmlmetadataeditor.model.MetaTag;
import balbucio.htmlmetadataeditor.model.MetaTagType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlMetadataService {

    public HtmlDocument parse(File file) throws IOException {
        Document doc = Jsoup.parse(file, "UTF-8");
        String title = doc.title();
        List<MetaTag> tags = new ArrayList<>();

        Elements metas = doc.head().select("meta");
        for (Element meta : metas) {
            if (meta.hasAttr("charset")) {
                tags.add(new MetaTag("charset", meta.attr("charset"), MetaTagType.STANDARD));
            } else if (meta.hasAttr("http-equiv")) {
                String httpEquiv = meta.attr("http-equiv");
                String content = meta.attr("content");
                tags.add(new MetaTag("http-equiv-" + httpEquiv.toLowerCase(), content, MetaTagType.STANDARD));
            } else if (meta.hasAttr("property")) {
                String property = meta.attr("property");
                String content = meta.attr("content");
                if (property.startsWith("og:")) {
                    tags.add(new MetaTag(property, content, MetaTagType.OPEN_GRAPH));
                } else if (property.startsWith("twitter:")) {
                    tags.add(new MetaTag(property, content, MetaTagType.TWITTER));
                } else {
                    tags.add(new MetaTag(property, content, MetaTagType.STANDARD));
                }
            } else if (meta.hasAttr("name")) {
                String name = meta.attr("name");
                String content = meta.attr("content");
                if (name.startsWith("twitter:")) {
                    tags.add(new MetaTag(name, content, MetaTagType.TWITTER));
                } else {
                    tags.add(new MetaTag(name, content, MetaTagType.STANDARD));
                }
            }
        }

        return new HtmlDocument(title, tags, doc);
    }

    public void save(File file, HtmlDocument htmlDoc) throws IOException {
        String title = htmlDoc.getTitle();
        List<MetaTag> tags = htmlDoc.getTags();
        Document doc = htmlDoc.getDocument();

        Element head = doc.head();

        head.select("title").remove();
        head.select("meta").remove();

        if (title != null && !title.isEmpty()) {
            Element titleEl = doc.createElement("title");
            titleEl.text(title);
            head.prependChild(titleEl);
        }

        for (MetaTag tag : tags) {
            Element meta = doc.createElement("meta");

            if ("charset".equals(tag.getKey())) {
                meta.attr("charset", tag.getContent());
            } else if (tag.getKey().startsWith("http-equiv-")) {
                meta.attr("http-equiv", tag.getKey().substring("http-equiv-".length()));
                meta.attr("content", tag.getContent());
            } else {
                String attr = switch (tag.getType()) {
                    case OPEN_GRAPH, TWITTER -> "property";
                    case STANDARD -> "name";
                };
                meta.attr(attr, tag.getKey());
                if (tag.getContent() != null && !tag.getContent().isEmpty()) {
                    meta.attr("content", tag.getContent());
                }
            }

            head.appendChild(meta);
        }

        String html = doc.html();
        Files.writeString(file.toPath(), html, StandardCharsets.UTF_8);
    }
}
