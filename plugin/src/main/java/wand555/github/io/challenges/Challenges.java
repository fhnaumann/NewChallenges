package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wand555.github.io.challenges.criteria.goals.Progressable;
import wand555.github.io.challenges.utils.ActionHelper;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Challenges extends JavaPlugin implements CommandExecutor, Listener {

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

        getCommand("load").setExecutor(this);
        getCommand("start").setExecutor(this);
        getCommand("cancel").setExecutor(this);
        getCommand("save").setExecutor(this);
        getCommand("skip").setExecutor(this);
        getCommand("pause").setExecutor(this);
        getCommand("resume").setExecutor(this);

        getServer().getPluginManager().registerEvents(this, this);


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
        else {
            handleLoad();
        }
    }

    @Override
    public void onDisable() {
        if(tempContext == null) {
            return;
        }
        if(!tempContext.challengeManager().isSetup()) {
            // Not being in setup phase ensures that the challenge data had been previously loaded.
            // Otherwise, the potentially existing data may be wiped because "nothing" is written.
            // When being in setup phase, the server cannot add any potential data, therefore it is not necessary to write something in this case.
            try {
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(getSettingsFile()));
            } catch (IOException e) {
                Bukkit.broadcast(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "save.error"
                ));
                throw new RuntimeException(e);
            }
        }
        tempContext.challengeManager().shutdownRunnables();
    }

    private boolean hasSettingsFileProvided() {
        File file = getSettingsFile();
        return file.exists() && file.isFile();
    }

    private File getSettingsFile() {
        return Paths.get(getDataFolder().getAbsolutePath(), "settings", "data.json").toFile();
    }

    private void handleLoad() {
        if(!hasSettingsFileProvided()) {
            Component noSettingsFile = ComponentUtil.formatChallengesPrefixChatMessage(
                    tempContext.plugin(),
                    tempContext.resourceBundleContext().commandsResourceBundle(),
                    "load.settings_missing"
            );
            Bukkit.broadcast(noSettingsFile);
            return;
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


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(command.getName().equalsIgnoreCase("load")) {
            if(tempContext.challengeManager().isRunning() || tempContext.challengeManager().isPaused()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "load.already_running"
                ));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(this, this::handleLoad);

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
            else if(!tempContext.challengeManager().isValid()) {
                // either invalid or never loaded
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "start.violation_error"
                ));
                return true;
            }
            tempContext.challengeManager().start();
        }
        else if(command.getName().equalsIgnoreCase("cancel")) {
            // artificially end the challenge (as success)
            tempContext.challengeManager().endChallenge(true);
        }
        else if (command.getName().equalsIgnoreCase("save")) {
            try {
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(getSettingsFile()));
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "save.saved"
                ));
                return true;
            } catch (IOException e) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "save.error"
                ));
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
            if(tempContext.challengeManager().isPaused()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "pause.already_paused"
                ));
                return true;
            }
            else if(!tempContext.challengeManager().isRunning()) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "pause.not_running"
                ));
                return true;
            }
            tempContext.challengeManager().pause();
        }
        else if(command.getName().equalsIgnoreCase("resume")) {
            if(!tempContext.challengeManager().isPaused() && tempContext.challengeManager().getGameState() != ChallengeManager.GameState.ENDED) {
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "resume.not_paused"
                ));
                return true;
            }
            tempContext.challengeManager().resume();
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void requestRPOnJoin(PlayerJoinEvent event) throws ExecutionException, InterruptedException {
        ResourcePackInfoLike pack = ResourcePackInfo.resourcePackInfo()
                .uri(URI.create("https://www.mc-challenges.com/resourcepack"))
                .computeHashAndBuild().get();
        ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(pack)
                .prompt(ComponentUtil.formatChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().miscResourceBundle(),
                        "rp.prompt",
                        false
                )
                )
                .required(true)
                .build();

        event.getPlayer().sendResourcePacks(request);
    }
}
