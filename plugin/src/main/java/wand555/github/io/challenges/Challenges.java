package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Progressable;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.utils.ActionHelper;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Challenges extends JavaPlugin implements CommandExecutor, Listener {

    private Context tempContext;
    private URLReminder urlReminder;

    private ChallengeFilesHandler challengeFilesHandler;
    private OfflineTempData offlineTempData;

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


        getCommand("challenges").setExecutor(this);
        getCommand("load").setExecutor(this);
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

        boolean success = handleLoad(offlineTempData.get("fileNameBeingPlayed", String.class), true);
        if(success) {
            tempContext.challengeManager().setGameState(ChallengeManager.GameState.PAUSED);
            TimerRunnable timerRunnable = new TimerRunnable(tempContext, tempContext.challengeManager().getTime());
            timerRunnable.start();
            tempContext.challengeManager().setTimerRunnable(timerRunnable);

            /*
            tempContext.challengeManager().getGoals().stream()
                    .filter(goal -> goal instanceof BossBarDisplay)
                    .filter(baseGoal -> !baseGoal.hasTimer() || baseGoal.getTimer().getOrder() == tempContext.challengeManager().getCurrentOrder())
                    .forEach(goal -> ((BossBarDisplay) goal).showBossBar(tempContext.plugin().getServer().getOnlinePlayers()));
            */
        }
        else {

        }

        /*
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
        */
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
    }

    private String challengeName2Filename(String challengeName, List<ChallengeFilesHandler.ChallengeLoadStatus> statuses) {
        return statuses.stream()
                .filter(challengeLoadStatus -> challengeLoadStatus.challengeMetadata() != null && challengeLoadStatus.challengeMetadata().getName().equals(challengeName))
                .findAny()
                .orElseThrow()
                .file().getName();
    }

    private boolean handleLoad(@Nullable String challengeNameToLoad, boolean asFileName) {
        List<ChallengeFilesHandler.ChallengeLoadStatus> statuses = challengeFilesHandler.getChallengesInFolderStatus();

        if(statuses.isEmpty()) {
            Component noSettingsFile = ComponentUtil.formatChallengesPrefixChatMessage(
                    tempContext.plugin(),
                    tempContext.resourceBundleContext().commandsResourceBundle(),
                    "load.settings_missing"
            );
            Bukkit.broadcast(noSettingsFile);
            return false;
        }
        if(challengeNameToLoad == null && statuses.size() > 1) {
            Component noSettingsFile = ComponentUtil.formatChallengesPrefixChatMessage(
                    tempContext.plugin(),
                    tempContext.resourceBundleContext().commandsResourceBundle(),
                    "load.specify_challenge"
            );
            Bukkit.broadcast(noSettingsFile);
            return false;
        }

        try {
            ActionHelper.showAllTitle(ComponentUtil.formatTitleMessage(
                    tempContext.plugin(),
                    tempContext.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.start.title"
            ));

            String filenameToBeLoaded = asFileName ? challengeNameToLoad : challengeName2Filename(challengeNameToLoad, statuses);
            Context context = FileManager.readFromFile(Paths.get(challengeFilesHandler.getFolderContainingChallenges().getAbsolutePath(), filenameToBeLoaded).toFile(), this);
            if(tempContext.challengeManager().isValid()) {
                // a previous challenge was loaded, better save it before unloading it
                FileManager.writeToFile(tempContext.challengeManager(), new FileWriter(new File(challengeFilesHandler.getFolderContainingChallenges(), challengeFilesHandler.getFileNameBeingPlayed())));
                tempContext.challengeManager().unload();
            }

            tempContext = context;
            urlReminder.setContext(context);
            challengeFilesHandler.setFileNameBeingPlayed(filenameToBeLoaded);

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
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Main.main(null, file, this);
        return true;
    }

    private Component formatChallengesInFolders2Component(Context context, ChallengeFilesHandler challengeFilesHandler) {
        Component fileSymbol = Component.text("\uD83D\uDCDD").appendSpace();
        return challengeFilesHandler.getChallengesInFolderStatus().stream().map(challengeLoadStatus -> {
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("name", Component.text(challengeLoadStatus.challengeMetadata().getName()));
            placeholders.put("mc-version", Component.text(challengeLoadStatus.challengeMetadata().getBuilderMCVersion()));
            Component formatted = fileSymbol.append(ComponentUtil.formatChatMessage(context.plugin(), context.resourceBundleContext().commandsResourceBundle(), "load.list.name", placeholders, false));
            if(challengeLoadStatus.file().getName().equals(challengeFilesHandler.getFileNameBeingPlayed())) {
                formatted = Component.text().decorate(TextDecoration.BOLD).append(formatted).asComponent();
            }
            return formatted;
        }).reduce(Component.empty(), ComponentUtil.NEWLINE_ACCUMULATOR);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(command.getName().equalsIgnoreCase("challenges")) {
            if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
                Component component = formatChallengesInFolders2Component(tempContext, challengeFilesHandler);
                sender.sendMessage(component);
            }
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

            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                handleLoad(args.length == 1 ? args[0] : null, false);
            });

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
