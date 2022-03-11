package com.vampbear.jappalyzer;

import java.util.Objects;

public class TechnologyMatch {

    public static final String HEADER = "header";
    public static final String COOKIE = "cookie";
    public static final String META = "meta";
    public static final String DOM = "dom";
    public static final String SCRIPT = "script";
    public static final String HTML = "html";

    private final Technology technology;
    private final long duration;
    private final String reason;
    private final boolean matched;


    public TechnologyMatch(Technology technology, String reason) {
        this(technology, reason, 0L);
    }

    public TechnologyMatch(Technology technology, String reason, long duration) {
        this.technology = technology;
        this.reason = reason;
        this.duration = duration;
        this.matched = true;
    }

    public TechnologyMatch(long duration) {
        this.matched = false;
        this.duration = duration;
        this.technology = new Technology("null");
        this.reason = "";
    }

    public static TechnologyMatch notMatched(long duration) {
        return new TechnologyMatch(duration);
    }

    public boolean isMatched() {
        return matched;
    }

    public Technology getTechnology() {
        return technology;
    }

    public long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechnologyMatch match = (TechnologyMatch) o;
        return matched == match.matched && Objects.equals(technology, match.technology) && Objects.equals(reason, match.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(technology, reason, matched);
    }

    @Override
    public String toString() {
        return "TechnologyMatch{" +
                "technology=" + technology.getName() +
                ", reason=" + reason +
                ", duration=" + duration + "ms" +
                ", categories=" + technology.getCategories() +
                '}';
    }
}
