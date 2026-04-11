package io.eliotesta98.VanillaChallenges.Utils;

import java.util.ArrayList;

import io.eliotesta98.VanillaChallenges.Core.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.Event;

public class DebugUtils {

    private ArrayList<String> lines = new ArrayList<>();
    private String eventName = "Event";

    public DebugUtils(ArrayList<String> lines) {
        this.lines = lines;
    }

    public DebugUtils(Event event) {
        eventName = event.getEventName();
    }

    public DebugUtils(String event) {
        eventName = event;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    public ArrayList<String> getLines() {
        return this.lines;
    }

    public String getLine(int index) {
        return this.lines.get(index);
    }

    public void setLine(String newLine, int index) {
        this.lines.set(index, newLine);
    }

    public void addLine(String newLine) {
        this.lines.add(eventName + " " + newLine);
    }

    public void removeLine(int index) {
        this.lines.remove(index);
    }

    public int getSize() {
        return this.lines.size();
    }

    public boolean containLine(String line) {
        return this.lines.contains(line);
    }

    public void removeAll() {
        this.lines.clear();
    }

    public void debug() {
        String debug = " Debug VanillaChallenges for " + eventName;
        Main.messageGesturePaper.sendMessage(" ");
        Main.messageGesturePaper.sendMessage("&e" + debug);
        Main.messageGesturePaper.sendMessage(" ");
        for (int i = 0; i < this.getSize(); i++) {
            Main.messageGesturePaper.sendMessage("&c" + this.getLine(i));
        }
        Main.messageGesturePaper.sendMessage(" ");
        debug = " Close Debug VanillaChallenges for " + eventName;
        Main.messageGesturePaper.sendMessage("&e" + debug);
        Main.messageGesturePaper.sendMessage(" ");
        removeAll();
    }

}
