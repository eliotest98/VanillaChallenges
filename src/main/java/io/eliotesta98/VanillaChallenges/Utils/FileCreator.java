package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FileCreator {

    private static final ArrayList<String> files = new ArrayList<>();

    public static void addFiles(HashMap<String, Boolean> hooks) {
        files.add("BlockBreaker.yml");
        files.add("BlockPlacer.yml");
        files.add("Cooker.yml");
        files.add("Crafter.yml");
        files.add("Consumer.yml");
        files.add("ExpCollector.yml");
        files.add("Killer.yml");
        files.add("Breeder.yml");
        files.add("Feeder.yml");
        files.add("Shooter.yml");
        files.add("JumperHorse.yml");
        files.add("Jumper.yml");
        files.add("Dyer.yml");
        files.add("Raider.yml");
        files.add("Fisher.yml");
        files.add("Sprinter.yml");
        files.add("Mover.yml");
        files.add("Damager.yml");
        files.add("Sneaker.yml");
        files.add("ItemBreaker.yml");
        files.add("Absorber.yml");
        files.add("Harvester.yml");
        files.add("EggThrower.yml");
        files.add("Enchanter.yml");
        files.add("Chatter.yml");
        files.add("ItemCollector.yml");
        files.add("InventoryControl.yml");
        files.add("BoatMove.yml");
        files.add("Dier.yml");
        files.add("Dropper.yml");
        files.add("Healer.yml");
        files.add("AFK.yml");
        files.add("SensorActuator.yml");
        files.add("Primer.yml");
        files.add("FireCatcher.yml");
        files.add("EntityCatcher.yml");
        files.add("Leasher.yml");
        if (hooks.get("CubeGenerator")) {
            files.add("CubeGenerator.yml");
        }
        if (hooks.get("SuperiorSkyblock2")) {
            files.add("SuperiorSkyBlock2.yml");
        }
    }

    public static void controlFiles(String folder, File[] folderFiles) {
        ArrayList<String> copyFiles = new ArrayList<>(files);
        for (File file : folderFiles) {
            String fileName = file.getName();
            copyFiles.remove(fileName);
        }
        if (!copyFiles.isEmpty()) {
            for (String file : copyFiles) {
                createFileChallenges(folder, file);
            }
            Main.instance.getServer().getConsoleSender().sendMessage("§e" + copyFiles.size() + " " + folder + " Challenges recreated!");
            Main.instance.getServer().getConsoleSender().sendMessage("§ePlease not delete files! Disable the challenge in the configuration file!");
        }
    }

    public static void createAllFiles(String folder) {
        for (String fileName : files) {
            createFileChallenges(folder, fileName);
        }
        Main.instance.getServer().getConsoleSender().sendMessage("§a" + files.size() + " " + folder + " Challenges created!");
    }

    public static void createFileChallenges(String folder, String fileName) {

        File newFile = new File(Main.instance.getDataFolder() +
                File.separator + "Challenges" + folder, fileName);

        if (!newFile.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {

                Main.instance.saveResource("Challenges" + File.separator + folder + File.separator + fileName, false);
                inputStream = Main.instance.getResource("Challenges" + File.separator + folder + File.separator + newFile.getName());

                // write the inputStream to a FileOutputStream
                outputStream = new FileOutputStream(newFile);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
