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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Challenges extends JavaPlugin implements CommandExecutor {

    private Context tempContext;
    private URLReminder urlReminder;

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
        File settingsFile = getSettingsFile();
        if(!settingsFile.exists()) {
            try {
                Files.createDirectories(Paths.get(settingsFile.getParentFile().toURI()));
                Files.createFile(settingsFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        getCommand("load").setExecutor(this);
        getCommand("start").setExecutor(this);
        getCommand("save").setExecutor(this);
        getCommand("status").setExecutor(this);
        getCommand("skip").setExecutor(this);
        getCommand("pause").setExecutor(this);
        getCommand("resume").setExecutor(this);


        tempContext = null;
        try {
            tempContext = new Context.Builder()
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
        tempContext.challengeManager().setContext(tempContext); // immediately set context so it is available in the manager

        urlReminder = new URLReminder(tempContext);
        urlReminder.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

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
                Component noSettingsFile = ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "load.settings_missing"
                );
                Bukkit.broadcast(noSettingsFile);
                return true;
            }
            try {
                ActionHelper.showAllTitle(ComponentUtil.formatTitleMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().miscResourceBundle(),
                        "challenges.validation.start.title"
                ));

                Context context = FileManager.readFromFile(getSettingsFile(), this);
                tempContext = context;
                urlReminder.setContext(context);

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
                ActionHelper.showAllTitle(successTitle, successSubtitle);

                Component successChat = ComponentUtil.formatChallengesPrefixChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().miscResourceBundle(),
                        "challenges.validation.success.chat"
                );
                Bukkit.broadcast(successChat);

                // don't start yet
                //context.challengeManager().start();
            } catch (LoadValidationException e) {
                Component failureTitle = ComponentUtil.formatSubTitleMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().miscResourceBundle(),
                        "challenges.validation.failure.title"
                );
                Component failureSubtitle = ComponentUtil.formatSubTitleMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().miscResourceBundle(),
                        "challenges.validation.failure.subtitle"
                );
                ActionHelper.showAllTitle(failureTitle, failureSubtitle, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(10), Duration.ofSeconds(1)));
                Component failureChat = ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().miscResourceBundle(),
                        "challenges.validation.failure.chat",
                        Map.of(),
                        false
                );
                Bukkit.broadcast(failureChat);
                Bukkit.broadcast(e.getValidationResult().asFormattedComponent(tempContext));
            }
            //Main.main(null, file, this);
        }
        else if(command.getName().equalsIgnoreCase("start")) {

            if(tempContext.challengeManager().isRunning()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "start.already_running"
                ));
                return true;
            }
            if(!tempContext.challengeManager().isSetup()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "start.not_loaded"
                ));
                return true;
            }
            else if(!tempContext.challengeManager().isValid()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "start.violation_error"
                ));
                return true;
            }
            tempContext.challengeManager().start();
        }
        else if (command.getName().equalsIgnoreCase("save")) {
            try {
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(getSettingsFile()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(command.getName().equalsIgnoreCase("status")) {
            sender.sendMessage(tempContext.challengeManager().getCurrentStatus());
        }
        // command for skipping
        else if(command.getName().equalsIgnoreCase("skip")) {
            tempContext.challengeManager().onSkip(player);
        }
        else if(command.getName().equalsIgnoreCase("pause")) {
            tempContext.challengeManager().pause();
        }
        else if(command.getName().equalsIgnoreCase("resume")) {
            tempContext.challengeManager().resume();
        }
        return true;
    }
}
