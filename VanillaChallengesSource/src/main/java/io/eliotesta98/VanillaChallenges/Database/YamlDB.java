package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YamlDB implements Database {

    private FileConfiguration file;
    private File configFile;
    private final ArrayList<Challenger> playerPoints = new ArrayList<>();
    private final ArrayList<Challenge> challenges = new ArrayList<>();
    private final ArrayList<DailyWinner> dailyWinners = new ArrayList<>();
    private final ArrayList<Challenger> topYesterday = new ArrayList<>();
    private final ArrayList<Challenger> oldPoints = new ArrayList<>();
    private final ArrayList<PlayerStats> stats = new ArrayList<>();

    public YamlDB() {
        initialize("");
    }

    @Override
    public void initialize(String AbsolutePath) {
        configFile = new File(Main.instance.getDataFolder(), "database.yml");

        if (!configFile.exists()) {
            boolean create = false;
            try {
                create = configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (create) {
                file = YamlConfiguration.loadConfiguration(configFile);
            } else {
                for (String playerName : file.getConfigurationSection("Points").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("Points." + playerName));
                    playerPoints.add(challenger);
                }
                for (String playerName : file.getConfigurationSection("PointsLastChallenge").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("PointsLastChallenge." + playerName));
                    oldPoints.add(challenger);
                }
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    Challenge challengeDB = new Challenge();
                    challengeDB.setChallengeName(challenge);
                    challengeDB.setTimeChallenge(file.getInt("Challenges." + challenge));
                    challenges.add(challengeDB);
                }
                for (String number : file.getConfigurationSection("DailyWinners").getKeys(false)) {
                    DailyWinner dailyWinner = new DailyWinner(Integer.parseInt(number),
                            file.getString("DailyWinners." + number + ".PlayerName"),
                            file.getString("DailyWinners." + number + ".NomeChallenge"),
                            file.getString("DailyWinners." + number + ".Reward")
                    );
                    dailyWinners.add(dailyWinner);
                }
                for (String playerName : file.getConfigurationSection("TopYesterday").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("TopYesterday." + playerName));
                    topYesterday.add(challenger);
                }
                for (String playerName : file.getConfigurationSection("Statistic").getKeys(false)) {
                    PlayerStats playerStats = new PlayerStats(playerName,
                            file.getInt("Statistic." + playerName + ".NumberVictories"),
                            file.getInt("Statistic." + playerName + ".NumberFirstPlace"),
                            file.getInt("Statistic." + playerName + ".NumberSecondPlace"),
                            file.getInt("Statistic." + playerName + ".NumberThirdPlace")
                    );
                    stats.add(playerStats);
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
            if (file.getConfigurationSection("PointsLastChallenge") != null) {
                for (String playerName : file.getConfigurationSection("PointsLastChallenge").getKeys(false)) {
                    Challenger challenger = new Challenger(playerName, file.getInt("PointsLastChallenge." + playerName));
                    oldPoints.add(challenger);
                }
            }
            if (file.getConfigurationSection("Challenges") != null) {
                for (String challenge : file.getConfigurationSection("Challenges").getKeys(false)) {
                    Challenge challengeDB = new Challenge();
                    challengeDB.setChallengeName(challenge);
                    challengeDB.setTimeChallenge(file.getInt("Challenges." + challenge));
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
            if (file.getConfigurationSection("Statistic") != null) {
                for (String playerName : file.getConfigurationSection("Statistic").getKeys(false)) {
                    PlayerStats playerStats = new PlayerStats(playerName,
                            file.getInt("Statistic." + playerName + ".NumberVictories"),
                            file.getInt("Statistic." + playerName + ".NumberFirstPlace"),
                            file.getInt("Statistic." + playerName + ".NumberSecondPlace"),
                            file.getInt("Statistic." + playerName + ".NumberThirdPlace")
                    );
                    stats.add(playerStats);
                }
            }
        }
    }

    @Override
    public void backupDb(int numberOfBackupFiles) {
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
                    if (folder.listFiles().length >= numberOfBackupFiles) {
                        java.util.Date finalDate = null;
                        int number = 0;
                        for (int i = 0; i < folder.listFiles().length; i++) {
                            try {
                                Date date = sdf.parse(folder.listFiles()[i].getName());
                                if (finalDate == null) {
                                    finalDate = date;
                                    number = i;
                                } else {
                                    int result = finalDate.compareTo(date);
                                    if (result > 0) {
                                        finalDate = date;
                                        number = i;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        folder.listFiles()[number].delete();
                    }
                }
                configFile.createNewFile();
                YamlConfiguration file = YamlConfiguration.loadConfiguration(configFile);
                for (Challenger playerPoint : playerPoints) {
                    file.set("Points." + playerPoint.getNomePlayer(), playerPoint.getPoints());
                }
                for (Challenge challenge : challenges) {
                    file.set("Challenges." + challenge.getChallengeName(), challenge.getTimeChallenge());
                }
                for (DailyWinner dailyWinner : dailyWinners) {
                    file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
                    file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
                    file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
                }
                for (Challenger challenger : topYesterday) {
                    file.set("TopYesterday." + challenger.getNomePlayer(), challenger.getPoints());
                }
                for (PlayerStats playerStats : stats) {
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberVictories", playerStats.getNumberOfVictories());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberFirstPlace", playerStats.getNumberOfFirstPlace());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberSecondPlace", playerStats.getNumberOfSecondPlace());
                    file.set("Statistic." + playerStats.getPlayerName() + ".NumberThirdPlace", playerStats.getNumberOfThirdPlace());
                }
                file.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveTopYesterday(ArrayList<Challenger> newTopYesterday) {
        for (Challenger challenger : newTopYesterday) {
            file.set("TopYesterday." + challenger.getNomePlayer(), challenger.getPoints());
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTopYesterday() {
        for (Challenger challenger : topYesterday) {
            file.set("TopYesterday." + challenger.getNomePlayer(), null);
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Challenger> getAllChallengersTopYesterday() {
        return topYesterday;
    }

    @Override
    public boolean isPresent(String playerName) {
        for (Challenger playerPoint : playerPoints) {
            if (playerName.equalsIgnoreCase(playerPoint.getNomePlayer())) {
                return true;
            }
        }
        return false;
    }

    public void saveChallenges() {
        for (Challenge challenge : challenges) {
            file.set("Challenges." + challenge.getChallengeName(), challenge.getTimeChallenge());
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String insertDailyChallenges() {
        int count = 1;
        if (challenges.isEmpty()) {
            String nome = "nessuno";
            ArrayList<String> keys = new ArrayList<>(Main.instance.getConfigGestion().getChallenges().keySet());
            if (Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Random")) {
                Collections.shuffle(keys);
            } else if (Main.instance.getConfigGestion().getChallengeGeneration().equalsIgnoreCase("Single")) {
                Collections.shuffle(keys);
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(keys.get(0));
                return challenge.getChallengeName();
            }
            for (String key : keys) {
                Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(key);
                if (count == 1) {
                    Main.instance.setDailyChallenge(challenge);
                    nome = challenge.getTypeChallenge();
                }
                challenges.add(challenge);
                count++;
            }
            saveChallenges();
            return nome;
        } else {
            for (int i = 0; i < challenges.size(); i++) {
                if (challenges.get(i).getTimeChallenge() <= 0) {
                    deleteChallengeWithName(challenges.get(i).getChallengeName());
                    challenges.remove(i);
                } else {
                    if (challenges.get(i).getChallengeName().contains("Event_")) {
                        Challenge challenge = Main.instance.getConfigGestion().getChallengesEvent().get(challenges.get(i).getChallengeName().replace("Event_", ""));
                        challenge.setTimeChallenge(challenges.get(i).getTimeChallenge());
                        Main.instance.setDailyChallenge(challenge);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                        return Main.instance.getDailyChallenge().getTypeChallenge();
                    }
                    Challenge challenge = Main.instance.getConfigGestion().getChallenges().get(challenges.get(i).getChallengeName());
                    challenge.setTimeChallenge(challenges.get(i).getTimeChallenge());
                    Main.instance.setDailyChallenge(challenge);
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Vanilla Challenges] " + challenges.size() + " challenges remain on DB");
                    return Main.instance.getDailyChallenge().getTypeChallenge();
                }
            }
            return "nessuno";
        }
    }

    @Override
    public void loadPlayersPoints() {
        Main.instance.getDailyChallenge().setPlayers(playerPoints);
        Main.instance.getDailyChallenge().savePoints();
        ArrayList<Challenger> top = Main.instance.getDailyChallenge().getTopPlayers(Main.instance.getConfigGestion().getNumberOfRewardPlayer());
        int i = 1;
        while (!top.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor(Main.instance.getConfigGestion().getMessages().get("topPlayers" + i).replace("{number}", "" + i).replace("{player}", top.get(0).getNomePlayer()).replace("{points}", "" + MoneyUtils.transform(top.get(0).getPoints()))));
            top.remove(0);
            i++;
        }
    }

    @Override
    public void disconnect() {
    }

    @Override
    public ArrayList<PlayerStats> getAllPlayerStats() {
        return stats;
    }

    @Override
    public void insertPlayerStat(PlayerStats playerStats) {
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberVictories", playerStats.getNumberOfVictories());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberFirstPlace", playerStats.getNumberOfFirstPlace());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberSecondPlace", playerStats.getNumberOfSecondPlace());
        file.set("Statistic." + playerStats.getPlayerName() + ".NumberThirdPlace", playerStats.getNumberOfThirdPlace());
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePlayerStatWithPlayerName(String playerName) {
        file.set("Statistic." + playerName, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePlayerStat(PlayerStats playerStats) {
        ArrayList<PlayerStats> stats = getAllPlayerStats();
        while (!stats.isEmpty()) {
            if (stats.get(0).getPlayerName().equalsIgnoreCase(playerStats.getPlayerName())) {
                deletePlayerStatWithPlayerName(stats.get(0).getPlayerName());
                insertPlayerStat(playerStats);
                return;
            }
            stats.remove(0);
        }
        insertPlayerStat(playerStats);
    }

    @Override
    public void deleteChallengeWithName(String challengeName) {
        file.set("Challenges." + challengeName, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateChallenge(String nomeChallenge, int number) {
        file.set("Challenges." + nomeChallenge, number);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateChallenger(String playerName, long value) {
        file.set("Points." + playerName, (int) value);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertChallengerEvent(String playerName, long value) {
        file.set("PointsLastChallenge." + playerName, (int) value);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertChallenger(String playerName, long value) {
        playerPoints.add(new Challenger(playerName, (int) value));
        file.set("Points." + playerName, (int) value);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearChallengesFromFile() {
        file.set("Challenges", null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearStats() {
        file.set("Statistic", null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlayerHaveStats(String playerName) {
        for (PlayerStats playerStats : stats) {
            if (playerStats.getPlayerName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void insertChallenge(String challengeName, int time) {
        file.set("Challenges." + challengeName, time);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveOldPointsForChallengeEvents() {
        HashMap<String, Long> copyMap = new HashMap<>(Main.instance.getDailyChallenge().getPlayers());
        for (Map.Entry<String, Long> player : copyMap.entrySet()) {
            try {
                if (player.getValue() > 0) {
                    insertChallengerEvent(player.getKey(), player.getValue());
                }
            } catch (Exception ex) {
                Bukkit.getServer().getConsoleSender().sendMessage("Save Points Event: " + ex.getMessage());
            }
        }
        clearChallengers();
    }

    @Override
    public void resumeOldPoints() {
        clearChallengers();
        for (Challenger challenger : oldPoints) {
            insertChallenger(challenger.getNomePlayer(), challenger.getPoints());
        }
        clearChallengersOldPoints();
    }

    @Override
    public ArrayList<Challenger> getAllOldChallengers() {
        return oldPoints;
    }

    @Override
    public boolean isChallengePresent(String challengeName) {
        for (Challenge challenge : challenges) {
            if (challenge.getChallengeName().equalsIgnoreCase(challengeName)) {
                return true;
            }
        }
        return false;
    }

    public void clearChallengersOldPoints() {
        file.set("PointsLastChallenge", null);
        oldPoints.clear();
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearChallengers() {
        file.set("Points", null);
        playerPoints.clear();
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDailyWinnerWithId(int id) {
        file.set("DailyWinners." + id, null);
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerStats getStatsPlayer(String playerName) {
        for (PlayerStats playerStats : stats) {
            if (playerStats.getPlayerName().equalsIgnoreCase(playerName)) {
                return playerStats;
            }
        }
        return null;
    }

    @Override
    public ArrayList<PlayerStats> getTopVictories() {
        ArrayList<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfVictories));
        Collections.reverse(top);
        return top;
    }

    @Override
    public ArrayList<PlayerStats> getTopFirstPlace() {
        ArrayList<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfFirstPlace));
        Collections.reverse(top);
        return top;
    }

    @Override
    public ArrayList<PlayerStats> getTopSecondPlace() {
        ArrayList<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfSecondPlace));
        Collections.reverse(top);
        return top;
    }

    @Override
    public ArrayList<PlayerStats> getTopThirdPlace() {
        ArrayList<PlayerStats> top = new ArrayList<>(stats);
        top.sort(Comparator.comparing(PlayerStats::getNumberOfThirdPlace));
        Collections.reverse(top);
        return top;
    }

    @Override
    public void insertDailyWinner(DailyWinner dailyWinner) {
        file.set("DailyWinners." + dailyWinner.getId() + ".PlayerName", dailyWinner.getPlayerName());
        file.set("DailyWinners." + dailyWinner.getId() + ".NomeChallenge", dailyWinner.getNomeChallenge());
        file.set("DailyWinners." + dailyWinner.getId() + ".Reward", dailyWinner.getReward());
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertChallengeEvent(String challengeName, int time) {
        Challenge challenge = Main.instance.getConfigGestion().getChallengesEvent().get(challengeName);
        challenge.setChallengeName("Event_" + challengeName);
        challenge.setTimeChallenge(time);
        challenges.add(0, challenge);
        clearChallengesFromFile();
        for (Challenge challenge1 : challenges) {
            insertChallenge(challenge1.getChallengeName(), challenge1.getTimeChallenge());
        }
        try {
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDailyWinner(DailyWinner dailyWinner) {
        ArrayList<DailyWinner> winners = getAllDailyWinners();
        while (!winners.isEmpty()) {
            if (winners.get(0).getPlayerName().equalsIgnoreCase(dailyWinner.getPlayerName())) {
                deleteDailyWinnerWithId(winners.get(0).getId());
                insertDailyWinner(dailyWinner);
                return;
            }
            winners.remove(0);
        }
        insertDailyWinner(dailyWinner);
    }

    @Override
    public ArrayList<DailyWinner> getAllDailyWinners() {
        return dailyWinners;
    }

    @Override
    public int lastDailyWinnerId() {
        int last = 0;
        for (DailyWinner dailyWinner : dailyWinners) {
            if (dailyWinner.getId() > last) {
                last = dailyWinner.getId();
            }
        }
        return last;
    }

    @Override
    public void clearAll() {
        configFile.delete();
    }

    @Override
    public void controlIfChallengeExist(ArrayList<String> controlIfChallengeExist) {
        for (String challengeName : controlIfChallengeExist) {
            for (Challenge challenge : challenges) {
                if (challenge.getChallengeName().equalsIgnoreCase(challengeName)) {
                    challenges.remove(challenge);
                    deleteChallengeWithName(challengeName);
                    break;
                }
            }
        }
    }

    @Override
    public ArrayList<Challenge> getAllChallenges() {
        return challenges;
    }

    public void saveFile() throws IOException {
        file.save(configFile);
    }

}
