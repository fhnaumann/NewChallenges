package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;

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
        // command for skipping
        else if(command.getName().equalsIgnoreCase("skip")) {

        }
        if(command.getName().equalsIgnoreCase("test")) {
            Bukkit.broadcast(Component.text("123"));
            Bukkit.broadcast(Component.translatable("minecraft.mineable.axe"));
            Bukkit.broadcast(Component.translatable("item.minecraft.carrot"));

            Bukkit.clearRecipes();


            NamespacedKey recipeKey = new NamespacedKey(this, "testrecipe");
            ItemStack itemStack = new ItemStack(Material.CARROT);
            //markItemStack(itemStack);
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeKey, itemStack);
            ItemStack ingredient = new ItemStack(Material.APPLE);
            //markItemStack(ingredient);
            shapelessRecipe.addIngredient(ingredient);

            Player player = ((Player) sender);
            boolean discovered = player.discoverRecipe(recipeKey);
            Bukkit.broadcastMessage("discovered? " + discovered);
            Bukkit.broadcastMessage("globally discovered" + Bukkit.addRecipe(shapelessRecipe));
            player.getDiscoveredRecipes().stream().filter(namespacedKey -> !namespacedKey.getNamespace().equals("minecraft")).forEach(namespacedKey -> Bukkit.broadcastMessage(namespacedKey.asString()));

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
}
