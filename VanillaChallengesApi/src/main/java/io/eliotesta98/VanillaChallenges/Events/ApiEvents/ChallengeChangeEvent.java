package io.eliotesta98.VanillaChallenges.Events.ApiEvents;

import io.eliotesta98.VanillaChallenges.Utils.Challenge;
import org.bukkit.event.*;

/**
 * This Event is invoked when challenge is finished or forced to finish <br>
 *
 * @see org.bukkit.event.Cancellable
 * @see org.bukkit.event.Listener
 * @see org.bukkit.event.Event
 *
 */
public class ChallengeChangeEvent extends Event implements Cancellable, Listener {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String reason;
    private final String currentChallengeName;
    private final String currentChallengeType;
    private final int currentChallengeTime;
    private boolean isCancelled;

    // Spigot request
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    // Spigot request
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    // Spigot request
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    // Spigot request
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    // Constructor
    public ChallengeChangeEvent(String reason, Challenge challenge) {
        this.isCancelled = false;
        this.reason = reason;
        this.currentChallengeName = challenge.getChallengeName();
        this.currentChallengeTime = challenge.getTimeChallenge();
        this.currentChallengeType = challenge.getTypeChallenge();
    }

    //getter
    public String getReason() {
        return this.reason;
    }

    public String getCurrentChallengeName() {
        return currentChallengeName;
    }

    public String getCurrentChallengeType() {
        return currentChallengeType;
    }

    public int getCurrentChallengeTime() {
        return currentChallengeTime;
    }

}
