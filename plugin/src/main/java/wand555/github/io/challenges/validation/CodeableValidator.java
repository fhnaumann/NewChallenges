package wand555.github.io.challenges.validation;

import org.bukkit.Keyed;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;

import java.util.List;
import java.util.function.Predicate;

public abstract class CodeableValidator<T extends DataSourceJSON<K>, K extends Keyed> extends ModelValidator {

    private final List<T> dataSource;

    public CodeableValidator(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema challengesSchema) {
        CodeValidator<T,K> codeValidator = new CodeValidator<>(dataSource, this::additionalCodeConstraints, getCollectables(challengesSchema), getPathToCurrentCollectables());
        return codeValidator.performValidation(builder, challengesSchema);
    }

    protected ValidationResult.ValidationResultBuilder performAdditionalValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema schema) {
        // default to nothing
        return builder;
    }

    protected abstract boolean additionalCodeConstraints(T dataSourceElement);

    protected abstract List<CollectableEntryConfig> getCollectables(ChallengesSchema challengesSchema);

    protected abstract String getPathToCurrentCollectables();
}
