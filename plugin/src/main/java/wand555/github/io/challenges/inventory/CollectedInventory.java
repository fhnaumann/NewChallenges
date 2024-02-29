package wand555.github.io.challenges.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectedInventory<S> implements Listener {

    record InvPage(Inventory inventory, int page) {}

    public static final int MAX_INV_SIZE = 54;

    public static final int USABLE_INV_SPACE = MAX_INV_SIZE - 9; //keep last row free

    public static final int PREV_PAGE_IDX = MAX_INV_SIZE - 9;
    public static final int NEXT_PAGE_IX = MAX_INV_SIZE - 1;

    private final Map<Player, InvPage> openInventories;

    private final List<MultipleCollectedItemStack> multipleCollectedItemStacks;



    public CollectedInventory(Challenges plugin) {
        this.multipleCollectedItemStacks = new ArrayList<>();
        this.openInventories = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addCollectedItemStack(MultipleCollectedItemStack multipleCollectedItemStack) {
        multipleCollectedItemStacks.add(multipleCollectedItemStack);
    }

    public abstract void addOrUpdate(S data);

    public void clearCollectedItemStacks() {
        multipleCollectedItemStacks.clear();
    }

    private void fillInventoryPage(Inventory inventory, int page) {
        //paginatedInventories.put(page, inventory);
        /*
        for(int i=0; i<USABLE_INV_SPACE; i++) {
            int globalIndex = (USABLE_INV_SPACE * page) + i;
            MultipleCollectedItemStack multipleCollectedItemStack;
            if(globalIndex < multipleCollectedItemStacks.size()) {
                multipleCollectedItemStack = multipleCollectedItemStacks.get(globalIndex);
            }
            else {
                multipleCollectedItemStack = new MultipleCollectedItemStack(Material.AIR, null, 0);
            }
            inventory.setItem(i, multipleCollectedItemStack.render());
        }
        inventory.setItem(45, new ItemStack(Material.ARROW));
        inventory.setItem(53, new ItemStack(Material.ARROW));

         */
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
        System.out.println(currentPage);
        int currentMaxNumber = ((currentPage+1) * USABLE_INV_SPACE);
        System.out.println(currentMaxNumber);
        return currentMaxNumber <= multipleCollectedItemStacks.size();
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
}
