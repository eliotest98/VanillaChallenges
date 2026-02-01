package io.eliotesta98.VanillaChallenges.Utils;

import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChallengeTest {

    private static ServerMock serverMock;
    private static Main plugin;

    // Fake Instances
    private PlayerMock playerMock;
    private WorldMock worldMock;

    private Challenge currentChallenge;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        playerMock = serverMock.addPlayer();
        worldMock = playerMock.getWorld();
        currentChallenge = plugin.getDailyChallenge();
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    @Test
    public void testNewChallenge() {
        currentChallenge = new Challenge();
        Assertions.assertEquals("nessuna", currentChallenge.getChallengeName());
    }

    @Test
    public void testNewChallengeWithCustomTime() {
        currentChallenge = currentChallenge.cloneChallenge("1h");
        Assertions.assertEquals(new Time(0, 1, 0, 0, ':'), currentChallenge.getTimeChallenge());
        currentChallenge = currentChallenge.cloneChallenge("1m");
        Assertions.assertEquals(new Time(0, 0, 1, 0, ':'), currentChallenge.getTimeChallenge());
        currentChallenge = currentChallenge.cloneChallenge("1s");
        Assertions.assertEquals(new Time(0, 0, 0, 1, ':'), currentChallenge.getTimeChallenge());
        currentChallenge = currentChallenge.cloneChallenge("1t");
        Assertions.assertEquals(new Time(1, ':'), currentChallenge.getTimeChallenge());
        currentChallenge.addTime(new Time(1, ':'));
        Assertions.assertEquals(new Time(2, ':'), currentChallenge.getTimeChallenge());
        currentChallenge.removeTime(new Time(1, ':'));
        Assertions.assertEquals(new Time(1, ':'), currentChallenge.getTimeChallenge());
        currentChallenge = currentChallenge.cloneChallenge("19h");
    }

    @Test
    public void testEmptyTop() {
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Assertions.assertTrue(challengers.isEmpty());
    }

    @Test
    public void testTwoInTops() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.incrementCommands("eliotesta98", 1);
        currentChallenge.incrementCommands("xSavior_of_God", 1);
        currentChallenge.incrementCommands("eliotesta98", 1);

        List<Challenger> challengers = currentChallenge.getTopPlayers(2);
        Assertions.assertEquals(2, challengers.get(0).getPoints());
        Assertions.assertEquals("eliotesta98", challengers.get(0).getNomePlayer());
        Assertions.assertEquals(1, challengers.get(1).getPoints());
        Assertions.assertEquals("xSavior_of_God", challengers.get(1).getNomePlayer());

        currentChallenge.clearPlayers();
    }

    @Test
    public void testIncrementPoints() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.incrementCommands("eliotesta98", 1);

        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Assertions.assertEquals(1, challengers.get(0).getPoints());

        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.incrementCommands("eliotesta98", 1);
        challengers = currentChallenge.getTopPlayers(1);
        Assertions.assertEquals(1, challengers.get(0).getPoints());

        currentChallenge.clearPlayers();
    }

    @Test
    public void isMinimumPoints() {
        Assertions.assertTrue(currentChallenge.isMinimumPointsReached());

        plugin.getConfigGestion().setMinimumPoints(100);
        plugin.reloadConfig();
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.incrementCommands("eliotesta98", 10);
        currentChallenge.incrementCommands("xSavior_of_God", 20);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);

        Assertions.assertFalse(currentChallenge.isMinimumPointsReached());

        currentChallenge.clearPlayers();
        plugin.getConfigGestion().setMinimumPoints(0);
        plugin.reloadConfig();
    }

    @Test
    public void incrementPointsNoCommand() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.increment("eliotesta98", 1);

        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 1);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(2, challengers.get(0).getPoints());
        Assertions.assertEquals(2, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(2, boostSinglePlayers.get("eliotesta98"));

        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    private void firstIncrement() {
        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));
    }

    @Test
    public void singleBoost() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setBoostMinutes(3);

        firstIncrement();

        currentChallenge.increment("eliotesta98", 9);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(10, challengers.get(0).getPoints());
        Assertions.assertEquals(10, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        currentChallenge.setMultiplier(2);
        currentChallenge.increment("eliotesta98", 10);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(30, challengers.get(0).getPoints());
        Assertions.assertEquals(30, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("xSavior_of_God", 10);
        challengers = currentChallenge.getTopPlayers(2);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(20, challengers.get(1).getPoints());
        Assertions.assertEquals(20, min10PlayersPoints.get("xSavior_of_God"));
        Assertions.assertNull(boostSinglePlayers.get("xSavior_of_God"));

        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void failedSingleBoost() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(2);

        firstIncrement();

        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void failedSingleBoostPoints() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        currentChallenge.setPointsBoost(1);

        firstIncrement();

        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        currentChallenge.setPointsBoost(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void failedSingleBoostPoints2() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        firstIncrement();

        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        currentChallenge.setPointsBoost(10);
        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(2, challengers.get(0).getPoints());
        Assertions.assertEquals(2, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(2, boostSinglePlayers.get("eliotesta98"));

        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void pointsMultiplierSinglePlayer() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        currentChallenge.setMinutesSinglePlayer(1);

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 1);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(2, challengers.get(0).getPoints());
        Assertions.assertEquals(2, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("xSavior_of_God", 1);
        challengers = currentChallenge.getTopPlayers(2);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(1).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("xSavior_of_God"));
        Assertions.assertNull(boostSinglePlayers.get("xSavior_of_God"));

        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void pointsMultiplierSinglePlayer2() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(2);
        currentChallenge.setPointsBoostSinglePlayer(0);

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 10);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(21, challengers.get(0).getPoints());
        Assertions.assertEquals(21, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(1);
        currentChallenge.setPointsBoostSinglePlayer(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void pointsMultiplierSinglePlayer3() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(1);
        currentChallenge.setPointsBoostSinglePlayer(1);

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 10);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(11, challengers.get(0).getPoints());
        Assertions.assertEquals(11, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(1);
        currentChallenge.setPointsBoostSinglePlayer(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void pointsMultiplierSinglePlayer4() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(1);
        currentChallenge.setPointsBoostSinglePlayer(10);

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 10);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(11, challengers.get(0).getPoints());
        Assertions.assertEquals(11, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        currentChallenge.setMinutesSinglePlayer(0);
        currentChallenge.setMultiplierSinglePlayer(1);
        currentChallenge.setPointsBoostSinglePlayer(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
        currentChallenge.clearPlayers();
    }

    @Test
    public void isActive() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        Assertions.assertTrue(currentChallenge.isActive());

        currentChallenge.setBoostMinutes(1);
        Assertions.assertTrue(currentChallenge.isActive());

        currentChallenge.setMultiplier(2);
        Assertions.assertTrue(currentChallenge.isActive());

        currentChallenge.setPointsBoost(1);
        Assertions.assertFalse(currentChallenge.isActive());

        currentChallenge.setBoostMinutes(0);
        currentChallenge.setMultiplier(1);
        currentChallenge.setPointsBoost(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void isActiveStartBoost() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        currentChallenge.setBoostMinutes(1);

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        currentChallenge.increment("eliotesta98", 10);
        challengers = currentChallenge.getTopPlayers(1);
        min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(11, challengers.get(0).getPoints());
        Assertions.assertEquals(11, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertEquals(1, boostSinglePlayers.get("eliotesta98"));

        Assertions.assertTrue(currentChallenge.isActive());

        currentChallenge.setBoostMinutes(0);
        currentChallenge.clearPlayers();
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void isActiveSingleBoost() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setMinutesSinglePlayer(2);
        Assertions.assertFalse(currentChallenge.isActiveSingleBoost("eliotesta98"));

        currentChallenge.increment("eliotesta98", 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get("eliotesta98"));
        Assertions.assertNull(boostSinglePlayers.get("eliotesta98"));

        Assertions.assertTrue(currentChallenge.isActiveSingleBoost("eliotesta98"));

        currentChallenge.setMinutesSinglePlayer(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void testStartBoostingSinglePlayerTask() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setMinutesSinglePlayer(2);

        currentChallenge.increment(playerMock.getName(), 1);
        List<Challenger> challengers = currentChallenge.getTopPlayers(1);
        Map<String, Long> min10PlayersPoints = currentChallenge.getMin10PlayersPoints();
        Map<String, Long> boostSinglePlayers = currentChallenge.getBoostSinglePlayers();
        Assertions.assertEquals(1, challengers.get(0).getPoints());
        Assertions.assertEquals(1, min10PlayersPoints.get(playerMock.getName()));
        Assertions.assertNull(boostSinglePlayers.get(playerMock.getName()));

        currentChallenge.increment("playerMock.getName()", 1);
        serverMock.getScheduler().performTicks(20 * 70);

        currentChallenge.setMinutesSinglePlayer(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void startBoostingTask() {
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        currentChallenge.setBoostMinutes(2);

        firstIncrement();
        currentChallenge.increment("eliotesta98", 10);

        serverMock.getScheduler().performTicks(20 * 120);

        currentChallenge.setBoostMinutes(0);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void nextChallenge() {
        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, false, false, 3, 3, "", true);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());
    }

    @Test
    public void nextChallenge2() {
        plugin.getConfigGestion().setBackupEnabled(false);
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        plugin.getDailyChallenge().incrementCommands("eliotesta98", 100);
        plugin.getDailyChallenge().incrementCommands("xSavior_of_God", 100);

        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(true, false,
                false, 3, 3,
                "", true);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());

        plugin.getConfigGestion().setBackupEnabled(true);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void nextChallenge3() {
        plugin.getConfigGestion().setBackupEnabled(false);
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        plugin.getDailyChallenge().incrementCommands("eliotesta98", 100);
        plugin.getDailyChallenge().incrementCommands("xSavior_of_God", 100);
        plugin.getDailyChallenge().incrementCommands("DarkSonic88", 100);
        plugin.getDailyChallenge().incrementCommands("miki28", 1);
        plugin.getDailyChallenge().incrementCommands("pippo", -1);
        serverMock.getScheduler().performTicks(20 * 240);

        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, true,
                false, 4, 3,
                "", true);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, true,
                false, 4, 3,
                "", true);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("EggThrower", currentChallenge.getChallengeName());

        currentChallenge.clearPlayers();
        plugin.getConfigGestion().setBackupEnabled(true);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void nextChallengeRandomReward() {
        plugin.getConfigGestion().setBackupEnabled(false);
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        plugin.getConfigGestion().setCooldown(new Time(20, ':'));
        plugin.getDailyChallenge().incrementCommands("eliotesta98", 100);

        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, false,
                true, 4, 3,
                "", false);
        serverMock.getScheduler().performTicks(20 * 240);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, false,
                true, 4, 3,
                "", true);

        plugin.getConfigGestion().setCooldown(new Time(-1, ':'));
        currentChallenge.clearPlayers();
        plugin.getConfigGestion().setBackupEnabled(true);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void nextChallengeNotQuota() {
        plugin.getConfigGestion().setBackupEnabled(false);
        plugin.getConfigGestion().getTasks().setChallengeStart(true);
        plugin.getConfigGestion().setMinimumPoints(100);

        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, false,
                true, 4, 3,
                "", false);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());

        plugin.getConfigGestion().setMinimumPoints(0);
        plugin.getConfigGestion().setBackupEnabled(true);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void nextChallengeEvent() {
        plugin.getConfigGestion().setBackupEnabled(false);
        plugin.getConfigGestion().getTasks().setChallengeStart(true);

        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());
        currentChallenge.nextChallenge(false, false,
                false, 4, 3,
                "", false);
        String challengeName = currentChallenge.getChallengeName();
        plugin.db.insertChallengeEvent(challengeName, 10000);

        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Dyer", currentChallenge.getChallengeName());

        plugin.getDailyChallenge().nextChallenge(
                false, false,
                false, 3,
                3, "", true);
        currentChallenge = plugin.getDailyChallenge();
        Assertions.assertEquals("Shooter", currentChallenge.getChallengeName());

        plugin.getConfigGestion().setBackupEnabled(true);
        plugin.getConfigGestion().getTasks().setChallengeStart(false);
    }

    @Test
    public void testGetterAndSetter() {
        Assertions.assertEquals(1, plugin.getDailyChallenge().getMultiplierSinglePlayer());
        Assertions.assertTrue(plugin.getDailyChallenge().getPlayers().isEmpty());
        plugin.getDailyChallenge().setQuests(new ArrayList<>());
        plugin.getDailyChallenge().setPoint(1);
        Assertions.assertEquals("Shooter", plugin.getDailyChallenge().getChallengeName());
        Assertions.assertEquals("ARROW", plugin.getDailyChallenge().getItemChallenge());
        Assertions.assertEquals("&7Shoot Challenge", plugin.getDailyChallenge().getNameChallenge());
        Assertions.assertEquals(0, plugin.getDailyChallenge().getPointFromPLayerName("eliotesta98"));
        plugin.getDailyChallenge().setRewards(plugin.getDailyChallenge().getRewards());
        plugin.getDailyChallenge().setTitle(plugin.getDailyChallenge().getTitle());
        plugin.getDailyChallenge().setItems(plugin.getDailyChallenge().getItems());
        plugin.getDailyChallenge().setItemsInHand(plugin.getDailyChallenge().getItemsInHand());
        plugin.getDailyChallenge().setMobs(plugin.getDailyChallenge().getMobs());
        plugin.getDailyChallenge().setMultiplier(plugin.getDailyChallenge().getMultiplier());
        plugin.getDailyChallenge().setPointsBoost(plugin.getDailyChallenge().getPointsBoost());
        plugin.getDailyChallenge().setMinutes(plugin.getDailyChallenge().getMinutes());
        Assertions.assertEquals(0, plugin.getDailyChallenge().getCountPointsChallenge());
        plugin.getDailyChallenge().setNumber(plugin.getDailyChallenge().getNumber());
        plugin.getDailyChallenge().setKeepInventory(plugin.getDailyChallenge().isKeepInventory());
        plugin.getDailyChallenge().setMinutesSinglePlayer(plugin.getDailyChallenge().getMinutesSinglePlayer());
        Assertions.assertNotNull(plugin.getDailyChallenge().toString());
    }

}
