package wand555.github.io.challenges.criteria.rules.blockbreakrule;

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
import wand555.github.io.challenges.validation.rules.BlockBreakRuleValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockBreakRuleValidationTest {

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
        //ValidationResult isolated = new BlockBreakRuleValidator(dataSourceContext.materialJSONList()).validate(new ValidationResult.ValidationResultBuilder(), json);
        ValidationResult full = Validation.modernValidate(json, CriteriaUtil.loadJSONSchemaStream(), CriteriaUtil.loadSchematronStream(), dataSourceContext);
        //ValidationUtil.assertResult(expected, isolated);
        ValidationUtil.assertResult(expected, full);
    }

    public static Stream<Arguments> provideJSONs() throws IOException {
        return Stream.of(
                Arguments.of(loadMultipleBlocks(), new ValidationResult()),
                Arguments.of(loadInvalidCodeBlocks(), createExpectedInvalidResult()),
                Arguments.of(loadInvalidItemButNotBlockCode(), createExpectedInvalidResult())
        );
    }

    private static ValidationResult createExpectedInvalidResult() {
        return new ValidationResult(false, List.of(new Violation("rules/enabledRules/noBlockBreakRule/exemptions", "", Violation.Level.ERROR)), null);
    }

    private static String loadInvalidCodeBlocks() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(BlockBreakRuleValidationTest.class.getResourceAsStream("invalid_block_code_block_break_rule.json"), Object.class));
    }

    private static String loadInvalidItemButNotBlockCode() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(BlockBreakRuleValidationTest.class.getResourceAsStream("invalid_item_but_not_block_code_block_break_rule.json"), Object.class));
    }

    private static String loadMultipleBlocks() throws IOException {
        return objectMapper.writeValueAsString(objectMapper.readValue(BlockBreakRuleValidationTest.class.getResourceAsStream("multiple_blocks_block_break_rule.json"), Object.class));
    }
}
