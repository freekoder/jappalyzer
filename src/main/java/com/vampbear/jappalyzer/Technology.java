package com.vampbear.jappalyzer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    private final List<String> scriptSrc = new ArrayList<>();
    private final Map<String, Pattern> headerTemplates = new HashMap<>();
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
        if (object.has("website")) {
            String website = object.getString("website");
            this.setWebsite(website);
        }
        if (object.has("icon")) {
            String icon = object.getString("icon");
            this.setIconName(icon);
        }
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
        this.scriptSrc.add(scriptSrc);
    }

    public List<Pattern> getHtmlTemplates() {
        return htmlTemplates;
    }

    public List<String> getDomTemplates() {
        return domTemplates;
    }

    public List<String> getScriptSrc() {
        return scriptSrc;
    }

    public Map<String, Pattern> getHeaderTemplates() {
        return headerTemplates;
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

        for (String header : this.headerTemplates.keySet()) {
            List<String> headerValues = page.getHeaders().get(header);
            if (headerValues != null && !headerValues.isEmpty()) {
                for (String value : headerValues) {
                    Matcher matcher = headerTemplates.get(header).matcher(value);
                    if (matcher.find()) {
                        return "header";
                    }
                }
            }
        }

        for (String domTemplate : this.domTemplates) {
            if (containsDomTemplate(document, prepareRegexp(domTemplate), getName())) {
                return "dom";
            }
        }

        Elements scripts = document.select("script");
        for (Element script : scripts) {
            String scriptSrc = script.attr("src");
            if (!scriptSrc.equals("")) {
                for (String scriptSrcTemplate : this.scriptSrc) {
                    if (containsHtmlTemplate(scriptSrc, scriptSrcTemplate)) {
                        return "script";
                    }
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
        this.headerTemplates.put(headerName, pattern);
    }
}
