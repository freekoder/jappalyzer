package com.vampbear.jappalyzer;

import com.vampbear.jappalyzer.utils.TestUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class JappalyzerTests {

    @Test
    public void wordpressComTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/wp.com.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("Google Font API", "WordPress");
    }

    @Test
    public void baeldungTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/Baeldung.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("Google Analytics",
                "Google Font API",
                "jQuery Migrate",
                "jQuery",
                "Elementor",
                "WordPress");
    }

    @Test
    public void queryMigrateMissTest() throws IOException {
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        String techDesc = TestUtils.readContentFromResource("technologies/jquery-migrate.json");
        Technology technology = technologyBuilder.fromString("jQuery Migrate", techDesc);
        Jappalyzer jappalyzer = Jappalyzer.empty();
        jappalyzer.addTechnology(technology);
        String htmlContent = TestUtils.readContentFromResource("contents/jquery_migrate.html");
        List<TechnologyMatch> matches = jappalyzer.fromString(htmlContent);
        assertThat(matches.size()).isEqualTo(1);
    }

    @Test
    public void yandexTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/yandex.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("React", "jQuery", "Cart Functionality", "BEM");
    }

    @Test
    public void twitterTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/twitter.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).isEmpty();
    }

    @Test
    public void sportConrodTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        List<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/sport-conrod.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("jsDelivr", "Lightbox", "Polyfill");
    }

    @Test
    public void shouldDetectAbicartTechnology() throws IOException {
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        String techDesc = TestUtils.readContentFromResource("technologies/abicart.json");
        Technology technology = technologyBuilder.fromString("Abicart", techDesc);
        String htmlContent = TestUtils.readContentFromResource("contents/abicart_meta.html");
        PageResponse pageResponse = new PageResponse(200, null, htmlContent);
        TechnologyMatch match = new TechnologyMatch(technology, TechnologyMatch.META);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(match);
    }

    private List<String> getTechnologiesNames(List<TechnologyMatch> matches) {
        return matches.stream().map(TechnologyMatch::getTechnology).map(Technology::getName).collect(Collectors.toList());
    }
}