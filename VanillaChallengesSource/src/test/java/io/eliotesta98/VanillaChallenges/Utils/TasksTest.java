package io.eliotesta98.VanillaChallenges.Utils;

import com.HeroxWar.HeroxCore.TimeGesture.Date.Date;
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
        Assertions.assertEquals("Dyer", plugin.getDailyChallenge().getChallengeName());
    }

    @Test
    public void testNextChallengeAdjust() {
        plugin.getConfigGestion().setAdjustTime(true);
        Assertions.assertEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
        plugin.getConfigGestion().getTasks().setNow(new Date().sumBetween(new Date(':', 86400000)));
        serverMock.getScheduler().performTicks(20 * 60 * 60 * 24);
        Assertions.assertEquals("Dyer", plugin.getDailyChallenge().getChallengeName());
        Date now = new Date();
        Date endChallenge = now.cloneDate();
        String rowDate = endChallenge.getDate();
        rowDate = rowDate.substring(0, 11) + "23.59.59";
        endChallenge.setDate(rowDate);

        serverMock.getScheduler().performTicks(20 * 60 * 60 * 24);
        serverMock.getScheduler().performTicks(20 * 60 * 60 * 24);
        serverMock.getScheduler().performTicks(20 * 60 * 60 * 24);
        serverMock.getScheduler().performTicks(20 * 60 * 60 * 24);

        plugin.getConfigGestion().setAdjustTime(false);
    }

}