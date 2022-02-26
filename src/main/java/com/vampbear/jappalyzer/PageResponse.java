package com.vampbear.jappalyzer;

import java.net.HttpCookie;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers = new HashMap<>();
    private final Map<String, List<String>> cookies = new HashMap<>();
    private Document document;
    private String origContent;
    private final List<String> scriptSources = new ArrayList<>();

    public PageResponse(String content) {
        this(200, Collections.emptyMap(), content);
    }

    public PageResponse(int statusCode, Map<String, List<String>> headers, String content) {
        this.statusCode = statusCode;
        this.setHeaders(headers);
        this.processContent(content);
    }

    private void processContent(String content) {
        this.origContent = content;
        this.document = Jsoup.parse(content);

        Elements scripts = document.select("script");
        for (Element script : scripts) {
            String scriptSrc = script.attr("src");
            if (!scriptSrc.equals("")) {
                this.scriptSources.add(scriptSrc);
            }
        }
    }

    public void setHeaders(Map<String, List<String>> headers) {
        if (headers == null) return;
        this.headers.putAll(headers);
        processCookies(headers.get("Set-Cookie"));
        processCookies(headers.get("Cookie"));
    }

    private void processCookies(List<String> cookieValues) {
        if (cookieValues == null) return;
        for (String cookieValue : cookieValues) {
            List<HttpCookie> cookies = HttpCookie.parse(cookieValue);
            for (HttpCookie cookie : cookies) {
                this.addCookie(cookie.getName(), cookie.getValue());
            }
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getOrigContent() {
        return origContent;
    }

    public Document getDocument() {
        return document;
    }

    public void addCookie(String name, String value) {
        this.cookies.computeIfAbsent(name, k -> new ArrayList<>());
        this.cookies.get(name).add(value);
    }

    public Map<String, List<String>> getCookies() {
        return this.cookies;
    }

    public List<String> getScriptSources() {
        return this.scriptSources;
    }
}
