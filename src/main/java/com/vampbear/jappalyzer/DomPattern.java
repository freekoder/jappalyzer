package com.vampbear.jappalyzer;

import java.util.Map;
import java.util.Collections;
import java.util.Objects;

public class DomPattern {

    private final String selector;
    private final Map<String, String> attributes;

    public DomPattern(String selector) {
        this(selector, Collections.emptyMap());
    }

    public DomPattern(String selector, Map<String, String> attributes) {
        this.selector = selector;
        this.attributes = attributes;
    }

    public String getSelector() {
        return selector;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomPattern pattern = (DomPattern) o;
        return Objects.equals(selector, pattern.selector) && Objects.equals(attributes, pattern.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, attributes);
    }

    @Override
    public String toString() {
        return "DomPattern{" +
                "selector='" + selector + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
