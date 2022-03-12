package io.eliotesta98.VanillaChallenges.Database;

import java.util.ArrayList;

public interface Database {

    void initialize(String AbsolutePath);

    String insertDailyChallenges();

    void deleteChallengeWithName(String challengeName);

    void loadPlayersPoints();

    void disconnect();

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

    void clearAll();
}
