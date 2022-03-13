package com.vampbear.jappalyzer;


import java.util.*;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import org.json.JSONException;
import java.io.InputStreamReader;

public class DataLoader {

    public static final String TECHNOLOGIES_GIT_PATH_TEMPLATE =
            "https://raw.githubusercontent.com/AliasIO/wappalyzer/master/src/technologies/%s.json";

    public static final String CATEGORIES_GIT_PATH =
            "https://raw.githubusercontent.com/AliasIO/wappalyzer/master/src/categories.json";

    public static final String GROUPS_GIT_PATH = "" +
            "https://raw.githubusercontent.com/AliasIO/wappalyzer/master/src/groups.json";

    public List<Technology> loadInternalTechnologies() {
        Map<Integer, Group> idGroupMap = readInternalGroups();
        Categories categories = readInternalCategories(idGroupMap);
        return readTechnologiesFromInternalResources(categories);
    }

    private Map<Integer, Group> readInternalGroups() {
        try {
            String groupsContent = readFileContentFromResource("groups.json");
            return createGroupsMap(new JSONObject(groupsContent));
        } catch (IOException | JSONException ignore) {}
        return Collections.emptyMap();
    }

    public List<Technology> loadLatestTechnologies() {
        Map<Integer, Group> idGroupMap = readLatestGroups();
        Categories categories = readLatestCategories(idGroupMap);
        return readTechnologiestFromGit(categories);
    }

    private Map<Integer, Group> readLatestGroups() {
        HttpClient httpClient = new HttpClient();
        try {
            PageResponse pageResponse = httpClient.getPageByUrl(GROUPS_GIT_PATH);
            JSONObject groupsContent = new JSONObject(pageResponse.getOrigContent());
            return createGroupsMap(new JSONObject(groupsContent));
        } catch (IOException | JSONException ignore) {}
        return Collections.emptyMap();
    }

    private Map<Integer, Group> createGroupsMap(JSONObject groupsJSON) {
        Map<Integer, Group> idGroupMap = new HashMap<>();
        for (String key : groupsJSON.keySet()) {
            JSONObject groupObject = groupsJSON.getJSONObject(key);
            int id = Integer.parseInt(key);
            idGroupMap.put(id, new Group(id, groupObject.getString("name")));
        }
        return idGroupMap;
    }

    private Categories readLatestCategories(Map<Integer, Group> idGroupMap) {
        List<Category> categories = new ArrayList<>();
        HttpClient httpClient = new HttpClient();
        try {
            PageResponse pageResponse = httpClient.getPageByUrl(CATEGORIES_GIT_PATH);
            JSONObject categoriesJSON = new JSONObject(pageResponse.getOrigContent());
            for (String key : categoriesJSON.keySet()) {
                JSONObject categoryJSON = categoriesJSON.getJSONObject(key);
                categories.add(extractCategory(categoryJSON, key, idGroupMap));
            }
        } catch (IOException | JSONException ignore) {}
        return new Categories(categories);
    }

    private Category extractCategory(JSONObject categoryJSON, String key, Map<Integer, Group> idGroupMap) {
        List<Integer> groupsIds = readGroupIds(categoryJSON);
        List<Group> groups = convertIdsToGroups(idGroupMap, groupsIds);
        Category category = new Category(
                Integer.parseInt(key), categoryJSON.getString("name"), categoryJSON.getInt("priority")
        );
        category.setGroups(groups);
        return category;
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

    private Categories readInternalCategories(Map<Integer, Group> idGroupMap) {
        List<Category> categories = new ArrayList<>();
        try {
            String categoriesContent = readFileContentFromResource("categories.json");
            JSONObject categoriesJSON = new JSONObject(categoriesContent);
            for (String key : categoriesJSON.keySet()) {
                JSONObject categoryJSON = categoriesJSON.getJSONObject(key);
                categories.add(extractCategory(categoryJSON, key, idGroupMap));
            }
        } catch (IOException | JSONException ignore) {}
        return new Categories(categories);
    }

    private List<Group> convertIdsToGroups(Map<Integer, Group> idGroupMap, List<Integer> groupsIds) {
        ArrayList<Group> groups = new ArrayList<>();
        for (Integer id : groupsIds) {
            if (idGroupMap.containsKey(id)) {
                groups.add(idGroupMap.get(id));
            }
        }
        return groups;
    }

    private List<Integer> readGroupIds(JSONObject categoryObject) {
        ArrayList<Integer> groupsIds = new ArrayList<>();
        if (categoryObject.has("groups")) {
            for (int i = 0; i < categoryObject.getJSONArray("groups").length(); i++) {
                int id = categoryObject.getJSONArray("groups").getInt(i);
                groupsIds.add(id);
            }
        }
        return groupsIds;
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
