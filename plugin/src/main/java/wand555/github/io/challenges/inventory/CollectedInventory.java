package wand555.github.io.challenges.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectedInventory<S extends Data<K>, K extends Keyed> implements Listener, Storable<List<CompletionConfig>> {

    protected final Context context;

    record InvPage(Inventory inventory, int page) {}

    public static final int MAX_INV_SIZE = 54;

    public static final int USABLE_INV_SPACE = MAX_INV_SIZE - 9; //keep last row free

    public static final int PREV_PAGE_IDX = MAX_INV_SIZE - 9;
    public static final int NEXT_PAGE_IX = MAX_INV_SIZE - 1;

    private final Map<Player, InvPage> openInventories;

    private final List<BaseCollectedItemStack> collectedItemStacks;



    public CollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<K> enumType) {
        this.context = context;
        Map<K, Collect> map = ModelMapper.str2Collectable(collectables, context.dataSourceContext(), enumType);
        this.collectedItemStacks = new ArrayList<>();
        this.openInventories = new HashMap<>();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    public void addOrUpdate(K data, Collect collect) {
        long secondsSinceStart = context.challengeManager().getTime();
        if(collect.getAmountNeeded() == 1) {
            // if it is a single collected itemstack, then it cannot have existed previously
            collectedItemStacks.add(createSingle(data, secondsSinceStart));
        }
        else {
            MultipleCollectedItemStack<?> match = findMatch(data);
            if(match == null) {
                collectedItemStacks.add(createMultiple(data, secondsSinceStart));
            }
            else {
                int idx = collectedItemStacks.indexOf(match);
                MultipleCollectedItemStack<?> existing = (MultipleCollectedItemStack<?>) collectedItemStacks.get(idx);
                if(collect.isComplete()) {
                    existing.setWhenCollectedSeconds((int) secondsSinceStart);
                }
                existing.update(collect);
            }
        }
    }

    private MultipleCollectedItemStack<?> findMatch(K data) {
        return collectedItemStacks.stream()
                .filter(collectedItemStack -> collectedItemStack instanceof MultipleCollectedItemStack<?>)
                .map(MultipleCollectedItemStack.class::cast)
                .filter(multipleCollectedItemStack -> multipleCollectedItemStack.getAbout() == data)
                .findFirst()
                .orElse(null);
    }

    protected abstract BaseCollectedItemStack createSingle(K data, long secondsSinceStart);

    protected abstract MultipleCollectedItemStack<?> createMultiple(K data, long secondsSinceStart);

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
        Inventory inventory = Bukkit.createInventory(null, MAX_INV_SIZE); // TODO: title
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
        if(openInventories.values().stream().noneMatch(invPage -> invPage.inventory().equals(event.getClickedInventory()))) {
            return;
        }
        int currentPage = openInventories.get(player).page;
        Inventory currentPageInv = openInventories.get(player).inventory;
        if(!event.getClickedInventory().equals(currentPageInv)) {
            return;
        }
        event.setCancelled(true);
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

    @Override
    public List<CompletionConfig> toGeneratedJSONClass() {
        return collectedItemStacks.stream().map(BaseCollectedItemStack::toGeneratedJSONClass).toList();
    }
}
