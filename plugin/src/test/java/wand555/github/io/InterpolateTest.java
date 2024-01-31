package wand555.github.io;

import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class InterpolateTest {



    @Test
    public void testInterpolate() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        //String plain = PlainTextComponentSerializer.plainText().serialize(result);
        //assertEquals("", MiniMessage.miniMessage().serialize(result));
    }
}
