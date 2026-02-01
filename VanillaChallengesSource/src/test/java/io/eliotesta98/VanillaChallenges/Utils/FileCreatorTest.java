package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileCreatorTest {

    private static ServerMock serverMock;
    private static Main plugin;

    // Fake Instances
    private PlayerMock playerMock;
    private WorldMock worldMock;

    // Plugin Info
    private Challenge currentChallenge;
    private File[] listOfChallengesGlobalFiles;
    private File[] listOfChallengesEventFiles;
    private Map<String, List<String>> stringListMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);

        listOfChallengesGlobalFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Global").listFiles();
        listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event").listFiles();
        playerMock = serverMock.addPlayer();
        worldMock = playerMock.getWorld();
        currentChallenge = plugin.getDailyChallenge();
        stringListMap = createStringLists();
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    private Map<String, List<String>> createStringLists() {
        Map<String, List<String>> stringLists = new HashMap<>();
        List<String> global = new ArrayList<>();
        for (File file : listOfChallengesGlobalFiles) {
            String name = file.getName().replace(".yml", "");
            global.add(name);
        }
        stringLists.put("Global", global);
        List<String> events = new ArrayList<>();
        for (File file : listOfChallengesEventFiles) {
            String name = file.getName().replace(".yml", "");
            events.add(name);
        }
        stringLists.put("Event", events);
        return stringLists;
    }

    @Test
    public void testControlFiles() {
        FileCreator.controlFiles("Global", listOfChallengesGlobalFiles, stringListMap.get("Global"), "Blacklist");
        FileCreator.controlFiles("Event", listOfChallengesEventFiles, stringListMap.get("Event"), "BlackList");
        FileCreator.controlFiles("Event", listOfChallengesEventFiles, stringListMap.get("Event"), "WhiteList");
        FileCreator.deleteFile(listOfChallengesEventFiles[0]);
        listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event").listFiles();
        FileCreator.controlFiles("Event", listOfChallengesEventFiles, new ArrayList<>(), "BlackList");
    }

    @Test
    public void testDeleteFile() {
        FileCreator.deleteFiles(listOfChallengesEventFiles);
        FileCreator.deleteFiles(listOfChallengesGlobalFiles);

        listOfChallengesGlobalFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Global").listFiles();
        listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event").listFiles();
        Assertions.assertEquals(0, listOfChallengesEventFiles.length);
        Assertions.assertEquals(0, listOfChallengesGlobalFiles.length);
    }

    @Test
    public void addFiles() {
        int previousSizeGlobal = listOfChallengesGlobalFiles.length;
        int previousSizeEvent = listOfChallengesEventFiles.length;
        Map<String, Boolean> debug = new HashMap<>();
        debug.put("CubeGenerator", true);
        debug.put("SuperiorSkyblock2", true);
        FileCreator.addFiles(debug);
        FileCreator.controlFiles("Global", listOfChallengesGlobalFiles, stringListMap.get("Global"), "Blacklist");
        FileCreator.controlFiles("Event", listOfChallengesEventFiles, stringListMap.get("Event"), "BlackList");

        listOfChallengesGlobalFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Global").listFiles();
        listOfChallengesEventFiles = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + File.separator + "Event").listFiles();
        int newSizeGlobal = listOfChallengesGlobalFiles.length;
        int newSizeEvent = listOfChallengesEventFiles.length;
        Assertions.assertEquals(previousSizeEvent + 2, newSizeEvent);
        Assertions.assertEquals(previousSizeGlobal + 2, newSizeGlobal);
    }

}
