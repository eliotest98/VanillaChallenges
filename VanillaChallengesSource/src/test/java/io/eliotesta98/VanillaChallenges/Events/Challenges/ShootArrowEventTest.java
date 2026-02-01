package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.ConfigGestion;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShootArrowEventTest {

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

    private void changeConfig() {
        serverMock.getPluginManager().unregisterPluginEvents(plugin);
        serverMock.getPluginManager().registerEvents(new ShootArrowEvent(), plugin);
        Challenge daily = plugin.getDailyChallenge();
        List<String> worlds = new ArrayList<>();
        worlds.add(playerMock.getWorld().getName());
        daily.setWorlds(worlds);
        plugin.setDailyChallenge(daily);
        plugin.setConfigGestion(new ConfigGestion(plugin.getDataFolder().getPath(), "config.yml"));
        plugin.pluginStartingProcess();
    }

    private void invertDebug(int currentTime) {
        Map<String, Boolean> debugs = plugin.getConfigGestion().getDebug();
        if (currentTime % 2 == 0) {
            debugs.replace("ShootArrowEvent", true);
            plugin.getConfigGestion().setDebug(debugs);
        } else {
            debugs.replace("ShootArrowEvent", false);
            plugin.getConfigGestion().setDebug(debugs);
        }
    }

    @RepeatedTest(value = 2)
    public void testShootArrow(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        changeConfig();

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(playerMock,
                        new ItemStack(Material.BOW, 1), null, 1.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);
    }

    @RepeatedTest(value = 2)
    public void testShootArrowPlayerNull(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        changeConfig();

        LivingEntity skeleton = (LivingEntity) worldMock.spawnEntity(playerMock.getLocation(), EntityType.SKELETON);

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(skeleton,
                        new ItemStack(Material.BOW, 1), null, 1.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);

        skeleton.setHealth(-1);
    }

    @Test
    public void testShootArrowWithoutForce() {
        changeConfig();

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(playerMock,
                        new ItemStack(Material.BOW, 1), null, 0.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);
    }

    @Test
    public void testShootArrowOnGround() {
        changeConfig();

        plugin.getDailyChallenge().setOnGround("false");
        Controls.reload();

        playerMock.setOnGround(true);

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(playerMock,
                        new ItemStack(Material.BOW, 1), null, 1.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);

        plugin.getDailyChallenge().setOnGround("NOBODY");
        Controls.reload();
    }

    @Test
    public void testShootArrowWorld() {
        changeConfig();

        List<String> worlds = new ArrayList<>();
        worlds.add("a");
        plugin.getDailyChallenge().setWorlds(worlds);
        Controls.reload();

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(playerMock,
                        new ItemStack(Material.BOW, 1), null, 1.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);

        plugin.getDailyChallenge().setWorlds(new ArrayList<>());
        Controls.reload();
    }

    @Test
    public void testShootArrowHasPermission() {
        changeConfig();

        plugin.getConfigGestion().setPermissionPointsGive("points");
        Controls.reload();

        serverMock.getPluginManager().callEvent(
                new EntityShootBowEvent(playerMock,
                        new ItemStack(Material.BOW, 1), null, 1.0f));

        serverMock.getPluginManager().assertEventFired(EntityShootBowEvent.class);

        plugin.getConfigGestion().setPermissionPointsGive("");
        Controls.reload();
    }

}
