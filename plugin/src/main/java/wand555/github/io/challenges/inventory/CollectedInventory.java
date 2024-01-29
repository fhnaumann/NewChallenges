package wand555.github.io.challenges.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectedInventory implements Listener {

    record InvPage(Inventory inventory, int page) {}

    public static final int MAX_INV_SIZE = 54;

    public static final int USABLE_INV_SPACE = MAX_INV_SIZE - 9; //keep last row free

    public static final int PREV_PAGE_IDX = MAX_INV_SIZE - 9;
    public static final int NEXT_PAGE_IX = MAX_INV_SIZE - 1;

    private Map<Integer, Inventory> paginatedInventories;
    private Map<Player, Integer> currentPageMap;

    private Map<Player, InvPage> openInventories;

    private final List<CollectedItemStack> collectedItemStacks;



    public CollectedInventory(Challenges plugin) {
        this.collectedItemStacks = new ArrayList<>();
        this.paginatedInventories = new HashMap<>();
        this.currentPageMap = new HashMap<>();
        this.openInventories = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addCollectedItemStack(CollectedItemStack collectedItemStack) {
        collectedItemStacks.add(collectedItemStack);
    }

    public void clearCollectedItemStacks() {
        collectedItemStacks.clear();
    }

    private void fillInventoryPage(Inventory inventory, int page) {
        //paginatedInventories.put(page, inventory);
        for(int i=0; i<USABLE_INV_SPACE; i++) {
            int globalIndex = (USABLE_INV_SPACE * page) + i;
            CollectedItemStack collectedItemStack;
            if(globalIndex < collectedItemStacks.size()) {
                collectedItemStack = collectedItemStacks.get(globalIndex);
            }
            else {
                collectedItemStack = new CollectedItemStack(Material.AIR, null, 0);
            }
            inventory.setItem(i, collectedItemStack.render());
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
        System.out.println(currentPage);
        int currentMaxNumber = ((currentPage+1) * USABLE_INV_SPACE);
        System.out.println(currentMaxNumber);
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