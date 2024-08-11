package wand555.github.io.challenges.criteria.goals.deathgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Violation;
import wand555.github.io.challenges.validation.goals.DeathGoalValidator;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class DeathGoalValidationTest {

    private ObjectMapper objectMapper;
    private InputStream jsonSchemaStream;
    private InputStream schematronSchemaStream;
    private DataSourceContext dataSourceContext;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        jsonSchemaStream = DeathGoalValidationTest.class.getResourceAsStream("/challenges_schema.json");
        schematronSchemaStream = DeathGoalValidationTest.class.getResourceAsStream("/constraints.sch");
        dataSourceContext = new DataSourceContext.Builder()
                .withMaterialJSONList(DeathGoalValidationTest.class.getResourceAsStream("/materials.json"))
                .withEntityTypeJSONList(DeathGoalValidationTest.class.getResourceAsStream("/entity_types.json"))
                .withDeathMessageList(DeathGoalValidationTest.class.getResourceAsStream(
                        "/death_messages_as_data_source_JSON.json"))
                .build();
    }

    @Test
    public void testDeathGoalValidatorValidDeathMessageCode() throws IOException {
        Object objJSON = objectMapper.readValue(DeathGoalValidationTest.class.getResourceAsStream(
                "multiple_death_types_death_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = new DeathGoalValidator(dataSourceContext.deathMessageList(),
                                                         -1
        ).validate(new ValidationResult.ValidationResultBuilder(),
                   json, progress -> {}
        );
        assertTrue(result.isValid());
    }

    @Test
    public void testDeathGoalValidatorInvalidDeathMessageCode() throws IOException {
        Object objJSON = objectMapper.readValue(DeathGoalValidationTest.class.getResourceAsStream(
                "invalid_death_types_death_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        DeathGoalValidator deathGoalValidator = new DeathGoalValidator(dataSourceContext.deathMessageList(), -1);
        ValidationResult result = deathGoalValidator.validate(new ValidationResult.ValidationResultBuilder(), json, progress -> {});
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation violation = result.getViolations().get(0);
        assertEquals("goals/deathGoal/deathMessages", violation.getWhere());
        assertEquals(Violation.Level.ERROR, violation.getLevel());
    }

    @Test
    public void testFullValidDeathMessageCode() throws IOException {
        Object objJSON = objectMapper.readValue(DeathGoalValidationTest.class.getResourceAsStream(
                "multiple_death_types_death_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = Validation.modernValidate(json,
                                                            jsonSchemaStream,
                                                            schematronSchemaStream,
                                                            dataSourceContext
        );
        assertTrue(result.isValid());
    }

    @Test
    public void testFullInvalidDeathMessageCode() throws IOException {
        Object objJSON = objectMapper.readValue(DeathGoalValidationTest.class.getResourceAsStream(
                "invalid_death_types_death_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = Validation.modernValidate(json,
                                                            jsonSchemaStream,
                                                            schematronSchemaStream,
                                                            dataSourceContext
        );
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
    }

    @Test
    public void testAllDeathMessageCode() throws IOException {
        Object objJSON = objectMapper.readValue(DeathGoalValidationTest.class.getResourceAsStream(
                "all_death_types_death_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = Validation.modernValidate(json,
                                                            jsonSchemaStream,
                                                            schematronSchemaStream,
                                                            dataSourceContext
        );
        assertTrue(result.isValid(), "ValidationResult: %s".formatted(result));
    }
}
