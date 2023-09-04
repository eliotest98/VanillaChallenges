package io.eliotesta98.VanillaChallenges.Database.Objects;

public class DailyWinner {

    private int id;
    private String playerName, nomeChallenge, reward;

    public DailyWinner() {

    }

    public DailyWinner(int id, String playerName, String nomeChallenge, String reward) {
        this.id = id;
        this.playerName = playerName;
        this.nomeChallenge = nomeChallenge;
        this.reward = reward;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getNomeChallenge() {
        return nomeChallenge;
    }

    public void setNomeChallenge(String nomeChallenge) {
        this.nomeChallenge = nomeChallenge;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "DailyWinner{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", nomeChallenge='" + nomeChallenge + '\'' +
                ", reward='" + reward + '\'' +
                '}';
    }
}
