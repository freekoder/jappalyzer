package com.vampbear.jappalyzer;

import com.vampbear.jappalyzer.utils.TestUtils;
import org.junit.Test;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class TechnologyBuilderTest {

    @Test
    public void shouldContainsBasicFields() throws IOException {
        Technology technology = buildTechnologyFromFile("DERAK.CLOUD", "derak.json");
        assertThat(technology.getName()).isEqualTo("DERAK.CLOUD");
        assertThat(technology.getDescription()).isEqualTo("Derak cloud service");
        assertThat(technology.getWebsite()).isEqualTo("https://derak.cloud");
        assertThat(technology.getIconName()).isEqualTo("DerakCloud.png");
        assertThat(technology.getHeaderTemplates("Derak-Umbrage").get(0).toString()).isEmpty();
        assertThat(technology.getHeaderTemplates("Server").get(0).toString()).isEqualTo("^DERAK\\.CLOUD$");
    }

    @Test
    public void shouldReadCPEFromTechDescription() throws IOException {
        Technology technology = buildTechnologyFromFile("Joomla", "joomla.json");
        assertThat(technology.getCPE()).isEqualTo("cpe:/a:joomla:joomla");
    }

    @Test
    public void shouldBeIncludedInTwoCategories() throws IOException {
        Technology technology = buildTechnologyFromFile("Pace", "pace.json");
        assertThat(technology.getCategories()).containsExactlyInAnyOrder(
                new Category(41, "Payment processors", 8),
                new Category(91, "Buy now pay later", 9)
        );
    }

    @Test
    public void shouldTechnologyHasTwoMetaGenerators() throws IOException {
        Technology technology = buildTechnologyFromFile("Abicart", "abicart.json");
        List<Pattern> generatorTemplates = technology.getMetaTemplates("generator");
        List<String> templateNames = generatorTemplates.stream().map(Pattern::toString).collect(Collectors.toList());
        assertThat(templateNames).containsExactlyInAnyOrder("Abicart", "Textalk Webshop");
    }

    @Test
    public void shouldContainsSaas() throws IOException {
        Technology technology = buildTechnologyFromFile("Jumio", "jumio.json");
        assertThat(technology.isSaas()).isTrue();
    }

    @Test
    public void shouldContainsPricing() throws IOException {
        Technology technology = buildTechnologyFromFile("Jumio", "jumio.json");
        assertThat(technology.getPricing()).containsExactlyElementsOf(Arrays.asList("payg", "mid", "recurring"));
    }

    @Test
    public void shouldReturnEmptyImplies() throws IOException {
        Technology technology = buildTechnologyFromFile("Abicart", "abicart.json");
        assertThat(technology.getImplies()).isEmpty();
    }

    @Test
    public void shouldReturnSingleImpliesValue() throws IOException {
        Technology technology = buildTechnologyFromFile("Warp", "warp.json");
        assertThat(technology.getImplies()).containsExactlyInAnyOrder("Haskell");
    }

    @Test
    public void shouldReturnTwoImpliesValues() throws IOException {
        Technology technology = buildTechnologyFromFile("Wordpress", "wordpress.json");
        assertThat(technology.getImplies()).containsExactlyInAnyOrder("PHP", "MySQL");
    }

    @Test
    public void shouldContainsTwoDOMPatterns() throws IOException {
        Technology technology = buildTechnologyFromFile("Magento", "magento.json");
        assertThat(technology.getDomTemplates().size()).isEqualTo(2);
        assertThat(technology.getDomTemplates().get(0))
                .isEqualTo("script[data-requiremodule*='mage'], script[data-requiremodule*='Magento_'], html[data-image-optimizing-origin]");
        assertThat(technology.getDomTemplates().get(1))
                .isEqualTo("script[type='text/x-magento-init']");
    }

    @Test
    public void shouldContainsOneDOMPattern() throws IOException {
        Technology technology = buildTechnologyFromFile("Jetpack", "jetpack.json");
        assertThat(technology.getDomTemplates().size()).isEqualTo(1);
        assertThat(technology.getDomTemplates().get(0)).isEqualTo("link[href*='/wp-content/plugins/jetpack/']");
    }

    private Technology buildTechnologyFromFile(String Abicart, String techFilename) throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/" + techFilename);
        List<Category> categories = Arrays.asList(
                new Category(41, "Payment processors", 8),
                new Category(91, "Buy now pay later", 9),
                new Category(22, "TEST CATEGORY 1", 9),
                new Category(33, "TEST CATEGORY 2", 9)
        );
        TechnologyBuilder technologyBuilder = new TechnologyBuilder(categories);
        return technologyBuilder.fromString(Abicart, techDesc);
    }

}