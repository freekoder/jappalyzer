package com.vampbear.jappalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageResponse {

    private String origContent;
    private Document document;

    public PageResponse(String content) {
        this.origContent = content;
        this.document = Jsoup.parse(content);
    }

    public String getOrigContent() {
        return origContent;
    }

    public Document getDocument() {
        return document;
    }
}
