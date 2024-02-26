package wand555.github.io.challenges.criteria.goals.mobgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validation;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MobGoalValidationTest {

    private static Validation validation;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        validation = new Validation(MobGoalValidationTest.class.getResourceAsStream("/validation/challenges_schema.json"), MobGoalValidationTest.class.getResourceAsStream("/validation/constraints.sch"));
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testMobGoalValidator() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("multiple_mobs_mob_goal2.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = validation.validate(json);
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testInvalidMobCode() throws IOException {
        Object objJSON = objectMapper.readValue(MobGoalValidationTest.class.getResourceAsStream("multiple_mobs_mob_goal2.json"), Object.class);
        String json = objectMapper.writeValueAsString(objJSON);
        ValidationResult result = validation.validate(json);
        assertTrue(result.getViolations().isEmpty());
    }
}
