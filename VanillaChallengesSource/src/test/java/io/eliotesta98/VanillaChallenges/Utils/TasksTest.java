package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.test.utils.PrintMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public class TasksTest {

    private static ServerMock serverMock;
    private static Main plugin;

    private PrintMessage printMessage;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        printMessage = new PrintMessage(serverMock);
        setChallengesStartTimeTo00();
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    public void setChallengesStartTimeTo00() {
        for(Challenge challenge: Main.instance.getConfigGestion().getChallenges().values()) {
            challenge.setStartTimeChallenge("00:01");
        }
    }

    @Test
    public void testNextChallenge() {
        Assertions.assertEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
        serverMock.getScheduler().performTicks(20 * 60 * 60 * 20);
        Assertions.assertFalse(plugin.getConfigGestion().getTasks().isChallengeStart());
        Assertions.assertNotEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
    }

    @Test
    public void testNextChallengeAdjust() {
        plugin.getConfigGestion().setAdjustTime(true);
        Assertions.assertEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
        serverMock.getScheduler().performTicks(20 * 60 * 5);
        plugin.getDailyChallenge().setEndTimeChallenge("08:00");
        serverMock.getScheduler().performTicks(20 * 4);
        Assertions.assertNotEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
        plugin.getConfigGestion().setAdjustTime(false);
        plugin.getDailyChallenge().setEndTimeChallenge("23:59");
    }

}