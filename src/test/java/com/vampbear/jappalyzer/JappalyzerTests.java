package com.vampbear.jappalyzer;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JappalyzerTests {

    @Test
    public void wordpressComTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<Technology> technologies = jappalyzer.fromFile("src/test/resources/files/wp.com.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Google Font API", "WordPress"), techNames);
    }

    @Test
    public void baeldungTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<Technology> technologies = jappalyzer.fromFile("src/test/resources/files/Baeldung.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Google Analytics", "Google Font API", "jQuery Migrate", "jQuery", "Elementor", "WordPress"), techNames);
    }

    @Test
    public void yandexTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<Technology> technologies = jappalyzer.fromFile("src/test/resources/files/yandex.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("React", "jQuery", "Cart Functionality", "BEM"), techNames);
    }

    @Test
    public void twitterTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<Technology> technologies = jappalyzer.fromFile("src/test/resources/files/twitter.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Collections.emptyList(), techNames);
    }
}