package wand555.github.io.challenges.criteria.settings;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CustomHealthSettingConfig;
import wand555.github.io.challenges.generated.SettingsConfig;

public class CustomHealthSetting extends BaseSetting implements Storable<CustomHealthSettingConfig>, Listener {

    private final int hearts;

    public CustomHealthSetting(Context context, CustomHealthSettingConfig config) {
        super(context);
        this.hearts = config.getHearts();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @Override
    public void addToGeneratedConfig(SettingsConfig config) {
        config.setCustomHealthSetting(toGeneratedJSONClass());
    }

    @Override
    public CustomHealthSettingConfig toGeneratedJSONClass() {
        return new CustomHealthSettingConfig(hearts);
    }

    private void setCustomHeartsForAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::setCustomHeartFor);
    }

    private void setCustomHeartFor(Player player) {
        setCustomHeartForWith(player, hearts);
    }

    private void setCustomHeartForWith(Player player, int amount) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealth.setBaseValue(amount);
        player.setHealthScale(player.getHealth());
        player.damage(0);
    }

    @Override
    public void onStart() {
        setCustomHeartsForAllPlayers();
    }

    @Override
    public void onEnd() {
        Bukkit.getOnlinePlayers().forEach(player -> setCustomHeartForWith(player,
                                                                          20
        )); // set health back to 20 (default)
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!context.challengeManager().isSetup()) {
            setCustomHeartFor(event.getPlayer());
        }
    }
}
