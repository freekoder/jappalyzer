package com.vampbear.jappalyzer;

import com.vampbear.jappalyzer.utils.TestUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class JappalyzerTests {

    @Test
    public void wordpressComTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/wp.com.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("Google Font API", "WordPress", "PHP", "MySQL", "RSS", "Open Graph");
    }

    @Test
    public void baeldungTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/Baeldung.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames)
                .containsExactlyInAnyOrder("Google Analytics", "Google Font API", "jQuery Migrate",
                "jQuery", "PHP", "MySQL", "WordPress", "PWA", "Autoptimize", "Open Graph");
    }

    @Test
    public void queryMigrateMissTest() throws IOException {
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        String techDesc = TestUtils.readContentFromResource("technologies/jquery-migrate.json");
        Technology technology = technologyBuilder.fromString("jQuery Migrate", techDesc);
        Jappalyzer jappalyzer = Jappalyzer.empty();
        jappalyzer.addTechnology(technology);
        String htmlContent = TestUtils.readContentFromResource("contents/jquery_migrate.html");
        Set<TechnologyMatch> matches = jappalyzer.fromString(htmlContent);
        assertThat(matches.size()).isEqualTo(1);
    }

    @Test
    public void yandexTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/yandex.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("React", "jQuery", "Cart Functionality", "BEM", "Open Graph", "RSS");
    }

    @Test
    public void twitterTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/twitter.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("React", "PWA", "Open Graph");
    }

    @Test
    public void sportConrodTest() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/sport-conrod.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).containsExactlyInAnyOrder("jsDelivr", "Lightbox", "Polyfill", "Open Graph");
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

    @Test
    public void shouldReturnTechnologiesWithDirectImplies() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/wordpress.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).contains("PHP", "MySQL");
    }

    @Test
    public void shouldReturnTechnologiesWithTwoLevelImplies() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        PageResponse pageResponse = new PageResponse(200, null, "");
        pageResponse.addHeader("X-Powered-By", "WP Engine");
        Set<TechnologyMatch> matches = jappalyzer.fromPageResponse(pageResponse);
        List<String> techNames = getTechnologiesNames(matches);

        assertThat(techNames).contains("WordPress", "PHP", "MySQL");
        assertThat(getMatchByName("WordPress", matches).getReason()).isEqualTo(TechnologyMatch.IMPLIED);
        assertThat(getMatchByName("PHP", matches).getReason()).isEqualTo(TechnologyMatch.IMPLIED);
        assertThat(getMatchByName("MySQL", matches).getReason()).isEqualTo(TechnologyMatch.IMPLIED);
    }

    @Test
    public void shouldMatchMagento() {
        Jappalyzer jappalyzer = Jappalyzer.create();
        Set<TechnologyMatch> matches = jappalyzer.fromFile("src/test/resources/files/magento.html");
        List<String> techNames = getTechnologiesNames(matches);
        assertThat(techNames).contains("Magento", "PHP", "MySQL");
    }

    private TechnologyMatch getMatchByName(String name, Collection<TechnologyMatch> matches) {
        return matches.stream()
                .filter(item -> item.getTechnology().getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private List<String> getTechnologiesNames(Collection<TechnologyMatch> matches) {
        return matches.stream()
                .map(TechnologyMatch::getTechnology)
                .map(Technology::getName)
                .collect(Collectors.toList());
    }
}