package com.vampbear.jappalyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Technology {

    private final String name;
    private final String description;
    private final List<Pattern> htmlTemplates;
    private final List<String> domTemplates;
    private final List<String> scriptSrc;
    private final Map<String, Pattern> headerTemplates;

    public Technology(String name, String description) {
        this.name = name;
        this.description = description;
        this.htmlTemplates = new ArrayList<>();
        this.domTemplates = new ArrayList<>();
        this.scriptSrc = new ArrayList<>();
        this.headerTemplates = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addHtmlTemplate(String template) {
        Pattern pattern = Pattern.compile(prepareRegexp(template));
        this.htmlTemplates.add(pattern);
    }

    public void addDomTemplate(String template) {
        this.domTemplates.add(template);
    }

    public void addScriptSrc(String scriptSrc) {
        this.scriptSrc.add(scriptSrc);
    }

    @Override
    public String toString() {
        return "Technology{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", htmlTemplates=" + htmlTemplates +
                '}';
    }

    public boolean appliebleTo(PageResponse page) {
        Document document = page.getDocument();
        String content = page.getOrigContent();

        for (String header : this.headerTemplates.keySet()) {
            List<String> headerValues = page.getHeaders().get(header);
            if (headerValues != null && !headerValues.isEmpty()) {
                for (String value : headerValues) {
                    Matcher matcher = headerTemplates.get(header).matcher(value);
                    if (matcher.find()) {
                        return true;
                    }
                }
            }
        }

        for (String domTemplate : this.domTemplates) {
            if (containsDomTemplate(document, domTemplate)) {
                return true;
            }
        }

        Elements scripts = document.select("script");
        for (Element script : scripts) {
            String scriptSrc = script.attr("src");
            if (!scriptSrc.equals("")) {
                for (String scriptSrcTemplate : this.scriptSrc) {
                    if (containsHtmlTemplate(scriptSrc, scriptSrcTemplate)) {
                        return true;
                    }
                }
            }
        }

        for (Pattern htmlTemplate : this.htmlTemplates) {
            BufferedReader bf = new BufferedReader(new StringReader(content));
            boolean match = bf.lines().anyMatch(line -> htmlTemplate.matcher(line).find());
            if (match) {
                return true;
            }
        }

        return false;
    }

    public boolean appliebleTo(String content) {
        return appliebleTo(new PageResponse(content));
    }

    private boolean containsDomTemplate(Document document, String template) {
        Elements elements = document.select(template);
        return elements.size() > 0;
    }

    private boolean containsHtmlTemplate(String content, String htmlTemplate) {
        String preparedTemplate = prepareRegexp(htmlTemplate);
        Pattern pattern = Pattern.compile(preparedTemplate, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    private String prepareRegexp(String pattern) {
        String[] splittedPattern = pattern.split("\\\\;");
        return splittedPattern[0];
    }

    public void addHeaderTemplate(String headerName, String template) {
        Pattern pattern = Pattern.compile(prepareRegexp(template));
        this.headerTemplates.put(headerName, pattern);
    }
}
