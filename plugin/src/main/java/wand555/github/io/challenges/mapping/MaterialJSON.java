package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MaterialJSON(
        @JsonProperty("code") String code,
        @JsonProperty("is_block") boolean isBlock,
        @JsonProperty("is_item") boolean isItem,
        @JsonProperty("translation_key") String translationKey,
        @JsonProperty("img_name") String imgName) {

}
