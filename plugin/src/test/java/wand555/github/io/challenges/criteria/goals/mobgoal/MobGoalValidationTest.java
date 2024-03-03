package wand555.github.io.challenges.criteria.goals.mobgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.validator.ValidatorResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.validation.MobGoalValidator;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.Validator;
import wand555.github.io.challenges.validation.Violation;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class MobGoalValidationTest {

    private ObjectMapper objectMapper;
    private InputStream jsonSchemaStream;
    private InputStream schematronSchemaStream;
    private DataSourceContext dataSourceContext;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        jsonSchemaStream = MobGoalValidationTest.class.getResourceAsStream("/challenges_schema.json");
        schematronSchemaStream = MobGoalValidationTest.class.getResourceAsStream("/constraints.sch");
        dataSourceContext = new DataSourceContext.Builder()
                .withMaterialJSONList(MobGoalValidationTest.class.getResourceAsStream("/materials.json"))
                .withEntityTypeJSONList(MobGoalValidationTest.class.getResourceAsStream("/entity_types.json"))
                .build();
    }

    @Test
    public void testMobGoalValidatorValidMobCode() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("multiple_mobs_mob_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = new MobGoalValidator(dataSourceContext.entityTypeJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        assertTrue(result.isValid());
    }

    @Test
    public void testMobGoalValidatorInvalidMobCode() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("invalid_mob_code_mob_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        MobGoalValidator mobGoalValidator = new MobGoalValidator(dataSourceContext.entityTypeJSONList());
        ValidationResult result = mobGoalValidator.validate(new ValidationResult.ValidationResultBuilder(), json);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation violation = result.getViolations().get(0);
        assertEquals("goals/mobGoal/mobs", violation.getWhere());
        assertEquals(Violation.Level.ERROR, violation.getLevel());
    }

    @Test
    public void testFullValidMobCode() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("multiple_mobs_mob_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = Validation.modernValidate(json, jsonSchemaStream, schematronSchemaStream, dataSourceContext);
        assertTrue(result.isValid());
    }

    @Test
    public void testFullInvalidMobCode() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("invalid_mob_code_mob_goal.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = Validation.modernValidate(json, jsonSchemaStream, schematronSchemaStream, dataSourceContext);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
    }
}
