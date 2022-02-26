package com.vampbear.jappalyzer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Jappalyzer {

    private List<Technology> technologies = new LinkedList<>();

    public static void main(String[] args) {
        List<Technology> technologies = loadTechnologies();
        System.out.println("Count: " + technologies.size());
        try {
            Jappalyzer jappalyzer = Jappalyzer.latest();
            List<Technology> instanceTechnologies = jappalyzer.getTechnologies();
            System.out.println("Instance techs: " + instanceTechnologies.size());
            List<TechnologyMatch> foundTechs = jappalyzer.fromUrl("https://wordpress.com/");
            foundTechs.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Technology> getTechnologies() {
        return this.technologies;
    }

    public static Jappalyzer create() {
        Jappalyzer jappalyzer = new Jappalyzer();
        List<Technology> technologies = loadTechnologiesFromInternalResources();
        jappalyzer.setTechnologies(technologies);
        return jappalyzer;
    }

    private void setTechnologies(List<Technology> technologies) {
        this.technologies = new ArrayList<>(technologies);
    }

    private static List<Technology> loadTechnologiesFromInternalResources() {
        List<Technology> technologies = new LinkedList<>();
        String[] keys = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z", "_"};
        for (String key : keys) {
            String techFilename = String.format("technologies/%s.json", key);
            try {
                String fileContent = readFileContentFromResource(techFilename);
                technologies.addAll(readTechnologiesFromString(fileContent));
            } catch (IOException ignore) {}
        }
        return technologies;
    }

    private static String readFileContentFromResource(String techFilename) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = Jappalyzer.class.getClassLoader().getResourceAsStream(techFilename)) {
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            }
        }
        return sb.toString();
    }

    private static Jappalyzer latest() {
        Jappalyzer jappalyzer = new Jappalyzer();
        // TODO: move technlogies loading from web to separate function
        jappalyzer.updateTechnologies();
        return jappalyzer;
    }

    private void updateTechnologies() {
        HttpClient httpClient = new HttpClient();
        String[] keys = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z", "_"};
        try {
            for (String key : keys) {
                String techGithubUrl = String.format("https://raw.githubusercontent.com/AliasIO/wappalyzer/master/src/technologies/%s.json", key);
                PageResponse pageResponse = httpClient.getPageByUrl(techGithubUrl);
                this.technologies.addAll(readTechnologiesFromString(pageResponse.getOrigContent()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TechnologyMatch> fromFile(String path) {
        String fileContent = readFileContent(path);
        PageResponse pageResponse = new PageResponse(fileContent);
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

    public List<TechnologyMatch> fromUrl(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        PageResponse pageResponse = httpClient.getPageByUrl(url);
        System.out.println("Page loaded");
        System.out.println("Page size " + pageResponse.getOrigContent().length());
        return getTechnologyMatches(pageResponse);
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

    private static List<Technology> loadTechnologies() {
        try (Stream<Path> filesStream = Files.list(Paths.get("src/main/resources/technologies"))) {
            return filesStream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Jappalyzer::readTechnologiesFromFile)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private static List<Technology> readTechnologiesFromFile(Path file) {
        List<Technology> technologies = new LinkedList<>();
        try {
            byte[] content = Files.readAllBytes(file);
            String technologyString = new String(content);
            return readTechnologiesFromString(technologyString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return technologies;
    }

    private static List<Technology> readTechnologiesFromString(String technologiesString) {
        List<Technology> technologies = new LinkedList<>();
        JSONObject fileJSON = new JSONObject(technologiesString);
        for (String key : fileJSON.keySet()) {
            JSONObject object = (JSONObject) fileJSON.get(key);
            try {
                technologies.add(new Technology(key, object));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return technologies;
    }
}
