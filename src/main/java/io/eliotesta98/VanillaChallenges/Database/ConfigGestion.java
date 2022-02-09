package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<String, Boolean>();
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();
    private int timeBrodcastMessageTitle;

    public ConfigGestion(FileConfiguration file) {
        for (String event : file.getConfigurationSection("Debug").getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }
        for (String message : file.getConfigurationSection("Message").getKeys(false)) {
            messages.put(message, file.getString("Message." + message));
        }
        for (String challengeName : file.getConfigurationSection("Configuration.Challenges").getKeys(false)) {
            String block = file.getString("Configuration.Challenges." + challengeName + ".Block");
            String blockOnPlaced = file.getString("Configuration.Challenges." + challengeName + ".BlockOnPlaced");
            String typeChallenge = file.getString("Configuration.Challenges." + challengeName + ".TypeChallenge");
            String reward = file.getString("Configuration.Challenges." + challengeName + ".Reward");
            String title = file.getString("Configuration.Challenges." + challengeName + ".Title");
            String subTitle = file.getString("Configuration.Challenges." + challengeName + ".Description");
            String item =  file.getString("Configuration.Challenges." + challengeName + ".Item");
            String mob =  file.getString("Configuration.Challenges." + challengeName + ".Mob");
            String itemInHand = file.getString("Configuration.Challenges." + challengeName + ".ItemInHand");
            double force = file.getDouble("Configuration.Challenges." + challengeName + ".Force");
            double power = file.getDouble("Configuration.Challenges." + challengeName + ".Power");
            String color = file.getString("Configuration.Challenges." + challengeName + ".Color");
            String cause = file.getString("Configuration.Challenges." + challengeName + ".Cause");
            Challenge challenge = new Challenge(block, blockOnPlaced, typeChallenge, reward,title,subTitle,item,itemInHand,mob,force,power,color,cause);
            challenges.put(challengeName, challenge);
        }
        timeBrodcastMessageTitle = file.getInt("Configuration.BroadcastMessage.TimeTitleChallenges");
    }

    public HashMap<String, Boolean> getDebug() {
        return debug;
    }

    public void setDebug(HashMap<String, Boolean> debug) {
        this.debug = debug;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public HashMap<String, Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(HashMap<String, Challenge> challenges) {
        this.challenges = challenges;
    }

    public int getTimeBrodcastMessageTitle() {
        return timeBrodcastMessageTitle;
    }

    public void setTimeBrodcastMessageTitle(int timeBrodcastMessageTitle) {
        this.timeBrodcastMessageTitle = timeBrodcastMessageTitle;
    }
}
