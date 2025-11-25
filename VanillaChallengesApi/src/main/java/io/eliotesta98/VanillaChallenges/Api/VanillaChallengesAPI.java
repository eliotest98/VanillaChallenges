package io.eliotesta98.VanillaChallenges.Api;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * VanillaChallenges API.
 *
 * @author eliotesta98
 * @version 1.1.8
 */
public class VanillaChallengesAPI {

    private static final boolean isDebugEnabled = Main.instance.getConfigGesture().getDebug().get("API");

    /**
     * Returns the name of Daily Challenge Selected.
     *
     * @return the name of challenge.
     */
    public static String getDailyChallengeName() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getChallengeName();
    }

    /**
     * Returns the type of Daily Challenge Selected.
     *
     * @return the type of challenge.
     */
    public static String getDailyChallengeType() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getTypeChallenge();
    }

    /**
     * Returns the gui name of Daily Challenge Selected.
     *
     * @return the gui name of the challenge.
     */
    public static String getDailyChallengeGuiName() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getNameChallenge();
    }

    /**
     * Returns the gui item of Daily Challenge Selected.
     *
     * @return the gui item of the challenge.
     */
    public static String getDailyChallengeGuiItem() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getItemChallenge();
    }

    /**
     * Returns the time remaining of Daily Challenge Selected.
     *
     * @return the time remaining of the challenge.
     */
    public static String getDailyChallengeTime() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getTimeChallenge().getTime();
    }

    /**
     * Returns the point of Daily Challenge Selected.
     *
     * @return the points that the challenge gives.
     */
    public static int getDailyChallengePoint() {
        DebugUtils debug = new DebugUtils("API");
        long time = System.currentTimeMillis();
        if (isDebugEnabled) {
            debug.addLine("Method: getDailyChallenge");
            debug.addLine("API execution time= " + (System.currentTimeMillis() - time));
            debug.debug();
        }
        return Main.instance.getDailyChallenge().getPoint();
    }

    /**
     * Returns the list of challengers with points.<br/>
     * ATTENTION! This is an unmodifableMap instance<br/>
     *
     * @return a Map with name Challengers and points.
     */
    public static Map<String, Long> getChallengers() {
        return Collections.unmodifiableMap(Main.instance.getDailyChallenge().getPlayers());
    }

    /**
     * Returns the points of a specific player.
     *
     * @param playerName name of player
     * @return the points of player otherwise -1.
     */
    public static long getPoints(String playerName) {
        if (Main.instance.getDailyChallenge().getPlayers().containsKey(playerName)) {
            return Main.instance.getDailyChallenge().getPlayers().get(playerName);
        } else {
            return -1;
        }
    }

    /**
     * Add VanillaChallenges points at specific player.<br>
     * If you search for how to remove points, see removePoints method.
     *
     * @param playerName name of player
     * @param points     points you want give at player <br>
     * @see #removePoints(String playerName, long points) method
     */
    public static void addPoints(String playerName, long points) {
        Main.instance.getDailyChallenge().incrementCommands(playerName, points);
    }

    /**
     * Remove VanillaChallenges points at specific player.<br>
     * If you search for how to remove points, see removePoints method.
     *
     * @param playerName name of player
     * @param points     points you want give at player
     * @see #removePoints(String playerName, long points) method
     */
    public static void removePoints(String playerName, long points) {
        Main.instance.getDailyChallenge().incrementCommands(playerName, Math.negateExact(points));
    }

    /**
     * Returns the list of top player in real time.<br/>
     * ATTENTION! This is an unmodifableList instance<br/>
     *
     * @return a list of top players ordered
     */
    public static List<String> getTop() {
        ArrayList<String> players = new ArrayList<>();
        for (Challenger challenger : Main.instance.getDailyChallenge().getTopPlayers(Main.instance.getConfigGesture().getNumberOfTop())) {
            players.add(challenger.getNomePlayer());
        }
        return Collections.unmodifiableList(players);
    }

}
