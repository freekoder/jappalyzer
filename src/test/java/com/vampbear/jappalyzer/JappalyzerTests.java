package com.vampbear.jappalyzer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JappalyzerTests {

    @Test
    public void wordpressComTest() {
        List<Technology> technologies = Jappalyzer.fromFile("src/test/resources/files/wp.com.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("WordPress", "test2"), techNames);
    }

    @Test
    public void baeldungTest() {
        List<Technology> technologies = Jappalyzer.fromFile("src/test/resources/files/Baeldung.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("WordPress", "test2"), techNames);
    }

    @Test
    public void yandexTest() {
        List<Technology> technologies = Jappalyzer.fromFile("src/test/resources/files/yandex.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("WordPress", "test2"), techNames);
    }

    @Test
    public void twitterTest() {
        List<Technology> technologies = Jappalyzer.fromFile("src/test/resources/files/twitter.html");
        List<String> techNames = technologies.stream().map(Technology::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("WordPress", "test2"), techNames);
    }
}