package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class YamlDB {

    private FileConfiguration file;
    private File configFile;
    private ArrayList<Challenger> playerPoints = new ArrayList<Challenger>();
    private ArrayList<ChallengeDB> challenges = new ArrayList<ChallengeDB>();
    private ArrayList<DailyWinner> dailyWinners = new ArrayList<DailyWinner>();
    private ArrayList<Challenger> topYesterday = new ArrayList<Challenger>();

    public YamlDB() throws IOException {
        configFile = new File(Main.instance.getDataFolder(), "database.yml");

        if (!configFile.exists()) {
            boolean create = configFile.createNewFile();
            if (create) {
                file = YamlConfiguration.loadConfiguration(configFile);
            } else {
                for (String playerName : file.getConfigurationSection("Points").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("Points." + playerName));
                    playerPoints.add(challenger);
                }
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    ChallengeDB challengeDB = new ChallengeDB(challenge, file.getInt("Challenges." + challenge));
                    challenges.add(challengeDB);
                }
                for (String number : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                    DailyWinner dailyWinner = new DailyWinner(Integer.parseInt(number), file.getString("DailyWinners." + number + ".PlayerName"), file.getString("DailyWinners." + number + ".NomeChallenge"), file.getString("DailyWinners." + number + ".Reward"));
                    dailyWinners.add(dailyWinner);
                }
                for (String playerName : file.getConfigurationSection("TopYesterday").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("TopYesterday." + playerName));
                    topYesterday.add(challenger);
                }
            }
        } else {
            file = YamlConfiguration.loadConfiguration(configFile);
            if (file.getConfigurationSection("Points") != null) {
                for (String playerName : file.getConfigurationSection("Points").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("Points." + playerName));
                    playerPoints.add(challenger);
                }
            }
            if (file.getConfigurationSection("Challenges") != null) {
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    ChallengeDB challengeDB = new ChallengeDB(challenge, file.getInt("Challenges." + challenge));
                    challenges.add(challengeDB);
                }
            }
            if (file.getConfigurationSection("DailyWinners") != null) {
                for (String number : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                    DailyWinner dailyWinner = new DailyWinner(Integer.parseInt(number), file.getString("DailyWinners." + number + ".PlayerName"), file.getString("DailyWinners." + number + ".NomeChallenge"), file.getString("DailyWinners." + number + ".Reward"));
                    dailyWinners.add(dailyWinner);
                }
            }
            if (file.getConfigurationSection("TopYesterday") != null) {
                for (String playerName : file.getConfigurationSection("TopYesterday").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("TopYesterday." + playerName));
                    topYesterday.add(challenger);
                }
            }
        }
    }

    public void backupDb() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String data = sdf.format(timestamp);
        File configFile = new File(Main.instance.getDataFolder() +
                File.separator + "backup", data + ".yml");
        if (!configFile.exists()) {
            try {
                File folder = new File(Main.instance.getDataFolder() +
                        File.separator + "backup");
                boolean folderCreate = folder.mkdir();
                if (!folderCreate) {
                    if (folder.listFiles().length > 4) {
                        folder.listFiles()[0].delete();
                    }
                }
                configFile.createNewFile();
                YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                for (int i = 0; i < playerPoints.size(); i++) {
                    file.set("Points." + playerPoints.get(i).getNomePlayer(), playerPoints.get(i).getPoints());
                }
                for (int i = 0; i < challenges.size(); i++) {
                    file.set("Challenges." + challenges.get(i).getNomeChallenge(), challenges.get(i).getTimeResume());
                }
                for (int i = 0; i < dailyWinners.size(); i++) {
                    file.set("DailyWinners." + dailyWinners.get(i).getId() + ".PlayerName", dailyWinners.get(i).getPlayerName());
                    file.set("DailyWinners." + dailyWinners.get(i).getId() + ".NomeChallenge", dailyWinners.get(i).getNomeChallenge());
                    file.set("DailyWinners." + dailyWinners.get(i).getId() + ".Reward", dailyWinners.get(i).getReward());
                }
                for (int i = 0; i < topYesterday.size(); i++) {
                    file.set("TopYesterday." + topYesterday.get(i).getNomePlayer(), topYesterday.get(i).getPoints());
                }
                file.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void saveTopYesterday(ArrayList<Challenger> newTopYesterday) {
        removeTopYesterday();
        for (int i = 0; i < newTopYesterday.size(); i++) {
            file.set("TopYesterday." + newTopYesterday.get(i).getNomePlayer(), newTopYesterday.get(i).getPoints());
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void removeTopYesterday() {
        for (int i = 0; i < topYesterday.size(); i++) {
            file.set("TopYesterday." + topYesterday.get(i).getNomePlayer(), null);
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public ArrayList<Challenger> getTopYesterday() {
        return topYesterday;
    }

    public void saveFile() throws IOException {
        file.save(configFile);
    }

    public boolean isPresent(String playerName) {
        for (int i = 0; i < playerPoints.size(); i++) {
            if (playerName.equalsIgnoreCase(playerPoints.get(i).getNomePlayer())) {
                return true;
            }
        }
        return false;
    }

    public void saveChallenges() {
        for (int i = 0; i < challenges.size(); i++) {
            file.set("Challenges." + challenges.get(i).getNomeChallenge(), challenges.get(i).getTimeResume());
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public String insertDailyChallenges() {
        int count = 1;
        if (challenges.isEmpty()) {
            String nome = "nessuno";
            for (Map.Entry<String, Challenge> challenge : Main.instance.getConfigGestion().getChallenges().entrySet()) {
                if (count == 1) {
                    Main.dailyChallenge = challenge.getValue();
                    nome = challenge.getValue().getTypeChallenge();
                    Main.currentlyChallengeDB = new ChallengeDB(challenge.getKey(), 86400);
                }
                challenges.add(new ChallengeDB(challenge.getKey(), 86400));
                count++;
            }
            saveChallenges();
            return nome;
        } else {
            for (int i = 0; i < challenges.size(); i++) {
                if (challenges.get(i).getTimeResume() <= 0) {
                    deleteChallengeWithName(challenges.get(i).getNomeChallenge());
                    challenges.remove(i);
                } else {
                    Main.currentlyChallengeDB = challenges.get(i);
                    Main.dailyChallenge = Main.instance.getConfigGestion().getChallenges().get(challenges.get(i).getNomeChallenge());
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                    return Main.dailyChallenge.getTypeChallenge();
                }
            }
            return "nessuno";
        }
    }

    public void loadPlayersPoints() {
        Main.dailyChallenge.setPlayers(playerPoints);
        Main.dailyChallenge.savePointsYaml();
        ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(3);
        int i = 1;
        while (!top.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
            top.remove(0);
            i++;
        }
    }

    public void deleteChallengeWithName(String challengeName) {
        file.set("Challenges." + challengeName, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void updateChallenge(String nomeChallenge, int number) {
        file.set("Challenges." + nomeChallenge, number);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void updateChallenger(String playerName, long value) {
        file.set("Points." + playerName, (int) value);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void insertChallenger(String playerName, long value) {
        playerPoints.add(new Challenger(playerName, (int) value));
        file.set("Points." + playerName, (int) value);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void deleteDailyWinnerWithId(int id) {
        file.set("DailyWinners." + id, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void insertDailyWinner(DailyWinner dailyWinner) {
        file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
        file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
        file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public ArrayList<DailyWinner> getAllDailyWinners() {
        return dailyWinners;
    }

    public int lastDailyWinnerId() {
        int last = 0;
        for (DailyWinner dailyWinner : dailyWinners) {
            if (dailyWinner.getId() > last) {
                last = dailyWinner.getId();
            }
        }
        return last;
    }

    public void clearAll() {
        configFile.delete();
    }
}
