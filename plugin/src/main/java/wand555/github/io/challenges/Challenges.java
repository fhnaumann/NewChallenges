package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Challenges extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(getDataFolder().exists() + "");
        System.out.println(getDataFolder().exists());
        if(!getDataFolder().exists()) {
            boolean created = getDataFolder().mkdir();
            System.out.println("PL FOLDER CREATED?" + created);
        }



        // Plugin startup logic
        getCommand("test").setExecutor(this);
        getCommand("load").setExecutor(this);

        Expansion.Builder builder = Expansion.builder("challenges");
        builder.globalPlaceholder("player", (argumentQueue, context) -> {
            String playerName = argumentQueue.popOr("argument missing").value();
            return Tag.selfClosingInserting(Component.text(playerName));
        });
        builder.audiencePlaceholder("player2", (audience, queue, ctx) -> {
            return Tag.selfClosingInserting(Component.text(((Player) audience).getName()));
        });

        Expansion expansion = builder.build();
        if(!expansion.registered()) {
            expansion.register();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("load")) {
            File file = new File(getDataFolder(), "data.json");
            if(!file.exists()) {

            }
            try {
                ChallengeManager challengeManager = FileManager.readFromFile(file, this);
            } catch (LoadValidationException e) {
                throw new RuntimeException(e);
            }
            //Main.main(null, file, this);
        }
        if(command.getName().equalsIgnoreCase("test")) {

            TranslationRegistry registry = TranslationRegistry.create(Key.key("namespace:value"));
            ResourceBundle bundle = ResourceBundle.getBundle("test", Locale.US, UTF8ResourceBundleControl.get());
            registry.registerAll(Locale.US, bundle, true);
            GlobalTranslator.translator().addSource(registry);

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
            NoBlockBreakRule noBlockBreakRule = new NoBlockBreakRule(this, rulesBundle, Set.of());
            sender.sendMessage("ABC");
            try {

                ChallengeManager challengeManager = new ChallengeManager();

                JsonNode schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
                MobGoal mobGoal = new MobGoal(new Context(this, bundle, schemaRoot, challengeManager), new HashMap<>(Map.of(EntityType.PIG, new Collect(2))));
                Player player = (Player) sender;
                mobGoal.openCollectedInv(player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        return true;
    }
}
