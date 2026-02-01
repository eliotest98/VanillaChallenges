package io.eliotesta98.VanillaChallenges.Events.Challenges.Modules;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.*;

public class ControlsTest {

    private static ServerMock serverMock;
    private static Main plugin;

    private Challenge challenge;

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
        challenge = plugin.getDailyChallenge();
    }

    @AfterEach
    public void tearDown() {
        plugin.setDailyChallenge(challenge);
        Controls.reload();
        // Unmock Server and Plugin
        MockBukkit.unmock();
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
    public void testWorld(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> worlds = new ArrayList<>();
        worlds.add("world");
        challenge.setWorlds(worlds);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isWorldEnable("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setWorlds(new ArrayList<>());
    }

    @Test
    public void testWorldIfPresent() {
        List<String> worlds = new ArrayList<>();
        worlds.add("world");
        challenge.setWorlds(worlds);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isWorldEnable("world", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setWorlds(new ArrayList<>());
    }

    @Test
    public void testWorldFalse() {
        List<String> worlds = new ArrayList<>();
        challenge.setWorlds(worlds);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isWorldEnable("world", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setWorlds(new ArrayList<>());
    }

    @Test
    public void testIsSneakingNobody() {
        boolean result = Controls.isSneaking(playerMock.isSneaking(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsSneakingTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        challenge.setSneaking("true");
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isSneaking(playerMock.isSneaking(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setSneaking("NOBODY");
    }

    @Test
    public void testIsSneakingFalse() {
        challenge.setSneaking("false");
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isSneaking(playerMock.isSneaking(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setSneaking("NOBODY");
    }

    @Test
    public void isItemInHandEmpty() {
        boolean result = Controls.isItemInHand(playerMock.getItemInHand().getType().toString(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void isItemInHandTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> itemsInHand = new ArrayList<>();
        itemsInHand.add("DIRT");
        challenge.setItemsInHand(itemsInHand);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItemInHand(playerMock.getItemInHand().getType().toString(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setItemsInHand(new ArrayList<>());
    }

    @Test
    public void testIsItemInHandFalse() {
        List<String> itemsInHand = new ArrayList<>();
        itemsInHand.add("DIRT");
        challenge.setItemsInHand(itemsInHand);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItemInHand("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setItemsInHand(new ArrayList<>());
    }

    @RepeatedTest(value = 2)
    public void testIsItemInInventoryEmpty(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        boolean result = Controls.isItemInInventory(playerMock.getInventory(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsItemInInventoryFalse() {
        List<String> items = new ArrayList<>();
        items.add("DIRT");
        items.add("DIRT:0");
        challenge.setItems(items);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItemInInventory(playerMock.getInventory(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsItemInInventoryTrue() {
        List<String> items = new ArrayList<>();
        items.add("DIRTS");
        challenge.setItems(items);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItemInInventory(playerMock.getInventory(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsItemInInventoryTrueLegacy() {
        List<String> items = new ArrayList<>();
        items.add("DIRTS:0");
        challenge.setItems(items);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItemInInventory(playerMock.getInventory(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsItemInInventory() {
        List<String> items = new ArrayList<>();
        items.add("DIRT");
        challenge.setItems(items);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        playerMock.getInventory().addItem(new ItemStack(Material.DIRT, 2));

        boolean result = Controls.isItemInInventory(playerMock.getInventory(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsBlock() {
        boolean result = Controls.isBlock("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsBlockTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setBlocks(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isBlock("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setBlocks(new ArrayList<>());
    }

    @Test
    public void testIsBlockFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setBlocks(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isBlock("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setBlocks(new ArrayList<>());
    }

    @Test
    public void testIsBlockOnPlaced() {
        boolean result = Controls.isBlockOnPlaced("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);
    }

    @RepeatedTest(value = 2)
    public void testIsBlockOnPlaceTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setBlocksOnPlaced(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isBlockOnPlaced("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setBlocksOnPlaced(new ArrayList<>());
    }

    @Test
    public void testIsBlockOnPlaceFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setBlocksOnPlaced(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isBlockOnPlaced("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setBlocksOnPlaced(new ArrayList<>());
    }

    @Test
    public void testIsMob() {
        boolean result = Controls.isMob("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsMobTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("CREEPER");
        challenge.setMobs(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isMob("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setMobs(new ArrayList<>());
    }

    @Test
    public void testIsMobFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("CREEPER");
        challenge.setMobs(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isMob("CREEPER", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setMobs(new ArrayList<>());
    }

    @Test
    public void testIsCause() {
        boolean result = Controls.isCause("SUICIDE", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsCauseTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("SUICIDE");
        challenge.setCauses(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isCause("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setCauses(new ArrayList<>());
    }

    @Test
    public void testIsCauseFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("SUICIDE");
        challenge.setCauses(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isCause("SUICIDE", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setCauses(new ArrayList<>());
    }

    @Test
    public void testIsColor() {
        boolean result = Controls.isColor("RED", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsColorTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("RED");
        challenge.setColors(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isColor("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setColors(new ArrayList<>());
    }

    @Test
    public void testIsColorFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("RED");
        challenge.setColors(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isColor("RED", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setColors(new ArrayList<>());
    }

    @Test
    public void testIsItem() {
        boolean result = Controls.isItem("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);
    }

    @RepeatedTest(value = 2)
    public void testIsItemTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setItems(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItem("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsItemFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("DIRT");
        challenge.setItems(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isItem("DIRT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setItems(new ArrayList<>());
    }

    @Test
    public void testIsVehicle() {
        boolean result = Controls.isVehicle("BOAT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);
    }

    @RepeatedTest(value = 2)
    public void testIsVehicleTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        List<String> blocks = new ArrayList<>();
        blocks.add("BOAT");
        challenge.setVehicle(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isVehicle("a", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setVehicle(new ArrayList<>());
    }

    @Test
    public void testIsVehicleFalse() {
        List<String> blocks = new ArrayList<>();
        blocks.add("BOAT");
        challenge.setVehicle(blocks);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isVehicle("BOAT", plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setVehicle(new ArrayList<>());
    }

    @Test
    public void testIsForce() {
        challenge.setForce(0.0);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isForce(0.0, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);
    }

    @RepeatedTest(value = 2)
    public void testIsForceFalse(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        challenge.setForce(0.1);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isForce(0.0, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setForce(0.0);
    }

    @Test
    public void testIsForceFalse2() {
        challenge.setForce(0.1);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isForce(0.2, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setForce(0.0);
    }

    @Test
    public void testIsPower() {
        challenge.setPower(0.0);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isPower(0.0, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);
    }

    @RepeatedTest(value = 2)
    public void testIsPowerFalse(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        challenge.setPower(0.1);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isPower(0.0, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setPower(0.0);
    }

    @Test
    public void testIsPowerFalse2() {
        challenge.setPower(0.1);
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isPower(0.2, plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setPower(0.0);
    }

    @Test
    public void testIsOnGroundNobody() {
        boolean result = Controls.isOnGround(playerMock.isOnGround(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);
    }

    @RepeatedTest(value = 2)
    public void testIsOnGroundTrue(RepetitionInfo repetitionInfo) {
        invertDebug(repetitionInfo.getCurrentRepetition());
        challenge.setOnGround("true");
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isOnGround(playerMock.isOnGround(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertFalse(result);

        challenge.setOnGround("NOBODY");
    }

    @Test
    public void testIsOnGroundFalse() {
        challenge.setOnGround("false");
        plugin.setDailyChallenge(challenge);
        Controls.reload();

        boolean result = Controls.isOnGround(playerMock.isOnGround(), plugin.getConfigGestion().getDebug().get("ShootArrowEvent"), new DebugUtils(""), System.currentTimeMillis());
        Assertions.assertTrue(result);

        challenge.setOnGround("NOBODY");
    }

    @Test
    public void testPermissionEmpty() {
        boolean result = Controls.hasPermission(playerMock.getName());
        Assertions.assertTrue(result);
    }

    Map<UUID, PermissionAttachment> perms = new HashMap<>();

    private void setPermission(Player player) {
        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        perms.put(player.getUniqueId(), permissionAttachment);

        PermissionAttachment pperms = perms.get(player.getUniqueId());
        pperms.setPermission("permission", true);
    }

    private void unsetPermission(Player player) {
        perms.get(player.getUniqueId()).unsetPermission("permission");
    }

    @Test
    public void testPermission() {
        plugin.getConfigGestion().setPermissionPointsGive("permission");
        setPermission(playerMock);
        Controls.reload();

        boolean result = Controls.hasPermission(playerMock.getName());
        Assertions.assertTrue(result);

        plugin.getConfigGestion().setPermissionPointsGive("");
        unsetPermission(playerMock);
    }

    @Test
    public void testPermissionFalse() {
        plugin.getConfigGestion().setPermissionPointsGive("permission");
        Controls.reload();

        boolean result = Controls.hasPermission(playerMock.getName());
        Assertions.assertFalse(result);

        plugin.getConfigGestion().setPermissionPointsGive("");
    }

}
