package com.vampbear.jappalyzer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Categories {

    private static final Map<Integer, Category> categoryMap = new HashMap<>();

    static {
        List<Category> categories = loadCategories();
        categoryMap.putAll(categories.stream().collect(Collectors.toMap(Category::getId, Function.identity())));
    }

    private Categories() {}

    public static Category getCategoryById(int id) {
        return categoryMap.get(id);
    }

    private static List<Category> loadCategories() {
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
        return categories;
    }

    private static String readFileContentFromResource(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = Jappalyzer.class.getClassLoader().getResourceAsStream(filename)) {
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
}
