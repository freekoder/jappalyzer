package com.vampbear.jappalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DomPatternTest {

    @Test
    public void shouldBeApplicableToSelectorWithAttributes() {
        Map<String, String> attributes = Collections.singletonMap("src", "www\\.resengo\\.\\w+");
        DomPattern pattern = new DomPattern("iframe[src*='resengo']", attributes);
        Document document = Jsoup.parse(
                "<html><body><iframe src='https://www.resengo.com/iframe'/></body></html>");
        assertThat(pattern.applicableToDocument(document)).isTrue();
    }

}