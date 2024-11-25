package wand555.github.io.challenges.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.generated.PlayerConfig;

import java.util.UUID;

public class LiveUtil {

    public static PlayerConfig constructPlayerConfig(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();
        String playerName = playerProfile.getName() != null ? playerProfile.getName() : "?";
        String skinTextureURL = playerProfile.getTextures().getSkin() != null ? playerProfile.getTextures().getSkin().toString() : "http://textures.minecraft.net/texture/5163dafac1d91a8c91db576caac784336791a6e18d8f7f62778fc47bf146b6";
        return new PlayerConfig(
            playerName,
            uuid.toString(),
            skinTextureURL
        );
    }
}
