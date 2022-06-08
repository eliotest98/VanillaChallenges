package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.io.*;
import java.util.List;

public class FileCreator {

    public static void createAllFiles(List<File> files) {
        for (File file : files) {
            createFileChallenges(file);
        }
    }

    public static void createFileChallenges(File newFile) {
        if (!newFile.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {

                Main.instance.saveResource("Challenges" + File.separator + newFile.getName(), false);
                inputStream = Main.instance.getResource(newFile.getName());

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
