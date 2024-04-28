package wand555.github.io.challenges.inventory.progress;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleNarrowable;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectedInventory<S extends Data<K>, K extends Keyed> implements Listener, ResourceBundleNarrowable {

    protected final Context context;

    record InvPage(Inventory inventory, int page) {}

    public static final int MAX_INV_SIZE = 54;

    public static final int USABLE_INV_SPACE = MAX_INV_SIZE - 9; //keep last row free

    public static final int PREV_PAGE_IDX = MAX_INV_SIZE - 9;
    public static final int NEXT_PAGE_IX = MAX_INV_SIZE - 1;

    private final Map<Player, InvPage> openInventories;

    private final List<BaseCollectedItemStack<K>> collectedItemStacks;



    public CollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<K> enumType) {
        this.context = context;
        Map<K, Collect> map = ModelMapper.str2Collectable(collectables, context.dataSourceContext(), enumType);
        collectedItemStacks = map.entrySet().stream().map(entry -> createFromConfig(entry.getKey(), entry.getValue())).toList();
        this.openInventories = new HashMap<>();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    public void update(K about, Collect collect) {
        collectedItemStacks.stream().filter(kBaseCollectedItemStack -> kBaseCollectedItemStack.getAbout().equals(about)).findAny().ifPresent(kBaseCollectedItemStack -> {
            kBaseCollectedItemStack.update(collect);
        });
    }

    protected abstract SingleCollectedItemStack<K> createSingle(K about, Collect collect);

    protected abstract MultipleCollectedItemStack<K> createMultiple(K about, Collect collect);

    protected BaseCollectedItemStack<K> createFromConfig(K about, Collect collect) {
        if(collect.getAmountNeeded() == 1) {
            return createSingle(about, collect);
        }
        else {
            return createMultiple(about, collect);
        }
    }

    private void fillInventoryPage(Inventory inventory, int page) {
        //paginatedInventories.put(page, inventory);

        for(int i=0; i<USABLE_INV_SPACE; i++) {
            int globalIndex = (USABLE_INV_SPACE * page) + i;
            BaseCollectedItemStack baseCollectedItemStack;
            if(globalIndex < collectedItemStacks.size()) {
                baseCollectedItemStack = collectedItemStacks.get(globalIndex);
            }
            else {
                baseCollectedItemStack = BaseCollectedItemStack.AIR;
            }
            inventory.setItem(i, baseCollectedItemStack.render());
        }
        inventory.setItem(45, new ItemStack(Material.ARROW));
        inventory.setItem(53, new ItemStack(Material.ARROW));


    }

    private void updateOnPageSwap(Player player, int newPage) {
        InvPage invPage = openInventories.computeIfPresent(player, (player1, invPage1) -> new InvPage(invPage1.inventory, newPage));
        fillInventoryPage(invPage.inventory, newPage);
    }

    public void show(Player player) {
        Component title = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.name".formatted(getNameInResourceBundle()),
                false
                );
        Inventory inventory = Bukkit.createInventory(null, MAX_INV_SIZE, title);
        fillInventoryPage(inventory, 0);
        openInventories.put(player, new InvPage(inventory, 0));
        player.openInventory(inventory);
    }

    private boolean hasNextPage(int currentPage) {
        int currentMaxNumber = ((currentPage+1) * USABLE_INV_SPACE);
        return currentMaxNumber <= collectedItemStacks.size();
    }

    @EventHandler
    public void onPageSwapClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null) {
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(!openInventories.containsKey(player)) {
            return;
        }
        int currentPage = openInventories.get(player).page;
        Inventory currentPageInv = openInventories.get(player).inventory;
        if(player.getOpenInventory().getTopInventory().equals(currentPageInv)) {
            event.setCancelled(true);
        }
        if(!event.getClickedInventory().equals(currentPageInv)) {
            return;
        }
        int clickedSlot = event.getSlot();
        if(clickedSlot == PREV_PAGE_IDX) {
            if(currentPage == 0) {
                return;
            }
            updateOnPageSwap(player, currentPage-1);
        }
        else if(clickedSlot == NEXT_PAGE_IX) {
            if(hasNextPage(currentPage)) {
                updateOnPageSwap(player, currentPage+1);
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) {
            return;
        }
        openInventories.remove(player);
    }

    public List<BaseCollectedItemStack<K>> getCollectedItemStacks() {
        return collectedItemStacks;
    }
}
