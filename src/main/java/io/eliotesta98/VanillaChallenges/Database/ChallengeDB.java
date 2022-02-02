package io.eliotesta98.VanillaChallenges.Database;

public class ChallengeDB {

    private String nomeChallenge;
    private int timeResume;

    public ChallengeDB() {

    }

    public ChallengeDB(String nomeChallenge, int timeResume) {
        this.nomeChallenge = nomeChallenge;
        this.timeResume = timeResume;
    }

    public String getNomeChallenge() {
        return nomeChallenge;
    }

    public void setNomeChallenge(String nomeChallenge) {
        this.nomeChallenge = nomeChallenge;
    }

    public int getTimeResume() {
        return timeResume;
    }

    public void setTimeResume(int timeResume) {
        this.timeResume = timeResume;
    }

    @Override
    public String toString() {
        return "ChallengeDB{" +
                "nomeChallenge='" + nomeChallenge + '\'' +
                ", timeResume=" + timeResume +
                '}';
    }
}
