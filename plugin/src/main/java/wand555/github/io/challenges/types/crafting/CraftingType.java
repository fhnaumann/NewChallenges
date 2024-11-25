package wand555.github.io.challenges.types.crafting;

import com.google.common.base.Preconditions;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;
import wand555.github.io.challenges.types.crafting.detectors.*;

import java.util.UUID;
import java.util.logging.Logger;

public class CraftingType extends Type<CraftingData<?>> {

    private static final Logger logger = ChallengesDebugLogger.getLogger(CraftingType.class);

    private final NamespacedKey markedKey;

    private final ItemCraftingDetector itemCraftingDetector;
    private final FurnaceSmeltingDetector furnaceSmeltingDetector;
    private final CampfireCookingDetector campfireCookingDetector;
    private final SmithingDetector smithingDetector;
    private final StoneCuttingDetector stoneCuttingDetector;


    public CraftingType(Context context, TriggerCheck<CraftingData<?>> triggerCheck, Trigger<CraftingData<?>> whenTriggered, MCEventAlias.EventType eventType) {
        super(context, triggerCheck, whenTriggered, eventType);
        this.markedKey = new NamespacedKey(context.plugin(), "lastOpenedBy");
        this.itemCraftingDetector = new ItemCraftingDetector(context, this);
        context.plugin().getServer().getPluginManager().registerEvents(itemCraftingDetector, context.plugin());
        this.furnaceSmeltingDetector = new FurnaceSmeltingDetector(context, this);
        context.plugin().getServer().getPluginManager().registerEvents(furnaceSmeltingDetector, context.plugin());
        this.campfireCookingDetector = new CampfireCookingDetector(context, this);
        context.plugin().getServer().getPluginManager().registerEvents(campfireCookingDetector, context.plugin());
        this.smithingDetector = new SmithingDetector(context, this);
        context.plugin().getServer().getPluginManager().registerEvents(smithingDetector, context.plugin());
        this.stoneCuttingDetector = new StoneCuttingDetector(context, this);
        context.plugin().getServer().getPluginManager().registerEvents(stoneCuttingDetector, context.plugin());

        logger.fine("Registered listeners for %s.".formatted(getClass().getSimpleName()));
    }

    public <E extends Event> CraftingData<E> constructCraftingData(E event, Keyed keyedRecipe, UUID playerUUID, boolean internallyCrafted) {
        String code = "challenges-" + keyedRecipe.key().value(); // the datasource has a 'challenges-' prefix here
        CraftingTypeJSON craftingTypeJSON = DataSourceJSON.fromCode(context.dataSourceContext().craftingTypeJSONList(),
                                                                    code
        );
        return new CraftingData<>(event, playerUUID, context.challengeManager().getTime(),craftingTypeJSON, internallyCrafted);
    }

    public void markLastOpenedByOn(TileState tileState, Player player) {
        PersistentDataContainer pdc = tileState.getPersistentDataContainer();
        String playerUUIDAsString = player.getUniqueId().toString();
        String lastOpenedBy = pdc.get(markedKey, PersistentDataType.STRING);
        if(isMarked(tileState)) {
            logger.fine("Overriding %s with %s for blockstate %s at %s".formatted(lastOpenedBy,
                                                                                  playerUUIDAsString,
                                                                                  tileState,
                                                                                  tileState.getLocation()
            ));
        }
        logger.fine("Marking blockstate %s with %s at %s".formatted(tileState,
                                                                    playerUUIDAsString,
                                                                    tileState.getLocation()
        ));
        pdc.set(markedKey, PersistentDataType.STRING, playerUUIDAsString);
        tileState.update();
    }

    public boolean isMarked(TileState tileState) {
        PersistentDataContainer pdc = tileState.getPersistentDataContainer();
        boolean marked = pdc.has(markedKey, PersistentDataType.STRING);
        logger.fine("Blockstate %s at %s marked: %s".formatted(tileState, tileState.getLocation(), marked));
        return marked;
    }

    public UUID getLastMarked(TileState tileState) {
        Preconditions.checkArgument(isMarked(tileState), "TileState not marked!");
        return UUID.fromString(tileState.getPersistentDataContainer().get(markedKey, PersistentDataType.STRING));
    }

    public void handleBlockCookEvent(BlockCookEvent event) {
        if(!context.challengeManager().isRunning()) { // use isRunning and not canTakeEffect because this event does not involve a player
            return;
        }
        if(event.getRecipe() == null) {
            return;
        }
        if(!(event.getBlock().getState() instanceof TileState tileState)) {
            return;
        }
        if(!isMarked(tileState)) {
            return;
        }
        UUID markedPlayer = getLastMarked(tileState);
        CraftingData<?> craftingData = constructCraftingData(event, event.getRecipe(), markedPlayer, false);
        triggerIfCheckPasses(craftingData, event);
    }
}
