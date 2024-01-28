package wand555.github.io.challenges.rules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.punishments.Punishment;

import java.util.*;

public class NoBlockBreakRule extends PunishableRule implements Storable<NoBlockBreakRuleConfig>, Listener {

    private Set<Material> exemptions;

    public NoBlockBreakRule(Challenges plugin, ResourceBundle ruleResourceBundle, Set<Material> exemptions) {
        this(plugin, ruleResourceBundle, List.of(), exemptions);
    }

    public NoBlockBreakRule(Challenges plugin, ResourceBundle ruleResourceBundle, List<Punishment> punishments, Set<Material> exemptions) {
        super(plugin, ruleResourceBundle, punishments);
        this.exemptions = exemptions;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material broken = event.getBlock().getType();
        if(exemptions.contains(broken)) {
            return;
        }
        System.out.println(Component.translatable(String.format("block.minecraft.%s", broken.toString().toLowerCase())));
        //plugin.getServer().broadcast(Component.text("ABC"));
        String raw = rulesResourceBundle.getString("noblockbreak.violation");
        Component ruleViolatedMsg = MiniMessage.miniMessage().deserialize(
                raw,
                Placeholder.component("block", Component.translatable(String.format("block.minecraft.%s", broken.toString().toLowerCase())))
        );
        Component oneLine = MiniMessage.miniMessage().deserialize("<lang:block.minecraft.dirt> was broken");
        Component nested = MiniMessage.miniMessage().deserialize("<dirt> was broken", Placeholder.component("dirt", Component.translatable("block.minecraft.dirt")));
        //Component nested = Component.translatable("block.minecraft.dirt").append(Component.text(" was broken"));
        Component nested2 = MiniMessage.miniMessage().deserialize(
                "<lang:block.minecraft.dirt> was broken"
        );
        Component serialized = MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(nested));
        plugin.getServer().broadcast(oneLine);
        enforcePunishments(player);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NoBlockBreakRule that = (NoBlockBreakRule) o;
        return Objects.equals(exemptions, that.exemptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exemptions);
    }

    @Override
    public String toString() {
        return "NoBlockBreakRule{" +
                "exemptions=" + exemptions +
                ", punishments=" + punishments +
                ", plugin=" + plugin +
                ", rulesResourceBundle=" + rulesResourceBundle +
                '}';
    }

    @Override
    public NoBlockBreakRuleConfig toGeneratedJSONClass() {
        return new NoBlockBreakRuleConfig(
                exemptions.stream().map(Enum::toString).sorted().toList(), // always sort when moving from set to list
                toPunishmentsConfig()
        );
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoBlockBreak(toGeneratedJSONClass());
    }
}
