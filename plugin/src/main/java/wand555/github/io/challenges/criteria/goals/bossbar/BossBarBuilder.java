package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.ComponentUtil;

import java.util.ArrayList;
import java.util.List;

public class BossBarBuilder {

    private final List<BossBarPart<?>> parts = new ArrayList<>();

    public BossBarBuilder parts(List<BossBarPart<?>> parts) {
        parts.forEach(this::then);
        return this;
    }

    public BossBarBuilder then(BossBarPart<?> part) {
        parts.add(part);
        return this;
    }

    public BossBar build() {
        return BossBar.bossBar(buildName(), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public Component buildName() {
        return parts.stream()
                    .map(BossBarPart::buildPart)
                    .reduce(Component.empty(), ComponentUtil.SPACE_ACCUMULATOR);
    }

    public float buildProgress() {
        List<BuildProgress> buildProgresses = this.parts.stream().filter(BuildProgress.class::isInstance).map(
                BuildProgress.class::cast).toList();
        if(buildProgresses.isEmpty()) {
            return 1f;
        }
        if(buildProgresses.size() > 1) {
            JavaPlugin.getPlugin(Challenges.class).getComponentLogger().warn(
                    "Encountered a BossBar where more than one part sets a progress value.");
        }
        return buildProgresses.get(0).buildProgress();
    }

    public List<BossBarPart<?>> getParts() {
        return parts;
    }
}
