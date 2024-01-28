package wand555.github.io.challenges.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.List;

public record CollectedItemStack(Material material, String collectedBy, long whenCollectedSeconds) {

    public ItemStack render() {
        ItemStack itemStack = new ItemStack(material);
        if(material == Material.AIR) {
            return itemStack;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.lore(List.of(Component.text(collectedBy + whenCollectedSeconds)));
        //itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
