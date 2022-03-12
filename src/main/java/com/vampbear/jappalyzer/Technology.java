package com.vampbear.jappalyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Technology {

    private final String name;
    private String description;
    private final List<Pattern> htmlTemplates = new ArrayList<>();
    private final List<String> domTemplates = new ArrayList<>();
    private final List<Pattern> scriptSrc = new ArrayList<>();
    private final Map<String, List<Pattern>> headerTemplates = new HashMap<>();
    private final Map<String, List<Pattern>> cookieTemplates = new HashMap<>();
    private final Map<String, List<Pattern>> metaTemplates = new HashMap<>();
    private String iconName = "";
    private String website = "";
    private String cpe = "";
    private boolean saas = false;
    private final List<String> pricing = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();

    public Technology(String name) {
        this.name = name;
    }

    public Technology(String name, String jsonString) {
        this(name, new JSONObject(jsonString));
    }

    public Technology(String name, String jsonString, Categories categories) {
        this(name, new JSONObject(jsonString), categories);
    }

    public Technology(String name, JSONObject object) {
        this(name, object, new Categories(Collections.emptyList()));
    }

    public Technology(String name, JSONObject object, Categories categories) {
        this.name = name;

        this.description = readStringOrEmpty("description", object);
        this.website = readStringOrEmpty("website", object);
        this.iconName = readStringOrEmpty("icon", object);
        this.cpe = readStringOrEmpty("cpe", object);
        this.saas = readBooleanOrFalse("saas", object);


        if (object.has("cats")) {
            JSONArray array = object.getJSONArray("cats");
            for (int i = 0; i < array.length(); i++) {
                int categoryId = array.getInt(i);
                Category category = categories.getCategoryById(categoryId);
                this.categories.add(category);
            }
        }

        if (object.has("pricing")) {
            JSONArray pricings = object.getJSONArray("pricing");
            for (int i = 0; i < pricings.length(); i++) {
                this.pricing.add(pricings.getString(i));
            }
        }

        if (object.has("html")) {
            List<String> htmlTemplates = readValuesFromObject(object.get("html"));
            for (String template : htmlTemplates) {
                this.addHtmlTemplate(template);
            }
        }

        if (object.has("dom")) {
            List<String> domTemplates = readValuesFromObject(object.get("dom"));
            for (String template : domTemplates) {
                this.addDomTemplate(template);
            }
        }

        if (object.has("scriptSrc")) {
            List<String> scriptSrcTemplates = readValuesFromObject(object.get("scriptSrc"));
            for (String template : scriptSrcTemplates) {
                this.addScriptSrc(template);
            }
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
            for (String key : metaObject.keySet()) {
                List<String> patterns = readValuesFromObject(metaObject.get(key));
                for (String pattern : patterns) {
                    this.addMetaTemplate(key, pattern);
                }
            }
        }
    }

    private boolean readBooleanOrFalse(String key, JSONObject object) {
        if (object.has(key) && (object.get(key) instanceof Boolean)) {
            return object.getBoolean(key);
        } else {
            return false;
        }
    }

    private String readStringOrEmpty(String key, JSONObject object) {
        if (object.has(key) && (object.get(key) instanceof String)) {
            return object.getString(key);
        } else {
            return "";
        }
    }

    private List<String> readValuesFromObject(Object jsonObject) {
        ArrayList<String> patterns = new ArrayList<>();
        if (jsonObject instanceof JSONArray) {
            for (Object arrayItem : (JSONArray)jsonObject) {
                patterns.add((String) arrayItem);
            }
        } else if (jsonObject instanceof String) {
            patterns.add((String)jsonObject);
        }
        return patterns;
    }

    private void addMetaTemplate(String name, String pattern) {
        this.metaTemplates.putIfAbsent(name, new ArrayList<>());
        this.metaTemplates.get(name).add(Pattern.compile(prepareRegexp(pattern)));
    }

    public void addCookieTemplate(String cookie, String cookiePattern) {
        this.cookieTemplates.putIfAbsent(cookie, new ArrayList<>());
        this.cookieTemplates.get(cookie).add(Pattern.compile(prepareRegexp(cookiePattern)));
    }

    public void addHeaderTemplate(String headerName, String template) {
        this.headerTemplates.putIfAbsent(headerName.toLowerCase(), new ArrayList<>());
        this.headerTemplates.get(headerName.toLowerCase()).add(Pattern.compile(prepareRegexp(template)));
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

    public Map<String, List<Pattern>> getHeaderTemplates() {
        return headerTemplates;
    }

    public List<Pattern> getHeaderTemplates(String headerKey) {
        return headerTemplates.get(headerKey.toLowerCase());
    }

    public String getCpe() {
        return cpe;
    }

    public boolean isSaas() {
        return saas;
    }

    public List<String> getPricing() {
        return pricing;
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

    public TechnologyMatch appliebleTo(PageResponse page) {
        long startTimestamp = System.currentTimeMillis();
        Document document = page.getDocument();
        String content = page.getOrigContent();

        if (!page.getHeaders().isEmpty()) {
            for (String header : this.headerTemplates.keySet()) {
                List<Pattern> patterns = this.headerTemplates.get(header);
                for (Pattern pattern : patterns) {
                    if (pattern.toString().isEmpty() && page.getHeaders().containsKey(header)) {
                        long endTimestamp = System.currentTimeMillis();
                        return new TechnologyMatch(this, TechnologyMatch.HEADER, endTimestamp - startTimestamp);
                    } else {
                        List<String> headerValues = page.getHeaders().get(header);
                        if (headerValues != null && !headerValues.isEmpty()) {
                            for (String value : headerValues) {
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    long endTimestamp = System.currentTimeMillis();
                                    return new TechnologyMatch(this, TechnologyMatch.HEADER, endTimestamp -startTimestamp);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!page.getCookies().isEmpty()) {
            for (String cookie : this.cookieTemplates.keySet()) {
                List<Pattern> patterns = this.cookieTemplates.get(cookie);
                for (Pattern pattern : patterns) {
                    if (pattern.toString().isEmpty() && page.getCookies().containsKey(cookie)) {
                        long endTimestamp = System.currentTimeMillis();
                        return new TechnologyMatch(this, TechnologyMatch.COOKIE, endTimestamp - startTimestamp);
                    } else {
                        List<String> values = page.getCookies().get(cookie);
                        if (values != null && !values.isEmpty()) {
                            for (String value : values) {
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    long endTimestamp = System.currentTimeMillis();
                                    return new TechnologyMatch(this, TechnologyMatch.COOKIE, endTimestamp - startTimestamp);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!page.getMetaMap().isEmpty()) {
            for (String name : this.metaTemplates.keySet()) {
                List<Pattern> patterns = this.metaTemplates.get(name);
                for (Pattern pattern : patterns) {
                    if (pattern.toString().isEmpty() && page.getMetaMap().containsKey(name)) {
                        long endTimestamp = System.currentTimeMillis();
                        return new TechnologyMatch(this, TechnologyMatch.META, endTimestamp - startTimestamp);
                    } else {
                        String metaContent = page.getMetaMap().get(name);
                        if (metaContent != null && !metaContent.isEmpty()) {
                            Matcher matcher = pattern.matcher(metaContent);
                            if (matcher.find()) {
                                long endTimestamp = System.currentTimeMillis();
                                return new TechnologyMatch(this, TechnologyMatch.META, endTimestamp - startTimestamp);
                            }
                        }
                    }
                }
            }
        }

        for (String domTemplate : this.domTemplates) {
            if (containsDomTemplate(document, prepareRegexp(domTemplate), getName())) {
                long endTimestamp = System.currentTimeMillis();
                return new TechnologyMatch(this, TechnologyMatch.DOM, endTimestamp - startTimestamp);
            }
        }

        for (Pattern scriptSrcPattern : this.scriptSrc) {
            for (String script : page.getScriptSources()) {
                Matcher matcher = scriptSrcPattern.matcher(script);
                if (matcher.find()) {
                    long endTimestamp = System.currentTimeMillis();
                    return new TechnologyMatch(this, TechnologyMatch.SCRIPT, endTimestamp - startTimestamp);
                }
            }
        }

        for (Pattern htmlTemplate : this.htmlTemplates) {
            BufferedReader bf = new BufferedReader(new StringReader(content));
            boolean match = bf.lines().anyMatch(line -> htmlTemplate.matcher(line).find());
            if (match) {
                long endTimestamp = System.currentTimeMillis();
                return new TechnologyMatch(this, TechnologyMatch.HTML, endTimestamp - startTimestamp);
            }
        }

        long endTimestamp = System.currentTimeMillis();
        return TechnologyMatch.notMatched(this,endTimestamp - startTimestamp);
    }

    public TechnologyMatch appliebleTo(String content) {
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

    private String prepareRegexp(String pattern) {
        String[] splittedPattern = pattern.split("\\\\;");
        return splittedPattern[0];
    }

    public Map<String, List<Pattern>> getCookieTemplates() {
        return this.cookieTemplates;
    }

    public String getCPE() {
        return this.cpe;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public List<Pattern> getMetaTemplates(String name) {
        List<Pattern> patterns = this.metaTemplates.get(name);
        if (patterns == null) {
            return Collections.emptyList();
        }
        return patterns;
    }
}
