package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.translation.Translatable;
import net.kyori.adventure.util.Ticks;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.CommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;
import wand555.github.io.challenges.commands.SkippableParser;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.utils.ActionHelper;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Challenges extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        /*
         * #8846f2 Only [Challenges]
         * #c9526c Only [Punishment]
         * #ffc54d Only [Rules]
         * #008e6e Only [Goals]
         *
         * #fceaff default text
         * #64baaa highlight in text
         */
        if(!getDataFolder().exists()) {
            boolean created = getDataFolder().mkdir();
        }

        getCommand("load").setExecutor(this);
        getCommand("save").setExecutor(this);
        getCommand("status").setExecutor(this);
        getCommand("skip").setExecutor(this);
        getCommand("pause").setExecutor(this);
        getCommand("resume").setExecutor(this);


        Context context = null;
        try {
            context = new Context.Builder()
                    .withPlugin(this)
                    .withRuleResourceBundle(ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get()))
                    .withGoalResourceBundle(ResourceBundle.getBundle("goals", Locale.US, UTF8ResourceBundleControl.get()))
                    .withPunishmentResourceBundle(ResourceBundle.getBundle("punishments", Locale.US, UTF8ResourceBundleControl.get()))
                    .withCommandsResourceBundle(ResourceBundle.getBundle("commands", Locale.US, UTF8ResourceBundleControl.get()))
                    .withMiscResourceBundle(ResourceBundle.getBundle("misc", Locale.US, UTF8ResourceBundleControl.get()))
                    .withSchemaRoot(new ObjectMapper().readTree(Main.class.getResourceAsStream("/challenges_schema.json")))
                    .withMaterialJSONList(Main.class.getResourceAsStream("/materials.json"))
                    .withEntityTypeJSONList(Main.class.getResourceAsStream("/entity_types.json"))
                    .withChallengeManager(new ChallengeManager())
                    .withRandom(new Random())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        context.challengeManager().setContext(context); // immediately set context so it is available in the manager

        /*
         * Send message with link to builder website.
         */
        Component builderLink = ComponentUtil.formatChallengesPrefixChatMessage(
                this,
                context.resourceBundleContext().miscResourceBundle(),
                "challenge.builder.chat",
                Map.of("url", ComponentUtil.BUILDER_LINK)
        );
        Bukkit.broadcast(builderLink);

        org.incendo.cloud.Command<CommandSender> command = org.incendo.cloud.Command.newBuilder("goal", CommandMeta.empty())
                .senderType(CommandSender.class)
                .optional("goalType", ParserDescriptor.of(new SkippableParser<>(context.challengeManager().getGoals()), Skippable.class))
                .handler(commandContext -> {
                    Skippable skippable = commandContext.get("goalType");
                    try {
                        skippable.onSkip();
                        // send skip message here
                    } catch (UnskippableException e) {
                        throw new RuntimeException(e);
                    }

                }).build();
        PaperCommandManager<CommandSender> commandManager = new PaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        commandManager.parserRegistry().registerParserSupplier(TypeToken.get(Skippable.class), options -> new SkippableParser<>(manager.getGoals()));
        commandManager.command(command);

        Context finalContext = context;
        org.incendo.cloud.Command<CommandSender> loadCommand = org.incendo.cloud.Command.<CommandSender>newBuilder("load", CommandMeta.empty())
                .senderType(CommandSender.class)
                .futureHandler(commandContext -> onLoadCommand(commandContext, finalContext))
                .build();
        PaperCommandManager<CommandSender> asyncCommandManager = new PaperCommandManager<>(
                this,
                ExecutionCoordinator.asyncCoordinator(),
                SenderMapper.identity()
        );
    }

    private CompletableFuture<Void> onLoadCommand(CommandContext<CommandSender> commandContext, Context context) {
        if(!hasSettingsFileProvided()) {
            commandContext.sender().sendMessage("settings file not provided");
            return CompletableFuture.completedFuture(null);
        }
        try {
            commandContext.sender().showTitle(Title.title(ComponentUtil.formatTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.start.title"
            ), Component.empty()));

            context = FileManager.readFromFile(getSettingsFile(), this);

            Component successTitle = ComponentUtil.formatTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.success.title"
            );
            Component successSubtitle = ComponentUtil.formatSubTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.success.subtitle"
            );
            commandContext.sender().showTitle(Title.title(successTitle, successSubtitle));

            Component successChat = ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.success.chat"
            );
            Bukkit.broadcast(successChat);
            return CompletableFuture.completedFuture(null);
        } catch (LoadValidationException e) {
            Component failureTitle = ComponentUtil.formatSubTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.failure.title"
            );
            Component failureSubtitle = ComponentUtil.formatSubTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.failure.subtitle"
            );
            commandContext.sender().showTitle(Title.title(failureTitle, failureSubtitle, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofSeconds(1))));
            Component failureChat = ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.failure.chat",
                    Map.of(),
                    false
            );
            Bukkit.broadcast(failureChat);
            Bukkit.broadcast(e.getValidationResult().asFormattedComponent(context));
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private ChallengeManager manager;

    private boolean hasSettingsFileProvided() {
        File file = getSettingsFile();
        return file.exists() && file.isFile();
    }

    private File getSettingsFile() {
        return Paths.get(getDataFolder().getAbsolutePath(), "settings", "data.json").toFile();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }


        if(command.getName().equalsIgnoreCase("load")) {
            if(!hasSettingsFileProvided()) {
                sender.sendMessage("settings file not provided");
            }
            try {
                Context context = FileManager.readFromFile(getSettingsFile(), this);
                context.challengeManager().start();
            } catch (LoadValidationException e) {
                throw new RuntimeException(e);
            }
            //Main.main(null, file, this);
        }
        else if(command.getName().equalsIgnoreCase("start")) {

        }
        else if (command.getName().equalsIgnoreCase("save")) {
            File file = new File(getDataFolder(), "data.json");
            try {
                FileManager.writeToFile(manager, new FileWriter(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(command.getName().equalsIgnoreCase("status")) {
            sender.sendMessage(manager.getCurrentStatus());
        }
        // command for skipping
        else if(command.getName().equalsIgnoreCase("skip")) {
            manager.onSkip(player);
        }
        else if(command.getName().equalsIgnoreCase("pause")) {
            manager.pause();
        }
        else if(command.getName().equalsIgnoreCase("resume")) {
            manager.resume();
        }
        if(command.getName().equalsIgnoreCase("test")) {
            Bukkit.broadcast(Component.text("123"));
            Bukkit.broadcast(Component.translatable("minecraft.mineable.axe"));
            Bukkit.broadcast(Component.translatable("item.minecraft.carrot"));

            List<String> translations = new ArrayList<>(Stream.of(
                    Arrays.stream(Material.values())
                            .filter(material -> !material.isLegacy())
                            .filter(Material::isBlock)
                            //.filter(material -> !Objects.equals(material.translationKey(), getBlockTranslationKey(material))) // comment out to get everything
                            .map(material -> String.format("%s, %s", material.translationKey(), getBlockTranslationKey(material)))
                            .toList(),
                    Arrays.stream(Material.values())
                            .filter(material -> !material.isLegacy())
                            .filter(Material::isItem)
                            //.filter(material -> !Objects.equals(material.translationKey(), getItemTranslationKey(material))) // comment out to get everything
                            .map(material -> String.format("%s, %s", material.translationKey(), getItemTranslationKey(material)))
                            .toList(),
                    Arrays.stream(EntityType.values())
                            .filter(entityType -> entityType.getName() != null)
                            .filter(entityType -> entityType != EntityType.UNKNOWN)
                            //.filter(entityType -> !entityType.translationKey().equals(formatTranslatable("entity", entityType))) // comment out to get everything
                            .map(entityType -> String.format("%s,%s", entityType.translationKey(), getTranslationKey(entityType)))
                            .toList(),
                    Arrays.stream(Material.values())
                            .filter(material -> !material.isLegacy())
                            .filter(Material::isItem)
                            .map(ItemStack::new)
                            //.filter(itemStack -> !Objects.equals(itemStack.translationKey(), getTranslationKey(itemStack))) // comment out to get everything
                            .map(itemStack -> String.format("%s,%s", itemStack.translationKey(), getTranslationKey(itemStack)))
                            .toList()
            ).flatMap(Collection::stream).toList());
            translations.
                    add(0, "native, mocked");
            try {
                FileWriter writer = new FileWriter(new File(getDataFolder(), "translations.csv"));
                for (String s : translations) {
                    writer.write(s + System.lineSeparator());
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Bukkit.broadcastMessage(Bukkit.getUnsafe().getTranslationKey(new ItemStack(Material.POTION)));
            Bukkit.broadcastMessage(Material.POTION.translationKey());
            Bukkit.broadcastMessage(Material.ACACIA_WALL_SIGN.translationKey());
            Bukkit.broadcastMessage(Material.ACACIA_WALL_HANGING_SIGN.translationKey());
            Bukkit.broadcastMessage(Tag.WALL_HANGING_SIGNS.key().asString());
            Bukkit.broadcastMessage(Tag.WALL_SIGNS.key().asString());
            /**
             *
             * Block prefix: block.minecraft.XXX
             * Item prefix: item.minecraft.XXX
             * Mob prefix: entity.minecraft.XXX
             *
             */


        }
        return true;
    }

    /**
     * Gets the translation key for a {@link Material} that {@link Material#isBlock() is a block}.
     * @param material the material to translate
     * @return a string of the structure "block.{@literal <namespace>}.{@literal <material>}" (e.g. "block.minecraft.stone"), or null if not a block.
     */
    public String getBlockTranslationKey(Material material)
    {
        if(!material.isBlock()) {
            return null;
        }
        // edge cases: WHEAT and NETHER_WART are blocks, but still use the "item" prefix
        if(material == Material.WHEAT || material == Material.NETHER_WART) {
            return formatTranslatable("item", material);
        }
        return formatTranslatable("block", material);
    }

    /**
     * Gets the translation key for a {@link Material} that {@link Material#isItem() is an item}.
     * @param material the material to translate
     * @return a string of the structure "item.{@literal <namespace>}.{@literal <material>}" (e.g. "item.minecraft.carrot"), or null if not an item.
     */
    public String getItemTranslationKey(Material material)
    {
        if(!material.isItem()) {
            return null;
        }
        // edge cases: WHEAT and NETHER_WART are blocks, but still use the "item" prefix (therefore this check has to be done BEFORE the isBlock check below)
        if(material == Material.WHEAT || material == Material.NETHER_WART) {
            return formatTranslatable("item", material);
        }
        // edge case: If a translation key from an item is requested from anything that is also a block, the block translation key is always returned
        // e.g: Material#STONE is a block (but also an obtainable item in the inventory). However, the translation key is always "block.minecraft.stone".
        if(material.isBlock()) {
            return formatTranslatable("block", material);
        }
        return formatTranslatable("item", material);
    }

    /**
     * Gets the translation key for an {@link EntityType}. Throws an error for custom entities.
     * @param type the entity to translate
     * @return a string of the structure "item.{@literal <namespace>}.{@literal <entity_type>}" (e.g. "entity.minecraft.pig").
     */
    public String getTranslationKey(EntityType type)
    {
        Preconditions.checkArgument(type.getName() != null, "Invalid name of EntityType %s for translation key", type);
        Arrays.stream(EntityType.values())
                .filter(entityType ->type.getName().equals(entityType.getName()))
                .findFirst()
                .orElseThrow();
        return formatTranslatable("entity", type);
    }

    /**
     * Gets the translation key for a {@link ItemStack} that {@link Material#isItem() is an item}.
     * @param itemStack the itemstack to translate
     * @return a string of the structure "item.{@literal <namespace>}.{@literal <material>}" (e.g. "item.minecraft.carrot"), or null if not an item.
     */
    public String getTranslationKey(ItemStack itemStack)
    {
        if(itemStack.getType().isItem()) {
            Material material = itemStack.getType();
            if(!material.isItem()) {
                return null;
            }
            // edge cases: WHEAT and NETHER_WART are blocks, but still use the "item" prefix (therefore this check has to be done BEFORE the isBlock check below)
            if(material == Material.WHEAT || material == Material.NETHER_WART) {
                return formatTranslatable("item", material);
            }
            // edge case: If a translation key from an item is requested from anything that is also a block, the block translation key is always returned
            // e.g: Material#STONE is a block (but also an obtainable item in the inventory). However, the translation key is always "block.minecraft.stone".
            if(material.isBlock()) {
                return formatTranslatable("block", material);
            }
            return formatTranslatable("item", material, true);
        }
        else if(itemStack.getType().isBlock()) {
            return getBlockTranslationKey(itemStack.getType());
        }
        else {
            return null;
        }
    }

    private <T extends Keyed & Translatable> String formatTranslatable(String prefix, T translatable, boolean fromItemStack)
    {
        // enforcing Translatable is not necessary, but translating only makes sense when the object is really translatable by design.
        String value = translatable.key().value();
        if(translatable instanceof Material material) {
            if(Tag.WALL_HANGING_SIGNS.isTagged(material) || Tag.WALL_SIGNS.isTagged(material) || value.endsWith("wall_banner") || value.endsWith("wall_torch") || value.endsWith("wall_skull") || value.endsWith("wall_head")) {
                value = value.replace("wall_", "");
            }
            final Set<Material> EMPTY_EFFECTS = Set.of(Material.POTION, Material.SPLASH_POTION, Material.TIPPED_ARROW, Material.LINGERING_POTION);
            if(fromItemStack && EMPTY_EFFECTS.contains(material)) {
                value += ".effect.empty";
            }
        }
        return String.format("%s.%s.%s", prefix, translatable.key().namespace(), value);
    }

    private <T extends Keyed & Translatable> String formatTranslatable(String prefix, T translatable)
    {
        return formatTranslatable(prefix, translatable, false);
    }
}
