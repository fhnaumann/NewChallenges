package wand555.github.io.challenges.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ValidationTest {

    private static Validation validation;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        var tets = ValidationTest.class.getResource("/constraints.sch");
        System.out.println(tets);
        validation = new Validation(
                ValidationTest.class.getResourceAsStream("/challenges_schema.json"),
                //new File(ValidatorTest.class.getResource("/constraints.sch").getFile())
                ValidationTest.class.getResourceAsStream("/constraints.sch")
        );
    }

    @Test
    public void testBareBoneJSON() {
        String bareBoneJSON =
                """
                {
                  "rules": {
                    "enabledRules": {},
                    "enabledGlobalPunishments": {}
                  },
                  "goals": {}
                }
                """;
        ValidationResult result = validation.validate(bareBoneJSON);
        assertTrue(result.isValid());
    }

    @Test
    public void testBareBoneMissingJSON() {
        ValidationResult result;
        Violation expected;

        String bareBoneMissingEnabledRulesJSON =
                """
                {
                  "rules": {
                    "enabledGlobalPunishments": {}
                  },
                  "goals": {}
                }
                """;
        result = validation.validate(bareBoneMissingEnabledRulesJSON);
        assertTrue(result.isValid());

        String bareBoneMissingGoalsJSON =
                """
                {
                  "rules": {
                    "enabledRules": {},
                    "enabledGlobalPunishments": {}
                  }
                }
                """;
        result = validation.validate(bareBoneMissingGoalsJSON);
        assertTrue(result.isValid());
    }

    @Test
    public void testBareBoneMultipleMissingJSON() {
        String bareBoneMultipleMissingJSON =
                """
                {
                  "rules": {
                    "enabledRules": {},
                  },
                }
                """;
        ValidationResult result = validation.validate(bareBoneMultipleMissingJSON);
        assertTrue(result.isValid());
    }
}
