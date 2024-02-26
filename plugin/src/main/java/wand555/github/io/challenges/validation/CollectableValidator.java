package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;

import java.util.List;
import java.util.function.Predicate;

public abstract class CollectableValidator<T extends DataSourceJSON<E>, E extends Enum<E>> extends ModelValidator {

    private final List<T> dataSource;

    public CollectableValidator(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema challengesSchema) {
        return performCodeValidation(builder, challengesSchema);
    }

    protected ValidationResult.ValidationResultBuilder performCodeValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema challengesSchema) {
        List<String> illegalCodes = getCollectables(challengesSchema).stream()
                .filter(Predicate.not(this::hasValidCode))
                .map(CollectableEntryConfig::getCollectableName)
                .toList();
        illegalCodes.forEach(s -> addCodeViolationToBuilder(builder, s));
        return builder;
    }

    private boolean hasValidCode(CollectableEntryConfig collectable) {
        return dataSource.stream().anyMatch(eDataSourceJSON -> eDataSourceJSON.getCode().equals(collectable.getCollectableName()));
    }

    private void addCodeViolationToBuilder(ValidationResult.ValidationResultBuilder builder, String illegalCode) {
        builder.addViolation(new Violation(
                getPathToCurrentCollectables(),
                "'%s' is not a valid code for the data source '%s'.".formatted(illegalCode, dataSource.get(0).getClass().getSimpleName()),
                Violation.Level.ERROR
        ));
    }

    protected abstract List<CollectableEntryConfig> getCollectables(ChallengesSchema challengesSchema);

    protected abstract String getPathToCurrentCollectables();
}
