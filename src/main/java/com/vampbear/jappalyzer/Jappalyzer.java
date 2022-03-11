package com.vampbear.jappalyzer;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Jappalyzer {

    private List<Technology> technologies = new LinkedList<>();

    public static Jappalyzer empty() {
        return new Jappalyzer();
    }

    public List<Technology> getTechnologies() {
        return this.technologies;
    }

    public static Jappalyzer create() {
        DataLoader dataLoader = new DataLoader();
        List<Technology> technologies = dataLoader.loadInternalTechnologies();
        Jappalyzer jappalyzer = new Jappalyzer();
        jappalyzer.setTechnologies(technologies);
        return jappalyzer;
    }

    private static Jappalyzer latest() {
        DataLoader dataLoader = new DataLoader();
        List<Technology> technologies = dataLoader.loadLatestTechnologies();
        Jappalyzer jappalyzer = new Jappalyzer();
        jappalyzer.setTechnologies(technologies);
        return jappalyzer;
    }

    public void addTechnology(Technology technology) {
        this.technologies.add(technology);
    }

    private void setTechnologies(List<Technology> technologies) {
        this.technologies = new ArrayList<>(technologies);
    }

    public List<TechnologyMatch> fromFile(String path) {
        String fileContent = readFileContent(path);
        PageResponse pageResponse = new PageResponse(fileContent);
        return getTechnologyMatches(pageResponse);
    }

    public List<TechnologyMatch> fromUrl(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        PageResponse pageResponse = httpClient.getPageByUrl(url);
        return getTechnologyMatches(pageResponse);
    }

    private List<TechnologyMatch> getTechnologyMatches(PageResponse pageResponse) {
        List<TechnologyMatch> matches = new ArrayList<>();
        for (Technology technology : technologies) {
            TechnologyMatch match = technology.appliebleTo(pageResponse);
            if (match.isMatched()) {
                matches.add(match);
            }
        }
        return matches;
    }

    private static String readFileContent(String path) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public List<TechnologyMatch> fromString(String content) {
        PageResponse pageResponse = new PageResponse(200, null, content);
        return getTechnologyMatches(pageResponse);
    }
}
