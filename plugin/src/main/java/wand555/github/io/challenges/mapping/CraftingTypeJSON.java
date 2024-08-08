package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class CraftingTypeJSON implements DataSourceJSON<CraftingTypeJSON>, Keyed {

    private final String key;
    private final Material craftingResult;
    private final RecipeType recipeType;

    public CraftingTypeJSON(@JsonProperty("code") String key, @JsonProperty("result") String craftingResult, @JsonProperty("recipeType") String recipeType) {
        this.key = key;
        // copied the code from MaterialJSON, because I can't access it from here
        this.craftingResult = Enum.valueOf(Material.class, craftingResult.toUpperCase());

        this.recipeType = from(recipeType);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey("challenges", getCode());
    }

    @Override
    public CraftingTypeJSON toEnum() {
        return this;
    }

    @Override
    public String getCode() {
        return key;
    }

    public Material getCraftingResult() {
        return craftingResult;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    private static RecipeType from(String string) {
        switch(string) {
            case "crafting" -> {
                return RecipeType.CRAFTING;
            }
            case "furnace" -> {
                return RecipeType.FURNACE;
            }
            case "blasting" -> {
                return RecipeType.BLASTING;
            }
            case "campfire" -> {
                return RecipeType.CAMPFIRE;
            }
            case "smithing" -> {
                return RecipeType.SMITHING;
            }
            case "smoking" -> {
                return RecipeType.SMOKING;
            }
            case "stonecutting" -> {
                return RecipeType.STONECUTTING;
            }
            default -> throw new RuntimeException("Unknown recipe type '%s'!".formatted(string));
        }
    }

    public enum RecipeType {
        CRAFTING,
        FURNACE,
        BLASTING,
        CAMPFIRE,
        SMITHING,
        SMOKING,
        STONECUTTING;
    }
}
