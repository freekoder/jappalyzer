package com.vampbear.jappalyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

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
    private String description;
    private final List<Pattern> htmlTemplates = new ArrayList<>();
    private final List<String> domTemplates = new ArrayList<>();
    private final List<Pattern> scriptSrc = new ArrayList<>();
    private final Map<String, Pattern> headerTemplates = new HashMap<>();
    private final Map<String, Pattern> cookieTemplates = new HashMap<>();
    private final Map<String, Pattern> metaTemplates = new HashMap<>();
    private String iconName = "";
    private String website = "";

    public Technology(String name) {
        this.name = name;
    }

    public Technology(String name, String jsonString) {
        this(name, new JSONObject(jsonString));
    }

    public Technology(String name, JSONObject object) {
        this.name = name;

        if (object.has("description")) {
            this.description = object.getString("description");
        }

        List<String> htmlTemplates = readValuesByKey(object, "html");
        for (String template : htmlTemplates) {
            this.addHtmlTemplate(template);
        }

        List<String> domTemplates = readValuesByKey(object, "dom");
        for (String template : domTemplates) {
            this.addDomTemplate(template);
        }

        List<String> scriptSrcTemplates = readValuesByKey(object, "scriptSrc");
        for (String template : scriptSrcTemplates) {
            this.addScriptSrc(template);
        }

        if (object.has("headers")) {
            JSONObject headersObject = object.getJSONObject("headers");
            for (String header : headersObject.keySet()) {
                String headerPattern = headersObject.getString(header);
                this.addHeaderTemplate(header, headerPattern);
            }
        }

        if (object.has("cookies")) {
            JSONObject cookiesObject = object.getJSONObject("cookies");
            for (String cookie : cookiesObject.keySet()) {
                String cookiePattern = cookiesObject.getString(cookie);
                this.addCookieTemplate(cookie, cookiePattern);
            }
        }

        if (object.has("meta")) {
            JSONObject metaObject = object.getJSONObject("meta");
            for (String metaKey : metaObject.keySet()) {
                String metaPattern = metaObject.getString(metaKey);
                this.addMetaTemplate(metaKey, metaPattern);
            }
        }

        if (object.has("website")) {
            String website = object.getString("website");
            this.setWebsite(website);
        }

        if (object.has("icon")) {
            String icon = object.getString("icon");
            this.setIconName(icon);
        }
    }

    private void addMetaTemplate(String name, String pattern) {
        this.metaTemplates.put(name, Pattern.compile(prepareRegexp(pattern)));
    }

    public void addCookieTemplate(String cookie, String cookiePattern) {
        Pattern pattern = Pattern.compile(prepareRegexp(cookiePattern));
        this.cookieTemplates.put(cookie, pattern);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void addHtmlTemplate(String template) {
        Pattern pattern = Pattern.compile(prepareRegexp(template));
        this.htmlTemplates.add(pattern);
    }

    public void addDomTemplate(String template) {
        this.domTemplates.add(template);
    }

    public void addScriptSrc(String scriptSrc) {
        this.scriptSrc.add(Pattern.compile(prepareRegexp(scriptSrc)));
    }

    public List<Pattern> getHtmlTemplates() {
        return htmlTemplates;
    }

    public List<String> getDomTemplates() {
        return domTemplates;
    }

    public List<Pattern> getScriptSrc() {
        return scriptSrc;
    }

    public Map<String, Pattern> getHeaderTemplates() {
        return headerTemplates;
    }

    public Pattern getHeaderTemplates(String headerKey) {
        return headerTemplates.get(headerKey.toLowerCase());
    }

    private static List<String> readValuesByKey(JSONObject object, String key) {
        List<String> values = new ArrayList<>();
        if (object.has(key)) {
            if (object.get(key) instanceof String) {
                values.add(object.getString(key));
            } else if (object.get(key) instanceof JSONArray) {
                JSONArray templates = object.getJSONArray(key);
                for (Object item : templates) {
                    if (item instanceof String) {
                        values.add((String) item);
                    }
                }
            }
        }
        return values;
    }

    @Override
    public String toString() {
        return "Technology{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", iconName='" + iconName + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    // TODO: change String to full Check class
    public String appliebleTo(PageResponse page) {
        Document document = page.getDocument();
        String content = page.getOrigContent();

        if (!page.getHeaders().isEmpty()) {
            for (String header : this.headerTemplates.keySet()) {
                Pattern pattern = this.headerTemplates.get(header);
                if (pattern.toString().isEmpty() && page.getHeaders().containsKey(header)) {
                    return TechnologyMatch.HEADER;
                } else {
                    List<String> headerValues = page.getHeaders().get(header);
                    if (headerValues != null && !headerValues.isEmpty()) {
                        for (String value : headerValues) {
                            Matcher matcher = headerTemplates.get(header).matcher(value);
                            if (matcher.find()) {
                                return TechnologyMatch.HEADER;
                            }
                        }
                    }
                }
            }
        }

        if (!page.getCookies().isEmpty()) {
            for (String cookie : this.cookieTemplates.keySet()) {
                Pattern pattern = this.cookieTemplates.get(cookie);
                if (pattern.toString().isEmpty() && page.getCookies().containsKey(cookie)) {
                    return TechnologyMatch.COOKIE;
                } else {
                    List<String> values = page.getCookies().get(cookie);
                    if (values != null && !values.isEmpty()) {
                        for (String value : values) {
                            Matcher matcher = cookieTemplates.get(cookie).matcher(value);
                            if (matcher.find()) {
                                return TechnologyMatch.COOKIE;
                            }
                        }
                    }
                }
            }
        }

        if (!page.getMetaMap().isEmpty()) {
            for (String name : this.metaTemplates.keySet()) {
                Pattern pattern = this.metaTemplates.get(name);
                if (pattern.toString().isEmpty() && page.getMetaMap().containsKey(name)) {
                    return TechnologyMatch.META;
                } else {
                    String metaContent = page.getMetaMap().get(name);
                    if (metaContent != null && !metaContent.isEmpty()) {
                        Matcher matcher = this.metaTemplates.get(name).matcher(metaContent);
                        if (matcher.find()) {
                            return TechnologyMatch.META;
                        }
                    }
                }
            }
        }

        for (String domTemplate : this.domTemplates) {
            if (containsDomTemplate(document, prepareRegexp(domTemplate), getName())) {
                return "dom";
            }
        }

        for (Pattern scriptSrcPattern : this.scriptSrc) {
            for (String script : page.getScriptSources()) {
                Matcher matcher = scriptSrcPattern.matcher(script);
                if (matcher.find()) {
                    return "script";
                }
            }
        }

        for (Pattern htmlTemplate : this.htmlTemplates) {
            BufferedReader bf = new BufferedReader(new StringReader(content));
            boolean match = bf.lines().anyMatch(line -> htmlTemplate.matcher(line).find());
            if (match) {
                return "html";
            }
        }

        return "";
    }

    public String appliebleTo(String content) {
        return appliebleTo(new PageResponse(content));
    }

    private boolean containsDomTemplate(Document document, String template, String name) {
        try {
            Elements elements = document.select(template);
            return elements.size() > 0;
        } catch (Selector.SelectorParseException e) {
            System.out.println("Technology name " + name);
            e.printStackTrace();
        }
        return false;
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
        this.headerTemplates.put(headerName.toLowerCase(), pattern);
    }

    public Map<String, Pattern> getCookieTemplates() {
        return this.cookieTemplates;
    }
}
