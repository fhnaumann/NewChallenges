package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public record EntityTypeJSON(
        @JsonProperty("code") String code,
        @JsonProperty("translation_key") String translationKey,
        @JsonProperty("img_name") String imgName
) implements DataSourceJSON<EntityType> {
    @Override
    public EntityType toEnum() {
        return EntityType.fromName(code);
    }

    public static String toCode(EntityType entityType) {
        return entityType.key().value();
    }

    @Override
    public String getCode() {
        return code;
    }
}
