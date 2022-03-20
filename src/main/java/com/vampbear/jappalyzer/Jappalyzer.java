package com.vampbear.jappalyzer;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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

    public static Jappalyzer latest() {
        DataLoader dataLoader = new DataLoader();
        List<Technology> technologies = dataLoader.loadLatestTechnologies();
        Jappalyzer jappalyzer = new Jappalyzer();
        jappalyzer.setTechnologies(technologies);
        return jappalyzer;
    }

    public Set<TechnologyMatch> fromFile(String path) {
        String fileContent = readFileContent(path);
        PageResponse pageResponse = new PageResponse(fileContent);
        return getTechnologyMatches(pageResponse);
    }

    public Set<TechnologyMatch> fromString(String content) {
        PageResponse pageResponse = new PageResponse(200, null, content);
        return getTechnologyMatches(pageResponse);
    }

    public Set<TechnologyMatch> fromPageResponse(PageResponse pageResponse) {
        return getTechnologyMatches(pageResponse);
    }

    public void addTechnology(Technology technology) {
        this.technologies.add(technology);
    }

    private void setTechnologies(List<Technology> technologies) {
        this.technologies = new ArrayList<>(technologies);
    }

    public Set<TechnologyMatch> fromUrl(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        PageResponse pageResponse = httpClient.getPageByUrl(url);
        return getTechnologyMatches(pageResponse);
    }

    private Set<TechnologyMatch> getTechnologyMatches(PageResponse pageResponse) {
        Set<TechnologyMatch> matchesSet = technologies.stream().parallel()
                .map(technology -> technology.applicableTo(pageResponse))
                .filter(TechnologyMatch::isMatched).collect(Collectors.toSet());
        enrichMatchesWithImpliedTechnologies(matchesSet);
        return matchesSet;
    }

    private void enrichMatchesWithImpliedTechnologies(Set<TechnologyMatch> matchesSet) {
        int currentMatchesSize;
        do {
            currentMatchesSize = matchesSet.size();
            List<TechnologyMatch> impliedMatches = new ArrayList<>();
            for (TechnologyMatch match : matchesSet) {
                for (String implyName : match.getTechnology().getImplies()) {
                    getTechnologyByName(implyName).ifPresent(technology -> {
                        TechnologyMatch impliedMatch = new TechnologyMatch(technology, TechnologyMatch.IMPLIED);
                        impliedMatches.add(impliedMatch);
                    });
                }
            }
            matchesSet.addAll(impliedMatches);
        } while (matchesSet.size() != currentMatchesSize);
    }

    private Optional<Technology> getTechnologyByName(String name) {
        return this.technologies.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
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
}
