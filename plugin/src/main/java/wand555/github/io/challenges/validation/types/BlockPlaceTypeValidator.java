package wand555.github.io.challenges.validation.types;

import org.bukkit.Material;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.validation.CodeableValidator;

import java.util.List;

public abstract class BlockPlaceTypeValidator extends CodeableValidator<MaterialJSON, Material> {


    public BlockPlaceTypeValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean additionalCodeConstraints(MaterialJSON dataSourceElement) {
        return dataSourceElement.isBlock();
    }
}
