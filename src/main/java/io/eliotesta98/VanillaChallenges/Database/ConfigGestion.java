package io.eliotesta98.VanillaChallenges.Database;

import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigGestion {

    private HashMap<String, Boolean> debug = new HashMap<String, Boolean>();
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();

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
            Challenge challenge = new Challenge(block, blockOnPlaced, typeChallenge);
            challenges.put(challengeName, challenge);
        }
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

}
