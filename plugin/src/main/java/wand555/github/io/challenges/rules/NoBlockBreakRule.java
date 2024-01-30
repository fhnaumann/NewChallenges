package wand555.github.io.challenges.rules;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.Audiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.ComponentInterpolator;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.punishments.Punishment;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NoBlockBreakRule extends PunishableRule implements Storable<NoBlockBreakRuleConfig>, Listener {

    private final Set<Material> exemptions;

    public NoBlockBreakRule(Context context, NoBlockBreakRuleConfig config) {
        super(context, ModelMapper.mapToPunishments(context, config.getPunishments()));
        this.exemptions = new HashSet<>(ModelMapper.str2Mat(config.getExemptions(), ModelMapper.VALID_BLOCKS));
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material broken = event.getBlock().getType();
        if(exemptions.contains(broken)) {
            return;
        }
        Component toSend = ComponentInterpolator.interpolate(
                context.plugin(),
                context.resourceBundleContext().ruleResourceBundle(),
                "noblockbreak.violation",
                Map.of(
                        "player", Component.text(player.getName()),
                        "block", Component.translatable(String.format("block.minecraft.%s", broken.toString().toLowerCase()))
                )
        );
        context.plugin().getServer().broadcast(toSend);
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
