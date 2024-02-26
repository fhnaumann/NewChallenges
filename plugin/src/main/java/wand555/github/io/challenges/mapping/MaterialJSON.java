package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Material;

public record MaterialJSON(
        @JsonProperty("code") String code,
        @JsonProperty("is_block") boolean isBlock,
        @JsonProperty("is_item") boolean isItem,
        @JsonProperty("translation_key") String translationKey,
        @JsonProperty("img_name") String imgName) implements DataSourceJSON<Material>  {

    @Override
    public Material toEnum() {
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }
}
