package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.DailyWinner;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import java.util.ArrayList;

public interface Database {

    void initialize(String AbsolutePath);

    String insertDailyChallenges();

    void insertChallengeEvent(String challengeName, int time);

    void deleteChallengeWithName(String challengeName);

    void loadPlayersPoints();

    void disconnect();

    ArrayList<PlayerStats> getAllPlayerStats();

    void insertPlayerStat(PlayerStats playerStats);

    void deletePlayerStatWithPlayerName(String playerName);

    void updatePlayerStat(PlayerStats playerStats);

    ArrayList<Challenger> getAllChallengersTopYesterday();

    boolean isPresent(String playerName);

    void updateChallenger(String playerName, long points);

    void insertChallenger(String playerName, long point);

    void updateDailyWinner(DailyWinner dailyWinner);

    void removeTopYesterday();

    void saveTopYesterday(ArrayList<Challenger> newTopYesterday);

    int lastDailyWinnerId();

    void backupDb(int numberOfBackupedFile);

    void insertDailyWinner(DailyWinner dailyWinner);

    void clearChallengers();

    void updateChallenge(String nomeChallenge,int timeResume);

    ArrayList<DailyWinner> getAllDailyWinners();

    void deleteDailyWinnerWithId(int id);

    PlayerStats getStatsPlayer(String playerName);

    ArrayList<PlayerStats> getTopVictories();

    ArrayList<PlayerStats> getTopFirstPlace();

    ArrayList<PlayerStats> getTopSecondPlace();

    ArrayList<PlayerStats> getTopThirdPlace();

    void clearAll();

    void controlIfChallengeExist(ArrayList<String> controlIfChallengeExist);

    ArrayList<Challenge> getAllChallenges();

    void clearChallengesFromFile();

    void clearStats();

    boolean isPlayerHaveStats(String playerName);

    void insertChallenge(String challengeName, int time);

    void saveOldPointsForChallengeEvents();

    void resumeOldPoints();

    ArrayList<Challenger> getAllOldChallengers();

    boolean isChallengePresent(String challengeName);
}
