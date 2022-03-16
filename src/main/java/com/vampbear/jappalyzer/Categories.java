package com.vampbear.jappalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Categories {

    private final Map<Integer, Category> categoryMap = new HashMap<>();

    public Categories(List<Category> categories) {
        categoryMap.putAll(categories.stream().collect(Collectors.toMap(Category::getId, Function.identity())));
    }

    public Category getCategoryById(int id) {
        return categoryMap.get(id);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categoryMap.values());
    }
}
