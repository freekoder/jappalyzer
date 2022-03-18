package com.vampbear.jappalyzer;

import com.vampbear.jappalyzer.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.*;

public class TechnologyTests {

    private TechnologyBuilder technologyBuilder;

    @Before
    public void setUp() {
        List<Category> categories = Arrays.asList(
                new Category(41, "Payment processors", 8),
                new Category(91, "Buy now pay later", 9),
                new Category(22, "TEST CATEGORY 1", 9),
                new Category(33, "TEST CATEGORY 2", 9)
        );
        this.technologyBuilder = new TechnologyBuilder(categories);
    }

    @Test
    public void shouldMatchHTMLTemplate() throws IOException {
        String pageContent = TestUtils.readContentFromResource("contents/font_awesome.html");
        Technology technology = new Technology("Font Awesome");
        technology.addHtmlTemplate("<link[^>]* href=[^>]+(?:([\\d.]+)/)?(?:css/)?font-awesome(?:\\.min)?\\.css\\;version:\\1");
        technology.addHtmlTemplate("<link[^>]* href=[^>]*kit\\-pro\\.fontawesome\\.com/releases/v([0-9.]+)/\\;version:\\1");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.HTML);
        assertThat(technology.applicableTo(pageContent)).isEqualTo(expected);
    }

    @Test
    public void emptyHeaderTest() {
        Technology technology = new Technology("test");
        technology.addHeaderTemplate("X-Flex-Lang", "");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Flex-Lang", Collections.singletonList("IT"));
        PageResponse pageResponse = new PageResponse(200, headers, "");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.HEADER);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void emptyHeaderPageLowerCaseTest() {
        Technology technology = new Technology("test");
        technology.addHeaderTemplate("X-Flex-Lang", "");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("x-flex-lang", Collections.singletonList("IT"));
        PageResponse pageResponse = new PageResponse(200, headers, "");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.HEADER);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void emptyHeaderTechnologyLowerCaseTest() {
        Technology technology = new Technology("test");
        technology.addHeaderTemplate("x-flex-lang", "");
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Flex-Lang", Collections.singletonList("IT"));
        PageResponse pageResponse = new PageResponse(200, headers, "");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.HEADER);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void emptyCookieTechnologyTest() {
        Technology technology = new Technology("test");
        technology.addCookieTemplate("forterToken", "");
        PageResponse pageResponse = new PageResponse(200, null, "");
        pageResponse.addCookie("forterToken", "");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.COOKIE);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void serverHeaderTest() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Server", Collections.singletonList("nginx"));
        PageResponse pageResponse = new PageResponse(200, headers, "");
        Technology technology = new Technology("Nginx");
        technology.addHeaderTemplate("Server", "nginx(?:/([\\d.]+))?\\;version:\\1");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.HEADER);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void cookieHeaderTest() {
        Technology technology = new Technology("Trbo");
        technology.addCookieTemplate("trbo_session", "^(?:[\\d]+)$");
        PageResponse pageResponse = new PageResponse(200, null, "");
        pageResponse.addCookie("trbo_session", "12312312");
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.COOKIE);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void scriptTest() throws IOException {
        Technology technology = new Technology("test");
        technology.addScriptSrc("livewire(?:\\.min)?\\.js");
        String htmlContent = TestUtils.readContentFromResource("contents/page_with_script.html");
        PageResponse pageResponse = new PageResponse(200, null, htmlContent);
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.SCRIPT);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void stringConstructorTest() throws IOException {
        String derakCloudContent = TestUtils.readContentFromResource("technologies/derak.json");
        Technology technology = this.technologyBuilder.fromString("DERAK.CLOUD", derakCloudContent);
        assertThat(technology.getName()).isEqualTo("DERAK.CLOUD");
        assertThat(technology.getDescription()).isEqualTo("Derak cloud service");
        assertThat(technology.getWebsite()).isEqualTo("https://derak.cloud");
        assertThat(technology.getIconName()).isEqualTo("DerakCloud.png");
        assertThat(technology.getHeaderTemplates("Derak-Umbrage").get(0).toString()).isEmpty();
        assertThat(technology.getHeaderTemplates("Server").get(0).toString()).isEqualTo("^DERAK\\.CLOUD$");
    }

    @Test
    public void shouldMatchWithMeta() throws IOException {
        String techDescription = TestUtils.readContentFromResource("technologies/joomla.json");
        Technology technology = this.technologyBuilder.fromString("Joomla", techDescription);
        String htmlContent = TestUtils.readContentFromResource("contents/joomla_meta.html");
        PageResponse pageResponse = new PageResponse(200, null, htmlContent);
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.META, 0L);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void shouldMatchMetaWithEmptyPattern() throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/jquery_pjax.json");
        Technology technology = this.technologyBuilder.fromString("JQuery pjax", techDesc);
        String htmlContent = TestUtils.readContentFromResource("contents/page_with_meta.html");
        PageResponse pageResponse = new PageResponse(200, null, htmlContent);
        TechnologyMatch expected = new TechnologyMatch(technology, TechnologyMatch.META, 0L);
        assertThat(technology.applicableTo(pageResponse)).isEqualTo(expected);
    }

    @Test
    public void shouldReadCPEFromTechDescription() throws IOException {
        String techDescription = TestUtils.readContentFromResource("technologies/joomla.json");
        Technology technology = this.technologyBuilder.fromString("Joomla", techDescription);
        assertThat(technology.getCPE()).isEqualTo("cpe:/a:joomla:joomla");
    }

    @Test
    public void shouldBeIncludedInTwoCategories() throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/pace.json");
        Technology technology = this.technologyBuilder.fromString("Pace", techDesc);
        assertThat(technology.getCategories()).containsExactlyInAnyOrder(
                new Category(41, "Payment processors", 8),
                new Category(91, "Buy now pay later", 9)
        );
    }

    @Test
    public void shouldContainsPricing() throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/jumio.json");
        Technology technology = this.technologyBuilder.fromString("Jumio", techDesc);
        assertThat(technology.getPricing()).containsExactlyElementsOf(Arrays.asList("payg", "mid", "recurring"));
    }

    @Test
    public void shouldContainsSaas() throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/jumio.json");
        Technology technology = this.technologyBuilder.fromString("Jumio", techDesc);
        assertThat(technology.isSaas()).isTrue();
    }

    @Test
    public void shouldTechnologyHasTwoMetaGenerators() throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/abicart.json");
        Technology technology = this.technologyBuilder.fromString("Abicart", techDesc);
        List<Pattern> generatorTemplates = technology.getMetaTemplates("generator");
        List<String> templateNames = generatorTemplates.stream().map(Pattern::toString).collect(Collectors.toList());
        assertThat(templateNames).containsExactlyInAnyOrder("Abicart", "Textalk Webshop");
    }
}