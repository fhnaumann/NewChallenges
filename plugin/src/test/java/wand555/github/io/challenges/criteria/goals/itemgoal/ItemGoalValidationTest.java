package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalValidationTest;
import wand555.github.io.challenges.validation.ItemGoalValidator;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ItemGoalValidationTest {
    private static ObjectMapper objectMapper;

    private InputStream jsonSchemaStream;
    private InputStream schematronStream;
    private DataSourceContext dataSourceContext;
    @BeforeAll
    public static void setUpIOData() {
        objectMapper = new ObjectMapper();

    }

    @BeforeEach
    public void setUp() {
        jsonSchemaStream = ItemGoalValidationTest.class.getResourceAsStream("/validation/challenges_schema.json");
        schematronStream = ItemGoalValidationTest.class.getResourceAsStream("/validation/constraints.sch");
        dataSourceContext = new DataSourceContext.Builder().withMaterialJSONList(Validation.class.getResourceAsStream("/materials.json")).build();
    }

    @Test
    public void testValidCode() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(ItemGoalValidationTest.class.getResource("/wand555/github/io/challenges/criteria/goals/itemgoal/valid_item_code_item_goal.json").toURI()));
        ValidationResult result = new ItemGoalValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        assertTrue(result.isValid());
    }

    @Test
    public void testInvalidCode() throws URISyntaxException, IOException {
        String json = Files.readString(Paths.get(ItemGoalValidationTest.class.getResource("/wand555/github/io/challenges/criteria/goals/itemgoal/invalid_item_code_item_goal.json").toURI()));
        ValidationResult result = new ItemGoalValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
    }

    @Test
    public void testCompleteValidation() {
        String json = """
                {
                  "goals": {
                    "itemGoal": {
                      "items": [
                        {
                          "collectableData": {
                            "amountNeeded": 100,
                            "currentAmount": 0
                          },
                          "collectableName": "stone"
                        }
                      ]
                    }
                  }
                }
                """;
        ValidationResult result = Validation.modernValidate(json, jsonSchemaStream, schematronStream, dataSourceContext);
        assertTrue(result.isValid());
    }
}
