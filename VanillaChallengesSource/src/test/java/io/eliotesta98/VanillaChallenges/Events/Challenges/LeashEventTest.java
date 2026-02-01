package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.ConfigGestion;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeashEventTest {

    private static ServerMock serverMock;
    private static Main plugin;

    // Fake Instances
    private PlayerMock playerMock;
    private WorldMock worldMock;
    private Challenge daily = null;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        playerMock = serverMock.addPlayer();
        worldMock = playerMock.getWorld();
        goToChallenge("Leasher");
        Assertions.assertEquals("Leasher", daily.getChallengeName());
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    private void changeConfig() {
        serverMock.getPluginManager().unregisterPluginEvents(plugin);
        serverMock.getPluginManager().registerEvents(new LeashEvent(), plugin);
        plugin.setDailyChallenge(daily);
        plugin.setConfigGestion(new ConfigGestion(plugin.getDataFolder().getPath(), "config.yml"));
        plugin.pluginStartingProcess();
    }

    private void invertDebug(int currentTime) {
        Map<String, Boolean> debugs = plugin.getConfigGestion().getDebug();
        if (currentTime % 2 == 0) {
            debugs.replace("LeashEvent", true);
            plugin.getConfigGestion().setDebug(debugs);
        } else {
            debugs.replace("LeashEvent", false);
            plugin.getConfigGestion().setDebug(debugs);
        }
    }

    private void goToChallenge(String challenge) {
        if (daily != null) {
            return;
        }
        List<Challenge> challenges = plugin.db.getChallenges();
        while (!challenges.isEmpty()) {
            //System.out.println(challenges.get(0).getChallengeName());
            plugin.getConfigGestion().setTimeBroadcastMessageTitle(0);
            plugin.getConfigGestion().setActiveOnlinePoints(true);
            plugin.challengeSelected = false;
            plugin.getDailyChallenge().nextChallenge(
                    false, false,
                    false, 3,
                    3, "", true);
            if (challenges.get(0).getChallengeName().equalsIgnoreCase(challenge)) {
                daily = plugin.getDailyChallenge();
                plugin.getConfigGestion().getTasks().setChallengeStart(true);
                return;
            }
            challenges.remove(0);
            Assertions.assertTrue(plugin.challengeSelected);
        }
    }

    @RepeatedTest(value = 2)
    public void testLeash(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        LivingEntity cow = (LivingEntity) worldMock.spawnEntity(playerMock.getLocation(), EntityType.COW);

        serverMock.getPluginManager().callEvent(
                new PlayerLeashEntityEvent(cow,
                        null, playerMock));

        serverMock.getPluginManager().assertEventFired(
                PlayerLeashEntityEvent.class);

        cow.setHealth(-1);

        serverMock.getScheduler().performTicks(20 * 500);
        List<Challenger> challengers = plugin.db.getPlayerPoints();
        long points = daily.getPointFromPLayerName(playerMock.getName());
        if (points != 0) {
            Assertions.assertEquals(1, points);
        }
        plugin.db.clearChallengers();
    }

    @RepeatedTest(value = 2)
    public void testLeashCow(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        LivingEntity cow = (LivingEntity) worldMock.spawnEntity(playerMock.getLocation(), EntityType.COW);

        List<String> mobs = new ArrayList<>();
        mobs.add("cow");
        plugin.getDailyChallenge().setMobs(mobs);
        Controls.reload();
        changeConfig();

        serverMock.getPluginManager().callEvent(
                new PlayerLeashEntityEvent(cow,
                        null, playerMock));

        serverMock.getPluginManager().assertEventFired(
                PlayerLeashEntityEvent.class);

        cow.setHealth(-1);
        plugin.getDailyChallenge().setMobs(new ArrayList<>());
    }

    @RepeatedTest(value = 2)
    public void testLeashNotCow(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());

        LivingEntity cow = (LivingEntity) worldMock.spawnEntity(playerMock.getLocation(), EntityType.COW);

        List<String> mobs = new ArrayList<>();
        mobs.add("boat");
        plugin.getDailyChallenge().setMobs(mobs);
        Controls.reload();
        changeConfig();

        serverMock.getPluginManager().callEvent(
                new PlayerLeashEntityEvent(cow,
                        null, playerMock));

        serverMock.getPluginManager().assertEventFired(
                PlayerLeashEntityEvent.class);

        cow.setHealth(-1);
        plugin.getDailyChallenge().setMobs(new ArrayList<>());
    }

}
