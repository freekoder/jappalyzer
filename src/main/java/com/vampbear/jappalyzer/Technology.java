package com.vampbear.jappalyzer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Technology {

    private final String name;
    private String description;
    private String iconName;
    private String website;
    private String cpe;
    private boolean saas;
    private final List<String> pricing = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final List<String> implies = new LinkedList<>();

    private final List<Pattern> htmlTemplates = new ArrayList<>();
    private final List<DomPattern> domTemplates = new ArrayList<>();
    private final List<Pattern> scriptSrc = new ArrayList<>();
    private final Map<String, List<Pattern>> headerTemplates = new HashMap<>();
    private final Map<String, List<Pattern>> cookieTemplates = new HashMap<>();
    private final Map<String, List<Pattern>> metaTemplates = new HashMap<>();

    public Technology(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCPE() {
        return this.cpe;
    }

    public void setCPE(String cpe) {
        this.cpe = cpe;
    }

    public boolean isSaas() {
        return saas;
    }

    public void setSaas(boolean saas) {
        this.saas = saas;
    }

    public List<String> getPricing() {
        return pricing;
    }

    public void addPricing(String pricing) {
        this.pricing.add(pricing);
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public List<String> getImplies() {
        return this.implies;
    }

    public void addImplies(String imply) {
        this.implies.add(imply);
    }

    public List<Pattern> getHtmlTemplates() {
        return htmlTemplates;
    }

    public void addHtmlTemplate(String template) {
        Pattern pattern = Pattern.compile(prepareRegexp(template));
        this.htmlTemplates.add(pattern);
    }

    public List<Pattern> getMetaTemplates(String name) {
        List<Pattern> patterns = this.metaTemplates.get(name);
        if (patterns == null) {
            return Collections.emptyList();
        }
        return patterns;
    }

    public void addMetaTemplate(String name, String pattern) {
        this.metaTemplates.putIfAbsent(name, new ArrayList<>());
        this.metaTemplates.get(name).add(Pattern.compile(prepareRegexp(pattern)));
    }

    public Map<String, List<Pattern>> getCookieTemplates() {
        return this.cookieTemplates;
    }

    public void addCookieTemplate(String cookie, String cookiePattern) {
        this.cookieTemplates.putIfAbsent(cookie, new ArrayList<>());
        this.cookieTemplates.get(cookie).add(Pattern.compile(prepareRegexp(cookiePattern)));
    }

    public Map<String, List<Pattern>> getHeaderTemplates() {
        return headerTemplates;
    }

    public List<Pattern> getHeaderTemplates(String headerKey) {
        return headerTemplates.get(headerKey.toLowerCase());
    }

    public void addHeaderTemplate(String headerName, String template) {
        this.headerTemplates.putIfAbsent(headerName.toLowerCase(), new ArrayList<>());
        this.headerTemplates.get(headerName.toLowerCase()).add(Pattern.compile(prepareRegexp(template)));
    }

    public List<DomPattern> getDomPatterns() {
        return domTemplates;
    }

    public void addDomPattern(DomPattern template) {
        this.domTemplates.add(template);
    }

    public List<Pattern> getScriptSrc() {
        return scriptSrc;
    }

    public void addScriptSrc(String scriptSrc) {
        this.scriptSrc.add(Pattern.compile(prepareRegexp(scriptSrc)));
    }

    public TechnologyMatch applicableTo(String content) {
        return applicableTo(new PageResponse(content));
    }

    public TechnologyMatch applicableTo(PageResponse page) {
        long startTimestamp = System.currentTimeMillis();

        if (!page.getHeaders().isEmpty()) {
            boolean match = getTechnologyMapMatch(this.headerTemplates, page.getHeaders());
            long duration = System.currentTimeMillis() - startTimestamp;
            if (match) return new TechnologyMatch(this, TechnologyMatch.HEADER, duration);
        }

        if (!page.getCookies().isEmpty()) {
            boolean match = getTechnologyMapMatch(this.cookieTemplates, page.getCookies());
            long duration = System.currentTimeMillis() - startTimestamp;
            if (match) return new TechnologyMatch(this, TechnologyMatch.COOKIE, duration);
        }

        if (!page.getMetaMap().isEmpty()) {
            boolean match = getTechnologyMapMatch(this.metaTemplates, page.getMetaMap());
            long duration = System.currentTimeMillis() - startTimestamp;
            if (match) return new TechnologyMatch(this, TechnologyMatch.META, duration);
        }

        for (DomPattern domTemplate : this.domTemplates) {
            if (domTemplate.applicableToDocument(page.getDocument())) {
                long duration  = System.currentTimeMillis() - startTimestamp;
                return new TechnologyMatch(this, TechnologyMatch.DOM, duration);
            }
        }

        for (Pattern scriptSrcPattern : this.scriptSrc) {
            boolean match = getTechnologyStringListMatch(page.getScriptSources(), scriptSrcPattern);
            long duration  = System.currentTimeMillis() - startTimestamp;
            if (match) return new TechnologyMatch(this, TechnologyMatch.SCRIPT, duration);
        }

        for (Pattern htmlTemplate : this.htmlTemplates) {
            boolean match = getTechnologyStringListMatch(page.getContentLines(), htmlTemplate);
            long duration  = System.currentTimeMillis() - startTimestamp;
            if (match) return new TechnologyMatch(this, TechnologyMatch.HTML, duration);
        }

        long duration = System.currentTimeMillis() - startTimestamp;
        return TechnologyMatch.notMatched(this, duration);
    }

    // TODO : check performance of filter + findFirst
    private boolean getTechnologyStringListMatch(List<String> lines, Pattern pattern) {
        return lines.stream().parallel().anyMatch(line -> pattern.matcher(line).find());
    }

    private boolean getTechnologyMapMatch(Map<String, List<Pattern>> templates, Map<String, List<String>> page) {
        for (String header : templates.keySet()) {
            List<Pattern> patterns = templates.get(header);
            for (Pattern pattern : patterns) {
                if (pattern.toString().isEmpty() && page.containsKey(header)) {
                    return true;
                } else {
                    List<String> headerValues = page.get(header);
                    if (headerValues != null && !headerValues.isEmpty()) {
                        for (String value : headerValues) {
                            Matcher matcher = pattern.matcher(value);
                            if (matcher.find()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private String prepareRegexp(String pattern) {
        String[] splittedPattern = pattern.split("\\\\;");
        return splittedPattern[0];
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
}
