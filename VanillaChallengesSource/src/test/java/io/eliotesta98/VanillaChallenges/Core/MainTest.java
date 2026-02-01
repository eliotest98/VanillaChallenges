package io.eliotesta98.VanillaChallenges.Core;

import com.HeroxWar.HeroxCore.TimeGesture.Time;
import io.eliotesta98.VanillaChallenges.Database.ConfigGestion;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.List;
import java.util.Map;

public class MainTest {

    private static ServerMock serverMock;
    private static Main plugin;

    // Fake Instances
    private PlayerMock playerMock;
    private WorldMock worldMock;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        playerMock = serverMock.addPlayer();
        worldMock = playerMock.getWorld();
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    private void invertDebug(int currentTime) {
        Map<String, Boolean> debugs = plugin.getConfigGestion().getDebug();
        if (currentTime % 2 == 0) {
            debugs.replace("Enabled", true);
            debugs.replace("Disabled", true);
            plugin.getConfigGestion().setDebug(debugs);
        } else {
            debugs.replace("Enabled", false);
            debugs.replace("Disabled", false);
            plugin.getConfigGestion().setDebug(debugs);
        }
    }

    @RepeatedTest(value = 2)
    public void onEnable(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        plugin.onEnable();
        serverMock.getScheduler().performTicks(40);
        Assertions.assertNotNull(plugin.getDailyChallenge());
    }

    @Test
    public void onEnableNext() {
        List<Challenge> challenges = plugin.db.getChallenges();
        while (!challenges.isEmpty()) {
            plugin.getConfigGestion().setTimeBroadcastMessageTitle(0);
            plugin.getConfigGestion().setActiveOnlinePoints(true);
            plugin.challengeSelected = false;
            plugin.getDailyChallenge().nextChallenge(
                    false, false,
                    false, 3,
                    3, "", true);
            challenges.remove(0);
            Assertions.assertTrue(plugin.challengeSelected);
        }

        plugin.getConfigGestion().setCooldown(new Time(20, ':'));
        plugin.getDailyChallenge().nextChallenge(
                false, false,
                false, 3,
                3, "", false);

        plugin.getConfigGestion().setTimeBroadcastMessageTitle(10);
        plugin.getConfigGestion().setActiveOnlinePoints(false);
        plugin.db.setPeacefulTime(new Time(-1, ':'));
    }

    @Test
    public void onEnableWithDependencies() {
        Map<String, Boolean> hooks = plugin.getConfigGestion().getHooks();
        for (String hook : hooks.keySet()) {
            hooks.replace(hook, true);
            plugin.getConfigGestion().setHooks(hooks);
            MockBukkit.createMockPlugin(hook);
            plugin.onEnable();
            try {
                serverMock.getScheduler().performTicks(40);
            } catch (NullPointerException ignore) {

            }
            hooks.replace(hook, false);
            plugin.getConfigGestion().setHooks(hooks);

            plugin.onEnable();
            serverMock.getScheduler().performTicks(40);
        }
    }

    @Test
    public void onDisableWithPAPI() {
        Map<String, Boolean> hooks = plugin.getConfigGestion().getHooks();
        hooks.replace("PlaceholderAPI", true);
        plugin.onDisable();
    }

    @Test
    public void pluginStartingProcessWithDependencies() {
        int pre = plugin.getConfigGestion().getChallenges().size();
        MockBukkit.createMockPlugin("CubeGenerator");
        MockBukkit.createMockPlugin("SuperiorSkyblock2");
        Map<String, Boolean> hooks = plugin.getConfigGestion().getHooks();
        hooks.replace("CubeGenerator", true);
        hooks.replace("SuperiorSkyblock2", true);
        plugin.getConfigGestion().setHooks(hooks);
        plugin.db.clearChallenges();
        plugin.setConfigGestion(new ConfigGestion(plugin.getDataFolder().getPath(), "config.yml"));
        plugin.pluginStartingProcess();
        Assertions.assertEquals(pre + 2, plugin.getConfigGestion().getChallenges().size());

        List<Challenge> challenges = plugin.db.getChallenges();
        while (!challenges.isEmpty()) {
            plugin.getConfigGestion().setTimeBroadcastMessageTitle(0);
            plugin.getConfigGestion().setActiveOnlinePoints(true);
            plugin.challengeSelected = false;
            plugin.getDailyChallenge().nextChallenge(
                    false, false,
                    false, 3,
                    3, "", true);
            challenges.remove(0);
            Assertions.assertTrue(plugin.challengeSelected);
        }
        plugin.getDailyChallenge().nextChallenge(
                false, false,
                false, 3,
                3, "", false);
        challenges = plugin.db.getChallenges();
        while (!challenges.isEmpty()) {
            plugin.getConfigGestion().setTimeBroadcastMessageTitle(0);
            plugin.getConfigGestion().setActiveOnlinePoints(true);
            plugin.challengeSelected = false;
            plugin.getDailyChallenge().nextChallenge(
                    false, false,
                    false, 3,
                    3, "", true);
            challenges.remove(0);
            Assertions.assertTrue(plugin.challengeSelected);
        }
    }

    @Test
    public void schedulerNothing() {
        plugin.getConfigGestion().setChallengeGeneration("Nothing");
        plugin.setConfigGestion(new ConfigGestion(plugin.getDataFolder().getPath(), "config.yml"));
        plugin.pluginStartingProcess();
        Assertions.assertEquals(0, plugin.db.getChallenges().size());
    }

    @Test
    public void schedulerSingle() {
        plugin.getConfigGestion().setChallengeGeneration("Single");
        plugin.setConfigGestion(new ConfigGestion(plugin.getDataFolder().getPath(), "config.yml"));
        plugin.pluginStartingProcess();
        Assertions.assertEquals(0, plugin.db.getChallenges().size());
        plugin.getDailyChallenge().nextChallenge(
                false, false,
                false, 3,
                3, "", true);
    }

    @Test
    public void restoreDb() {
        plugin.restoreDatabase();
        plugin.restoreDatabase();
    }

    @Test
    public void mySqlDb() {
        plugin.getConfigGestion().setDatabase("MySql");
        plugin.onEnable();
    }

    @Test
    public void yamlDB() {
        plugin.getConfigGestion().setDatabase("YamlDB");
        plugin.onEnable();
    }

}
