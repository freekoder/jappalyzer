package com.vampbear.jappalyzer;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Jappalyzer {

    private List<Technology> technologies = new LinkedList<>();

    public static void main(String[] args) {
        try {
            Jappalyzer jappalyzer = Jappalyzer.create();
            List<Technology> instanceTechnologies = jappalyzer.getTechnologies();
            System.out.println("Instance techs: " + instanceTechnologies.size());
            List<TechnologyMatch> foundTechs = jappalyzer.fromUrl("https://www.flexcmp.com/dxp");
            foundTechs.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Jappalyzer empty() {
        return new Jappalyzer();
    }

    private List<Technology> getTechnologies() {
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
            long startTimestamp = System.currentTimeMillis();
            String reason = technology.appliebleTo(pageResponse);
            if (!reason.isEmpty()) {
                long endTimestamp = System.currentTimeMillis();
                matches.add(new TechnologyMatch(technology, reason, endTimestamp - startTimestamp));
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
