package wand555.github.io.challenges.validation.types;

import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.mapping.EntityTypeJSON;
import wand555.github.io.challenges.validation.CodeableValidator;

import java.util.List;

public abstract class MobTypeValidator extends CodeableValidator<EntityTypeJSON, EntityType> {
    public MobTypeValidator(List<EntityTypeJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean additionalCodeConstraints(EntityTypeJSON dataSourceElement) {
        return true;
    }
}
