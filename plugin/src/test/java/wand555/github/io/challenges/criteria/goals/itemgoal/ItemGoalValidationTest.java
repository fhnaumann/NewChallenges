package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ValidationUtil;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.validation.goals.ItemGoalValidator;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Violation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class ItemGoalValidationTest {

    private static ObjectMapper objectMapper;
    private InputStream jsonSchemaStream;
    private InputStream schematronStream;
    private DataSourceContext dataSourceContext;

    @BeforeAll
    public static void setUpOnce() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() throws IOException {
        jsonSchemaStream = CriteriaUtil.loadJSONSchemaStream();
        schematronStream = CriteriaUtil.loadSchematronStream();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
    }

    @ParameterizedTest
    @MethodSource("provideJSONs")
    public void testCodes(String json, ValidationResult expected) {
        ValidationResult isolated = new ItemGoalValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        ValidationResult full = Validation.modernValidate(json, CriteriaUtil.loadJSONSchemaStream(), CriteriaUtil.loadSchematronStream(), dataSourceContext);
        ValidationUtil.assertResult(expected, isolated);
        ValidationUtil.assertResult(expected, full);
    }

    public static Stream<Arguments> provideJSONs() throws IOException {
        return Stream.of(
                Arguments.of(loadMultipleItems(), new ValidationResult()),
                Arguments.of(loadInvalidCodeItems(), createExpectedInvalidResult()),
                Arguments.of(loadInvalidBlockButNotItemCode(), createExpectedInvalidResult())
        );
    }

    private static ValidationResult createExpectedInvalidResult() {
        return new ValidationResult(false, List.of(new Violation("goals/itemGoal/items", "", Violation.Level.ERROR)), null);
    }

    private static String loadInvalidCodeItems() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(ItemGoalValidationTest.class.getResourceAsStream("invalid_item_code_item_goal.json"), Object.class));
    }

    private static String loadInvalidBlockButNotItemCode() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(ItemGoalValidationTest.class.getResourceAsStream("invalid_block_but_not_item_code_item_goal.json"), Object.class));
    }


    private static String loadMultipleItems() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(ItemGoalValidationTest.class.getResourceAsStream("multiple_items_item_goal.json"), Object.class));
    }
}
