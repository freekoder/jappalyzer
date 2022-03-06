package com.vampbear.jappalyzer;

import java.util.HashMap;
import java.util.Map;

public class Categories {

    private static final Map<Integer, Category> categoryMap = new HashMap<>();

    static {
        categoryMap.put(41, new Category(41, "Payment processors", 8));
        categoryMap.put(91, new Category(91, "Buy now pay later", 9));
    }

    private Categories() {}

    public static Category getCategoryById(int id) {
        return categoryMap.get(id);
    }
}
