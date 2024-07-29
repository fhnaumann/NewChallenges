package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wand555.github.io.challenges.commands.ChallengesCommand;
import wand555.github.io.challenges.commands.LoadCommand;
import wand555.github.io.challenges.criteria.goals.Progressable;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Challenges extends JavaPlugin implements CommandExecutor, Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(Challenges.class);

    public Context tempContext;
    public URLReminder urlReminder;

    private ChallengeFilesHandler challengeFilesHandler;
    private OfflineTempData offlineTempData;

    @EventHandler
    public void test(BlockPlaceEvent event) {
        System.out.println(event.getBlock().getType());
    }

    @Override
    public void onLoad() {
        addDefaultValuesToConfig();
        ChallengesDebugLogger.initLogging(this);

        //if(true) {
        if(!isLoadedFromTests()) {
            CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
        }

    }

    private boolean isLoadedFromTests() {
        return getServer().getClass().getName().contains("ServerMock");
    }

    private void addDefaultValuesToConfig() {
        FileConfiguration fileConfiguration = getConfig();
        ConfigValues.addDefaults(fileConfiguration);
        fileConfiguration.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onEnable() {
        if(!isLoadedFromTests()) {
            CommandAPI.onEnable();
        }

        //getCommand("challenges").setExecutor(this);
        //getCommand("load").setExecutor(this);
        getCommand("start").setExecutor(this);
        getCommand("cancel").setExecutor(this);
        getCommand("save").setExecutor(this);
        getCommand("skip").setExecutor(this);
        getCommand("pause").setExecutor(this);
        getCommand("resume").setExecutor(this);
        getCommand("progress").setExecutor(this);



        getServer().getPluginManager().registerEvents(this, this);

        offlineTempData = new OfflineTempData(this);
        tempContext = null;
        try {
            tempContext = new Context.Builder()
                    .withPlugin(this)
                    .withRuleResourceBundle(ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get()))
                    .withGoalResourceBundle(ResourceBundle.getBundle("goals", Locale.US, UTF8ResourceBundleControl.get()))
                    .withSettingsResourceBundle(ResourceBundle.getBundle("settings", Locale.US, UTF8ResourceBundleControl.get()))
                    .withPunishmentResourceBundle(ResourceBundle.getBundle("punishments", Locale.US, UTF8ResourceBundleControl.get()))
                    .withCommandsResourceBundle(ResourceBundle.getBundle("commands", Locale.US, UTF8ResourceBundleControl.get()))
                    .withMiscResourceBundle(ResourceBundle.getBundle("misc", Locale.US, UTF8ResourceBundleControl.get()))
                    .withSchemaRoot(new ObjectMapper().readTree(Main.class.getResourceAsStream("/challenges_schema.json")))
                    .withMaterialJSONList(Main.class.getResourceAsStream("/materials.json"))
                    .withEntityTypeJSONList(Main.class.getResourceAsStream("/entity_types.json"))
                    .withChallengeManager(new ChallengeManager())
                    .withRandom(new Random())
                    .withOfflineTempData(offlineTempData)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempContext.challengeManager().setContext(tempContext); // immediately set context so it is available in the manager
        urlReminder = new URLReminder(tempContext);
        urlReminder.start();

        try {
            challengeFilesHandler = new ChallengeFilesHandler(offlineTempData, Paths.get(getDataFolder().getAbsolutePath(), "settings").toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        Register commands
         */
        if(!isLoadedFromTests()) {
            LoadCommand.registerLoadCommand(tempContext, challengeFilesHandler);
            ChallengesCommand.registerChallengesCommand(tempContext, challengeFilesHandler);
        }


        String fileNameBeingPlayed = offlineTempData.get("fileNameBeingPlayed", String.class);
        boolean hasFileBeingPlayed = fileNameBeingPlayed != null;
        if(hasFileBeingPlayed) {
            logger.fine("Detected a file to load from previous session: " + fileNameBeingPlayed);
            try {
                LoadCommand.loadFile(tempContext, challengeFilesHandler, challengeFilesHandler.getFileBeingPlayed());
                logger.fine("Loaded '%s' from previous session.".formatted(fileNameBeingPlayed));

                // Accessing tempContext after LoadCommand#loadFile was called is a bit misleading.
                // loadFile reassigns tempContext to the newly (now final) context, so it's the same instance from now on

            } catch (LoadValidationException e) {
                logger.warning("Failed to load challenge '%s' from previous session.".formatted(fileNameBeingPlayed));
                logger.warning(e.getValidationResult().asFormattedString());
            }
        }
        else {
            logger.fine("No previous session exists, don't prematurely load anything.");
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
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(new File(challengeFilesHandler.getFolderContainingChallenges(), challengeFilesHandler.getFileNameBeingPlayed())));
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

        if(!isLoadedFromTests()) {
            CommandAPI.onDisable();
        }
    }

    private String challengeName2Filename(String challengeName, List<ChallengeFilesHandler.ChallengeLoadStatus> statuses) {
        return statuses.stream()
                .filter(challengeLoadStatus -> challengeLoadStatus.challengeMetadata() != null && challengeLoadStatus.challengeMetadata().getName().equals(challengeName))
                .findAny()
                .orElseThrow(IllegalArgumentException::new)
                .file().getName();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
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
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(new File(challengeFilesHandler.getFolderContainingChallenges(), challengeFilesHandler.getFileNameBeingPlayed())));
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
        else if(command.getName().equalsIgnoreCase("progress")) {
            List<Progressable> progressables = tempContext.challengeManager().getGoals().stream().filter(Progressable.class::isInstance).map(Progressable.class::cast).toList();
            if(progressables.isEmpty()) {
                // nothing to show progress for
                sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                        tempContext.plugin(),
                        tempContext.resourceBundleContext().commandsResourceBundle(),
                        "progress.empty.message"
                ));
                return true;
            }
            if(args.length == 0) {
                if(progressables.size() > 1) {
                    // more than one progressable goal, but not specified which to show
                    sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                            tempContext.plugin(),
                            tempContext.resourceBundleContext().commandsResourceBundle(),
                            "progress.toomany.message"
                    ));
                    return true;
                }
                else {
                    tempContext.challengeManager().onProgress(player, progressables.get(0));
                }
            }
            else if(args.length == 1) {
                Optional<Progressable> optionalProgressable = progressables.stream().filter(progressable -> progressable.getNameInCommand().equalsIgnoreCase(args[0])).findAny();
                if(optionalProgressable.isPresent()) {
                    tempContext.challengeManager().onProgress(player, optionalProgressable.get());
                }
                else {
                    // did not match any progressable goal that is active
                    sender.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                            tempContext.plugin(),
                            tempContext.resourceBundleContext().commandsResourceBundle(),
                            "progress.unknown_name.message",
                            Map.of("name", Component.text(args[0]))
                    ));
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void requestRPOnJoin(PlayerJoinEvent event) throws ExecutionException, InterruptedException {
        ResourcePackInfoLike pack = ResourcePackInfo.resourcePackInfo()
                .uri(URI.create("https://challenges-builder.s3.eu-central-1.amazonaws.com/Challenges+Plugin+RP.zip"))
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        // The player might be somewhere they shouldn't be when leaving/rejoining.
        // In that case, behave as if the thing they are busy with (ongoing MLG, ...) is completed.
        //TODO: MLG COMPLETE TRIGGER CALL

    }
}
