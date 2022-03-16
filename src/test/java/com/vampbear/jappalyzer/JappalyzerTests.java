package com.vampbear.jappalyzer;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class JappalyzerTests {

    public static final String JQUERY_MIGRATE_TECH = "" +
            "{\n" +
            "    \"cats\": [\n" +
            "      59\n" +
            "    ],\n" +
            "    \"description\": \"Query Migrate is a javascript library that allows you to preserve the compatibility of your jQuery code developed for versions of jQuery older than 1.9.\",\n" +
            "    \"icon\": \"jQuery.svg\",\n" +
            "    \"implies\": \"jQuery\",\n" +
            "    \"js\": {\n" +
            "      \"jQuery.migrateVersion\": \"([\\\\d.]+)\\\\;version:\\\\1\",\n" +
            "      \"jQuery.migrateWarnings\": \"\",\n" +
            "      \"jqueryMigrate\": \"\"\n" +
            "    },\n" +
            "    \"scriptSrc\": \"jquery[.-]migrate(?:-([\\\\d.]+))?(?:\\\\.min)?\\\\.js(?:\\\\?ver=([\\\\d.]+))?\\\\;version:\\\\1?\\\\1:\\\\2\",\n" +
            "    \"website\": \"https://github.com/jquery/jquery-migrate\"\n" +
            "  }";

    public static final String JQUERY_MIGRATE_CONTENT = "" +
            "<html><head>\n" +
            "<script type='text/javascript' src='https://www.baeldung.com/wp-includes/js/jquery/jquery.min.js?ver=3.6.0' id='jquery-core-js'></script>\n" +
            "<script type='text/javascript' src='https://www.baeldung.com/wp-includes/js/jquery/jquery-migrate.min.js?ver=3.3.2' id='jquery-migrate-js'></script>\n" +
            "<script type='text/javascript' id='ba_big_menu_scripts-js-extra'>\n" +
            "</head><body></body></html>";

    public static final String ABICART_TECHNOLOGY = "{\n" +
            "    \"cats\": [\n" +
            "      6\n" +
            "    ],\n" +
            "    \"description\": \"Abicart is an ecommerce platform developed by the Swedish company Abicart AB.\",\n" +
            "    \"icon\": \"abicart.png\",\n" +
            "    \"meta\": {\n" +
            "      \"generator\": [\n" +
            "        \"Abicart\",\n" +
            "        \"Textalk Webshop\"\n" +
            "      ]\n" +
            "    }" +
            "}";


    public static final String ABICART_CONTENT = "" +
            "<html><head>\n" +
            "<meta name=\"generator\" content=\"Abicart\"/>\n" +
            "</head><body></body></html>";

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
    public void queryMigrateMissTest() {
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        Jappalyzer jappalyzer = Jappalyzer.empty();
        Technology technology = technologyBuilder.fromString("jQuery Migrate", JQUERY_MIGRATE_TECH);
        jappalyzer.addTechnology(technology);
        List<TechnologyMatch> matches = jappalyzer.fromString(JQUERY_MIGRATE_CONTENT);
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
    public void shouldDetectAbicartTechnology() {
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        Technology technology = technologyBuilder.fromString("Abicart", ABICART_TECHNOLOGY);
        PageResponse pageResponse = new PageResponse(200, null, ABICART_CONTENT);
        TechnologyMatch match = new TechnologyMatch(technology, TechnologyMatch.META);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(match);
    }

    private List<String> getTechnologiesNames(List<TechnologyMatch> matches) {
        return matches.stream().map(TechnologyMatch::getTechnology).map(Technology::getName).collect(Collectors.toList());
    }
}