package wand555.github.io.challenges.validation.types;

import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.validation.CodeableValidator;

import java.util.List;

public abstract class CraftingTypeValidator extends CodeableValidator<CraftingTypeJSON, CraftingTypeJSON> {
    public CraftingTypeValidator(List<CraftingTypeJSON> dataSource) {
        super(dataSource);
    }
}
