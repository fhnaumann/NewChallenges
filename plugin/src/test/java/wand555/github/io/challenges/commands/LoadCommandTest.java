package wand555.github.io.challenges.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.Challenges;

public class LoadCommandTest {

    private ServerMock server;
    private Challenges plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void test() {

    }
}
