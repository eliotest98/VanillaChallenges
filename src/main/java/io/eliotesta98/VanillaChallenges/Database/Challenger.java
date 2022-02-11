package io.eliotesta98.VanillaChallenges.Database;

public class Challenger {

    private String nomePlayer;
    private long points;

    public Challenger() {
        this.nomePlayer = "Notch";
        this.points = -1;
    }

    public Challenger(String nomePlayer, long points) {
        this.nomePlayer = nomePlayer;
        this.points = points;
    }

    public String getNomePlayer() {
        return nomePlayer;
    }

    public void setNomePlayer(String nomePlayer) {
        this.nomePlayer = nomePlayer;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Point{" +
                "nomePlayer='" + nomePlayer + '\'' +
                ", points=" + points +
                '}';
    }
}
