package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeathMessage implements DataSourceJSON<DeathMessage>, Keyed, Translatable {

    private final String key;
    private final String message;
    private final String messageWithDummyData;
    private final Pattern pattern;

    public DeathMessage(@JsonProperty("code") String key, @JsonProperty("deathMessage") String message, @JsonProperty("deathMessageWithDummyData") String messageWithDummyData) {
        this.key = key;
        this.message = message;
        this.messageWithDummyData = messageWithDummyData;
        this.pattern = Pattern.compile(message);
    }

    @Deprecated
    public Matcher getMatcherFor(String deathMessageFromEvent) {
        // message from event contains placeholders

        return pattern.matcher(deathMessageFromEvent);
    }

    public String getMessage() {
        return message;
    }

    public String getMessageWithDummyData() {
        return messageWithDummyData;
    }

    @Override
    public DeathMessage toEnum() {
        return this;
    }

    @Override
    public String getCode() {
        return key;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey("challenges", getCode());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        DeathMessage that = (DeathMessage) o;
        return Objects.equals(key, that.key) && Objects.equals(message, that.message) && Objects.equals(
                messageWithDummyData,
                that.messageWithDummyData
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, message, messageWithDummyData);
    }

    @Override
    public String toString() {
        return "DeathMessage{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                ", messageWithDummyData='" + messageWithDummyData + '\'' +
                '}';
    }

    @Override
    public @NotNull String translationKey() {
        return getCode();
    }
}
