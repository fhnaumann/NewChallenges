package wand555.github.io.challenges.criteria.rules.nocraftingrule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ValidationUtil;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Violation;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoCraftingRuleValidationTest {

    private static ObjectMapper objectMapper;
    private DataSourceContext dataSourceContext;

    @BeforeAll
    public static void setUpOnce() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() throws IOException {
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.craftingTypeJSONList()).thenReturn(CriteriaUtil.loadCraftingTypes().getData());
    }

    @ParameterizedTest
    @MethodSource("provideJSONs")
    public void testCodes(String json, ValidationResult expected) {
        ValidationResult full = Validation.modernValidate(json,
                                                          CriteriaUtil.loadJSONSchemaStream(),
                                                          CriteriaUtil.loadSchematronStream(),
                                                          dataSourceContext
        );
        ValidationUtil.assertResult(expected, full);
    }

    public static Stream<Arguments> provideJSONs() throws IOException {
        return Stream.of(
                Arguments.of(loadMultipleRecipes(), new ValidationResult()),
                Arguments.of(loadInvalidCodeRecipes(), createExpectedInvalidResult())
        );
    }

    private static ValidationResult createExpectedInvalidResult() {
        return new ValidationResult(false,
                                    List.of(new Violation("rules/enabledRules/noCraftingRule/exemptions",
                                                          "",
                                                          Violation.Level.ERROR
                                    )),
                                    null
        );
    }

    private static String loadInvalidCodeRecipes() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(NoCraftingRuleValidationTest.class.getResourceAsStream(
                "invalid_recipe_code_no_crafting_rule.json"), Object.class));
    }

    private static String loadMultipleRecipes() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(NoCraftingRuleValidationTest.class.getResourceAsStream(
                "multiple_recipes_no_crafting_rule.json"), Object.class));
    }
}
