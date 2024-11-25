package wand555.github.io.challenges.types.crafting;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.junit.jupiter.api.*;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathType;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled("MockBukkit does not register default recipes, so the mocked server has 0 recipes when running")
public class ItemCraftingDetectorTest extends CraftingDetectorTest {



    @Test
    public void testPlayerSelectsResultInInternalCrafting() {
        simulateStickCrafting(server, player);
        verify(mockedTriggerCheck).applies(new CraftingData<>(null, player, 0, stickCraftingTypeJSON(), true));
    }

    public static void simulateStickCrafting(ServerMock server, Player player) {
        InventoryView open = player.openInventory(player.getInventory());
        open.getTopInventory().setItem(3, new ItemStack(Material.STICK, 4));
        CraftItemEvent event = new CraftItemEvent(stickRecipe(server), open, null, 3, ClickType.LEFT, InventoryAction.UNKNOWN);
        CriteriaUtil.callEvent(server, event, 1);
    }

    private static Recipe stickRecipe(ServerMock server) {
        return server.getRecipe(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "stick"));
    }

    private CraftingTypeJSON stickCraftingTypeJSON() {
        return new CraftingTypeJSON(null, "stick", "stick", "crafting");
    }
}
