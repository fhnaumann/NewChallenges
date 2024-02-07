package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.InvProgress;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.types.item.ItemType;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.RandomUtil;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemGoal extends BaseGoal implements Triggable<ItemData>, Storable<ItemGoalConfig>, BossBarDisplay, InvProgress, Skippable {

    private final ItemType itemType;
    private final ItemGoalMessageHelper messageHelper;

    private final Map<Material, Collect> toCollect;
    private final boolean allItems;
    private final boolean allBlocks;

    private final boolean fixedOrder;
    private final BossBar bossBar;
    private final CollectedInventory collectedInventory;

    private final NamespacedKey markedKey;

    public ItemGoal(Context context, ItemGoalConfig config) {
        super(context, config.getComplete());
        JsonNode amountNeeded = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/amountNeeded/default");
        if(amountNeeded.isMissingNode()) {
            throw new RuntimeException();
        }
        JsonNode currentAmount = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/currentAmount/default");
        if(currentAmount.isMissingNode()) {
            throw new RuntimeException();
        }
        this.allItems = config.getAllItems();
        this.allBlocks = config.getAllBlocks();
        this.fixedOrder = config.getFixedOrder();

        // get data that may already exist
        // assumption: If some data already exists, then any setting allItems or allBlocks were already previously set
        // in that case just use what already exists and ignore the other settings
        // only if no data exists, assume that there have not been any previous saves and therefore fill the map according to the settings
        Map<String, CollectableDataConfig> existingItemsToCollect = config.getItems().getAdditionalProperties();
        if(!existingItemsToCollect.isEmpty()) {

        }

        if(this.allItems) {
            this.toCollect = Stream.of(Material.values()).filter(ModelMapper.VALID_ITEMS).collect(Collectors.toMap(
                    Function.identity(),
                    material -> new Collect(amountNeeded.asInt(), currentAmount.asInt())
            ));
        }
        else if(this.allBlocks) {
            this.toCollect = Stream.of(Material.values()).filter(ModelMapper.VALID_BLOCKS).collect(Collectors.toMap(
                    Function.identity(),
                    material -> new Collect(amountNeeded.asInt(), currentAmount.asInt())
            ));
        }
        else {
            this.toCollect = ModelMapper.str2Collectable(config.getItems().getAdditionalProperties(), Material.class);
        }

        if(this.fixedOrder) {

        }

        this.bossBar = createBossBar();
        this.collectedInventory = new CollectedInventory(context.plugin());

        this.markedKey = new NamespacedKey(context.plugin(), "marked");

        this.itemType = new ItemType(context, triggerCheck(), trigger());
        context.plugin().getServer().getPluginManager().registerEvents(itemType, context.plugin());
        this.messageHelper = new ItemGoalMessageHelper(context);
    }

    public boolean determineComplete() {
        return toCollect.values().stream().allMatch(Collect::isComplete);
    }

    @Override
    public BossBar createBossBar() {
        Map.Entry<Material, Collect> randomToCollect = RandomUtil.pickRandom(toCollect);

        Component formattedBossBarComponent = formatBossBarComponent(randomToCollect.getKey(), randomToCollect.getValue());
        return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    private Component formatBossBarComponent(Material material, Collect collect) {
        Component unicodeComponent = ResourcePackHelper.getMaterialUnicodeMapping(material);
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.bossbar.message",
                Map.of(
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                ),
                Map.of(
                        "item", unicodeComponent
                )
        );
    }

    private void refreshBossBar(Material material, Collect collect) {
        Component name;
        if(!collect.isComplete()) {
            name = formatBossBarComponent(material, collect);
        }
        else {
            Map.Entry<Material, Collect> randomToCollect = RandomUtil.pickRandom(toCollect);
            name = formatBossBarComponent(randomToCollect.getKey(), randomToCollect.getValue());
        }
        bossBar.name(name);
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public BossBarPriority getBossBarPriority() {
        return BossBarPriority.INFO;
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setItemGoal(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return Component.text("ItemGoal TODO");
    }

    @Override
    public ItemGoalConfig toGeneratedJSONClass() {
        CollectableEntryConfig collectableEntryConfig = new CollectableEntryConfig();
        toCollect.forEach((material, collect) -> collectableEntryConfig.setAdditionalProperty(material.key().asMinimalString(), collect.toGeneratedJSONClass()));
        return new ItemGoalConfig(allBlocks, allItems, isComplete(), fixedOrder, collectableEntryConfig);
    }

    private void newItemCollected(ItemData data) {
        Collect updatedCollect = getToCollect().computeIfPresent(data.itemStackInteractedWith().getType(), (material, collect) -> {
            // don't overshoot the total amount, because the player might have picked up more items (in the stack) that they needed
            int newCurrentAmount = Math.min(collect.getCurrentAmount()+data.itemStackInteractedWith().getAmount(), collect.getAmountNeeded());
            collect.setCurrentAmount(newCurrentAmount);
            return collect;
        });
        Component toSend;
        String soundInBundleKey;
        if(updatedCollect.isComplete()) {
            messageHelper.sendSingleReachedAction(data, updatedCollect);
        }
        else {
            messageHelper.sendSingleReachedAction(data, updatedCollect);
        }
        refreshBossBar(data.itemStackInteractedWith().getType(), updatedCollect);

        if(determineComplete()) {
            onComplete();
        }
    }




    @Override
    public void onComplete() {
        setComplete(true);

        messageHelper.sendAllReachedAction();

        notifyManager();
    }

    public Map<Material, Collect> getToCollect() {
        return toCollect;
    }

    @Override
    public @NotNull CollectedInventory getCollectedInventory() {
        return collectedInventory;
    }

    @Override
    public String getBaseCommand() {
        return "itemgoal";
    }

    @Override
    public void onShowInvProgressCommand() {

    }

    @Override
    public void onSkip() {

    }

    @Override
    public TriggerCheck<ItemData> triggerCheck() {
        return data -> getToCollect().containsKey(data.itemStackInteractedWith().getType());
    }

    @Override
    public Trigger<ItemData> trigger() {
        return this::newItemCollected;
    }
}
