package wand555.github.io.validation;

import com.helger.schematron.pure.SchematronResourcePure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validator;
import wand555.github.io.challenges.validation.Violation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        var tets = ValidatorTest.class.getResource("/constraints.sch");
        System.out.println(tets);
        validator = new Validator(
                ValidatorTest.class.getResourceAsStream("/challenges_schema.json"),
                //new File(ValidatorTest.class.getResource("/constraints.sch").getFile())
                ValidatorTest.class.getResourceAsStream("/constraints.sch")
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
        ValidationResult result = validator.validate(bareBoneJSON);
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
        result = validator.validate(bareBoneMissingEnabledRulesJSON);
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
        result = validator.validate(bareBoneMissingGoalsJSON);
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
        ValidationResult result = validator.validate(bareBoneMultipleMissingJSON);
        assertTrue(result.isValid());
    }
}
