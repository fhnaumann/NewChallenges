package wand555.github.io.challenges.validation;

import org.bukkit.Keyed;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;

import java.util.List;
import java.util.function.Predicate;

public class CodeValidator<T extends DataSourceJSON<K>, K extends Keyed> extends ModelValidator {

    private final List<T> dataSource;
    private final Predicate<T> additionalCodeConstraints;
    private final List<String> codes;
    private final String pathToCollectables;

    public CodeValidator(List<T> dataSource, Predicate<T> additionalCodeConstraints, List<String> codes, String pathToCollectables) {
        this.dataSource = dataSource;
        this.additionalCodeConstraints = additionalCodeConstraints;
        this.codes = codes;
        this.pathToCollectables = pathToCollectables;
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema) {
        return performCodeValidation(builder, challengesSchema);
    }

    private ValidationResult.ValidationResultBuilder performCodeValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema) {
        List<String> illegalCodes = codes.stream()
                .filter(Predicate.not(this::hasValidCode))
                .toList();
        illegalCodes.forEach(s -> addCodeViolationToBuilder(builder, s));
        return builder;
    }

    private boolean hasValidCode(String code) {
        return codeExists(code) && additionalCodeConstraints.test(DataSourceJSON.fromCode(dataSource, code));
    }

    private boolean codeExists(String code) {
        return dataSource.stream().anyMatch(eDataSourceJSON -> eDataSourceJSON.getCode().equals(code));
    }

    private void addCodeViolationToBuilder(ValidationResult.ValidationResultBuilder builder, String illegalCode) {
        builder.addViolation(new Violation(
                pathToCollectables,
                "'%s' is not a valid code for the data source '%s'.".formatted(illegalCode, dataSource.get(0).getClass().getSimpleName()),
                Violation.Level.ERROR
        ));
    }
}
