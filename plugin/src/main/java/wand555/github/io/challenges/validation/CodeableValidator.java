package wand555.github.io.challenges.validation;

import org.bukkit.Keyed;
import wand555.github.io.challenges.files.ProgressListener;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.DataSourceJSON;

import java.util.List;

public abstract class CodeableValidator<T extends DataSourceJSON<K>, K extends Keyed> extends ModelValidator {

    private final List<T> dataSource;

    public CodeableValidator(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema, ProgressListener progressListener) {
        CodeValidator<T, K> codeValidator = new CodeValidator<>(dataSource,
                                                                this::additionalCodeConstraints,
                                                                getCodes(challengesSchema),
                                                                getPathToCurrentCollectables()
        );
        return codeValidator.performValidation(builder, challengesSchema, progressListener);
    }

    protected abstract boolean additionalCodeConstraints(T dataSourceElement);

    protected abstract List<String> getCodes(Model challengesSchema);

    protected abstract String getPathToCurrentCollectables();
}
