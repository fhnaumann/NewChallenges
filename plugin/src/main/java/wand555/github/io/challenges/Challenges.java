package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import wand555.github.io.challenges.generated.TestOutputSchema;
import wand555.github.io.challenges.goals.Collect;
import wand555.github.io.challenges.goals.MobGoal;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.rules.NoBlockBreakRule;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

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

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private ChallengeManager manager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("load")) {




            File file = new File(getDataFolder(), "data.json");
            if(!file.exists()) {

            }
            try {
                manager = FileManager.readFromFile(file, this);
                manager.start();
            } catch (LoadValidationException | IOException e) {
                throw new RuntimeException(e);
            }
            //Main.main(null, file, this);
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
        if(command.getName().equalsIgnoreCase("test")) {

            TranslationRegistry registry = TranslationRegistry.create(Key.key("namespace:value"));
            ResourceBundle bundle = ResourceBundle.getBundle("test", Locale.US, UTF8ResourceBundleControl.get());
            registry.registerAll(Locale.US, bundle, true);
            GlobalTranslator.translator().addSource(registry);


            ResourceBundle resourceBundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
            Component bundleTranslated = Component.translatable("some.translation.key");

            /**
             *
             * Block prefix: block.minecraft.XXX
             * Item prefix: item.minecraft.XXX
             * Mob prefix: entity.minecraft.XXX
             *
             */

            Component translated = MiniMessage.miniMessage().deserialize(
                    bundle.getString("some.translation.key"),
                    Placeholder.component("player", Component.translatable(String.format("block.minecraft.%s", Material.DIRT.toString().toLowerCase()))));

            sender.sendMessage(translated);
            ResourceBundle rulesBundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
            sender.sendMessage("ABC");
            try {

                ChallengeManager challengeManager = new ChallengeManager();

                JsonNode schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
                MobGoal mobGoal = new MobGoal(new Context(this, new ResourceBundleContext(bundle, null, null), schemaRoot, challengeManager), new HashMap<>(Map.of(EntityType.PIG, new Collect(2))));
                Player player = (Player) sender;
                mobGoal.openCollectedInv(player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        return true;
    }
}
