package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalValidationTest;
import wand555.github.io.challenges.validation.ItemGoalValidator;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validator;
import wand555.github.io.challenges.validation.Violation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemGoalValidationTest {

    private ObjectMapper objectMapper;
    private InputStream jsonSchemaStream;
    private InputStream schematronStream;
    private DataSourceContext dataSourceContext;

    @BeforeEach
    public void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        jsonSchemaStream = CriteriaUtil.loadJSONSchemaStream();
        schematronStream = CriteriaUtil.loadSchematronStream();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
    }

    @Test
    public void testItemGoalValidatorValidItemCode() throws IOException {
        String json = loadMultipleItems();
        ValidationResult result = new ItemGoalValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        assertTrue(result.isValid());
    }

    @Test
    public void testItemGoalValidatorInvalidItemCode() throws IOException {
        String json = loadInvalidCodeItems();
        ValidationResult result = new ItemGoalValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        assertInvalidResult(result);
    }

    @Test
    public void testFullValidItemCode() throws IOException {
        String json = loadMultipleItems();
        ValidationResult result = Validation.modernValidate(json, CriteriaUtil.loadJSONSchemaStream(), CriteriaUtil.loadSchematronStream(), dataSourceContext);
        assertTrue(result.isValid());
    }

    @Test
    public void testFullInvalidItemCode() throws IOException {
        String json = loadInvalidCodeItems();
        ValidationResult result = Validation.modernValidate(json, CriteriaUtil.loadJSONSchemaStream(), CriteriaUtil.loadSchematronStream(), dataSourceContext);
        assertInvalidResult(result);
    }

    private void assertInvalidResult(ValidationResult result) {
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation violation = result.getViolations().get(0);
        assertEquals("goals/itemGoal/items", violation.getWhere());
        assertEquals(Violation.Level.ERROR, violation.getLevel());
    }

    private String loadInvalidCodeItems() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(ItemGoalValidationTest.class.getResourceAsStream("invalid_item_code_item_goal.json"), Object.class));
    }

    private String loadMultipleItems() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(ItemGoalValidationTest.class.getResourceAsStream("multiple_items_item_goal.json"), Object.class));
    }
}
