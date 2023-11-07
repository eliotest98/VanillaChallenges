package io.eliotesta98.VanillaChallenges.Database.Objects;

public class PlayerStats {

    private String playerName;
    private int numberOfVictories, numberOfFirstPlace, numberOfSecondPlace, numberOfThirdPlace;

    public PlayerStats(String playerName, int numberOfVictories, int numberOfFirstPlace, int numberOfSecondPlace, int numberOfThirdPlace) {
        this.playerName = playerName;
        this.numberOfVictories = numberOfVictories;
        this.numberOfFirstPlace = numberOfFirstPlace;
        this.numberOfSecondPlace = numberOfSecondPlace;
        this.numberOfThirdPlace = numberOfThirdPlace;
    }

    public PlayerStats() {

    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getNumberOfVictories() {
        return numberOfVictories;
    }

    public void setNumberOfVictories(int numberOfVictories) {
        this.numberOfVictories = numberOfVictories;
    }

    public int getNumberOfFirstPlace() {
        return numberOfFirstPlace;
    }

    public void setNumberOfFirstPlace(int numberOfFirstPlace) {
        this.numberOfFirstPlace = numberOfFirstPlace;
    }

    public int getNumberOfSecondPlace() {
        return numberOfSecondPlace;
    }

    public void setNumberOfSecondPlace(int numberOfSecondPlace) {
        this.numberOfSecondPlace = numberOfSecondPlace;
    }

    public int getNumberOfThirdPlace() {
        return numberOfThirdPlace;
    }

    public void setNumberOfThirdPlace(int numberOfThirdPlace) {
        this.numberOfThirdPlace = numberOfThirdPlace;
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "playerName='" + playerName + '\'' +
                ", numberOfVictories=" + numberOfVictories +
                ", numberOfFirstPlace=" + numberOfFirstPlace +
                ", numberOfSecondPlace=" + numberOfSecondPlace +
                ", numberOfThirdPlace=" + numberOfThirdPlace +
                '}';
    }
}
