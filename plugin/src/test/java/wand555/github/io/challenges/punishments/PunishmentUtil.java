package wand555.github.io.challenges.punishments;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.types.Data;

import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PunishmentUtil {

    /**
     * Mock a data object where only the player actually exists. This may be used to test the behaviour of punishments
     * that don't involve the event and instead only need the player.
     * For example, {@link CancelPunishment} needs more information than just the player, but for most punishments this is enough.
     * @param causer The player who caused the punishment to trigger.
     * @return A mocked {@link Data} object. Only methods on the player are defined.
     * @param <E> Type of the event, unused in the mock.
     * @param <K> Type of the main interaction, unused in the mock.
     */
    public static <E extends Event, K> Data<E, K> mockData(Player causer) {
        Data<E, K> mockedData = mock(Data.class);
        when(mockedData.player()).thenReturn(causer);
        when(mockedData.playerUUID()).thenReturn(causer.getUniqueId());
        when(mockedData.playerName()).thenReturn(causer.getName());
        return mockedData;
    }

    public static <E extends Event & Cancellable, K> Data<E, K> mockDataWithEvent(Player causer) {
        Data<E, K> mockedData = mockData(causer);
        when(mockedData.event()).thenReturn((E) new CancellableEventStub());
        return mockedData;
    }

    private static class CancellableEventStub extends Event implements Cancellable {

        private boolean cancelled = false;

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new NotImplementedException();
        }
    }
}
