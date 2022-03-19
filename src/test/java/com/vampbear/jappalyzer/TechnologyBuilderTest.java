package com.vampbear.jappalyzer;

import com.vampbear.jappalyzer.utils.TestUtils;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

public class TechnologyBuilderTest {

    @Test
    public void shouldReturnEmptyImplies() throws IOException {
        Technology technology = getTechnologyFromFile("Abicart", "abicart.json");
        assertThat(technology.getImplies()).isEmpty();
    }

    @Test
    public void shouldReturnSingleImpliesValue() throws IOException {
        Technology technology = getTechnologyFromFile("Warp", "warp.json");
        assertThat(technology.getImplies()).containsExactlyInAnyOrder("Haskell");
    }

    @Test
    public void shouldReturnTwoImpliesValues() throws IOException {
        Technology technology = getTechnologyFromFile("Wordpress", "wordpress.json");
        assertThat(technology.getImplies()).containsExactlyInAnyOrder("PHP", "MySQL");
    }

    private Technology getTechnologyFromFile(String Abicart, String techFilename) throws IOException {
        String techDesc = TestUtils.readContentFromResource("technologies/" + techFilename);
        TechnologyBuilder technologyBuilder = new TechnologyBuilder();
        return technologyBuilder.fromString(Abicart, techDesc);
    }

}