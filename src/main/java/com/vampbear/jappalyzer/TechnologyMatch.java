package com.vampbear.jappalyzer;

public class TechnologyMatch {

    private final Technology technology;
    private long duration;
    private String reason;

    public TechnologyMatch(Technology technology) {
        this.technology = technology;
    }

    public Technology getTechnology() {
        return technology;
    }
}
