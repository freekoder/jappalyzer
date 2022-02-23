package com.vampbear.jappalyzer;

public class TechnologyMatch {

    private final Technology technology;
    private long duration;
    private String reason;

    public TechnologyMatch(Technology technology, long duration) {
        this.technology = technology;
        this.duration = duration;
    }

    public Technology getTechnology() {
        return technology;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "TechnologyMatch{" +
                "technology=" + technology.getName() +
                ", duration=" + duration + "ms" +
                '}';
    }
}
