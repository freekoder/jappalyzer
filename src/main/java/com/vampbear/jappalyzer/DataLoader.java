package com.vampbear.jappalyzer;


import java.util.List;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.io.BufferedReader;
import org.json.JSONException;
import java.io.InputStreamReader;

public class DataLoader {

    public static final String TECHNOLOGIES_GIT_PATH_TEMPLATE =
            "https://raw.githubusercontent.com/AliasIO/wappalyzer/master/src/technologies/%s.json";

    public List<Technology> loadInternalTechnologies() {
        Categories categories = readInternalCategories();
        return readTechnologiesFromInternalResources(categories);
    }

    public List<Technology> loadLatestTechnologies() {
        Categories categories = readLatestCategories();
        return readTechnologiestFromGit(categories);
    }

    private Categories readLatestCategories() {
        return new Categories(Collections.emptyList());
    }

    private List<Technology> readTechnologiestFromGit(Categories categories) {
        ArrayList<Technology> technologies = new ArrayList<>();
        HttpClient httpClient = new HttpClient();
        String[] keys = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z", "_"};
        try {
            for (String key : keys) {
                String techGithubUrl = String.format(TECHNOLOGIES_GIT_PATH_TEMPLATE, key);
                PageResponse pageResponse = httpClient.getPageByUrl(techGithubUrl);
                technologies.addAll(
                        readTechnologiesFromString(pageResponse.getOrigContent(), categories)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return technologies;
    }

    private Categories readInternalCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            String categoriesContent = readFileContentFromResource("categories.json");
            JSONObject categoriesJSON = new JSONObject(categoriesContent);
            for (String key : categoriesJSON.keySet()) {
                JSONObject categoryObject = categoriesJSON.getJSONObject(key);
                categories.add(
                        new Category(
                                Integer.parseInt(key), categoryObject.getString("name"), categoryObject.getInt("priority")
                        )
                );
            }
        } catch (IOException | JSONException ignore) {}
        return new Categories(categories);
    }

    private List<Technology> readTechnologiesFromInternalResources(Categories categories) {
        List<Technology> technologies = new LinkedList<>();
        String[] keys = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r",
                "s", "t", "u", "v", "w", "x", "y", "z", "_"};
        for (String key : keys) {
            String techFilename = String.format("technologies/%s.json", key);
            try {
                String fileContent = readFileContentFromResource(techFilename);
                technologies.addAll(readTechnologiesFromString(fileContent, categories));
            } catch (IOException ignore) {}
        }
        return technologies;
    }

    private String readFileContentFromResource(String techFilename) throws IOException {
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

    private List<Technology> readTechnologiesFromString(String technologiesString, Categories categories) {
        List<Technology> technologies = new LinkedList<>();
        JSONObject fileJSON = new JSONObject(technologiesString);
        for (String key : fileJSON.keySet()) {
            JSONObject object = (JSONObject) fileJSON.get(key);
            try {
                technologies.add(new Technology(key, object, categories));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return technologies;
    }

}
