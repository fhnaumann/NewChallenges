package wand555.github.io.challenges.validation.types;

import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.validation.CodeableValidator;

import java.util.List;

public abstract class DeathTypeValidator extends CodeableValidator<DeathMessage, DeathMessage> {
    public DeathTypeValidator(List<DeathMessage> dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean additionalCodeConstraints(DeathMessage dataSourceElement) {
        return true;
    }
}
