package io.eliotesta98.VanillaChallenges.Commands;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.test.utils.PrintMessage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

public class CommandsTest {

    private static ServerMock serverMock;
    private static Main plugin;

    private PlayerMock playerMock;
    private PrintMessage printMessage;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        playerMock = serverMock.addPlayer();
        printMessage = new PrintMessage(serverMock);
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    @Test
    public void helpCommandWihoutPermissions() {
        playerMock.performCommand("vc help");
        List<String> messages = printMessage.getAllPlayerMessages(playerMock);
        System.out.println(messages);
        // Verify message contains expected parts without version check
        //Assertions.assertTrue(message.contains("VanillaChallenges"));
        //Assertions.assertTrue(message.contains("created by &a&leliotesta98 & xSavior_of_God"));
        //Assertions.assertTrue(message.contains("[] optional value, <> required"));
    }

    /*@Test
    public void helpCommandWithPermissions() {
        playerMock.setOp(true);
        playerMock.performCommand("vc help");
        String message = printMessage.getMessage(playerMock);
        System.out.println(message);
        // Verify message contains expected parts without version check
        Assertions.assertTrue(message.contains("VanillaChallenges"));
        Assertions.assertTrue(message.contains("created by &a&leliotesta98 & xSavior_of_God"));
        Assertions.assertTrue(message.contains("/vc &6<add|remove> <playerName> <points>"));
        Assertions.assertTrue(message.contains("/vc &6<challenge>"));
        Assertions.assertTrue(message.contains("/vc &6<clear>"));
        Assertions.assertTrue(message.contains("/vc &6<event> <stop|challenge|random> [time]"));
        Assertions.assertTrue(message.contains("/vc &6<list>"));
        Assertions.assertTrue(message.contains("/vc &6<next> [skip]"));
        Assertions.assertTrue(message.contains("/vc &6<points> [playerName]"));
        Assertions.assertTrue(message.contains("/vc &6<reload>"));
        Assertions.assertTrue(message.contains("/vc &6<reward>"));
        Assertions.assertTrue(message.contains("/vc &6<schedule> <add|remove|disable> [[challenge|random] [time]]"));
        Assertions.assertTrue(message.contains("/vc &6<time> <add|remove|set|remaining> [time]"));
        Assertions.assertTrue(message.contains("/vc &6<top> [yesterday]"));
        Assertions.assertTrue(message.contains("[] optional value, <> required"));
        playerMock.setOp(false);
    }

    @Test
    public void topCommandWithPermissions() {
        playerMock.setOp(true);
        playerMock.performCommand("vc top");
        String message = printMessage.getMessage(playerMock);
        // Verify message contains expected parts without version check
        Assertions.assertTrue(message.contains("VanillaChallenges"));
        Assertions.assertTrue(message.contains("created by &a&leliotesta98 & xSavior_of_God"));
        playerMock.setOp(false);
    }

    @Test
    public void topCommandWithPermissions2() {
        playerMock.setOp(true);
        plugin.getConfigGestion().setYesterdayTop(true);
        playerMock.performCommand("vc top");
        String message = printMessage.getMessage(playerMock);
        // Verify message contains expected parts without version check
        Assertions.assertTrue(message.contains("VanillaChallenges"));
        Assertions.assertTrue(message.contains("created by &a&leliotesta98 & xSavior_of_God"));
        playerMock.setOp(false);
    }*/

}
