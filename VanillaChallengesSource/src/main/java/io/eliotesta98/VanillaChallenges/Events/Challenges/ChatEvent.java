package io.eliotesta98.VanillaChallenges.Events.Challenges;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Events.Challenges.Modules.Controls;
import io.eliotesta98.VanillaChallenges.Modules.SuperiorSkyblock2.SuperiorSkyBlock2Utils;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public class ChatEvent implements Listener {

    private DebugUtils debugUtils;
    private final boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ChatEvent");
    private final int point = Main.dailyChallenge.getPoint();
    private final ArrayList<String> quests = Main.dailyChallenge.getQuests();
    private String word = "";
    private String quest = "";
    private final String message = Main.instance.getConfigGestion().getMessages().get("ChatWord");
    private final String correctAnswer = Main.instance.getConfigGestion().getMessages().get("CorrectAnswer");
    private final boolean superiorSkyBlock2Enabled = Main.instance.getConfigGestion().getHooks().get("SuperiorSkyblock2");


    public ChatEvent() {
        //ogni 2 minuti
        start(20 * 60 * 2);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        debugUtils = new DebugUtils(e);
        long tempo = System.currentTimeMillis();
        String world = e.getPlayer().getWorld().getName();
        String playerName = e.getPlayer().getName();

        if (debugActive) {
            debugUtils.addLine("message: " + e.getMessage() + " word: " + word);
        }

        if (superiorSkyBlock2Enabled) {
            if (SuperiorSkyBlock2Utils.isInsideIsland(SuperiorSkyBlock2Utils.getSuperiorPlayer(playerName))) {
                if (debugActive) {
                    debugUtils.addLine("Player is inside his own island");
                }
            } else {
                if (debugActive) {
                    debugUtils.addLine("Player isn't inside his own island");
                    debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
                    debugUtils.debug();
                }
                return;
            }
        }

        if (!Controls.isWorldEnable(world, debugActive, debugUtils, tempo)) {
            return;
        }

        if (e.getMessage().equalsIgnoreCase(word)) {
            Main.dailyChallenge.increment(e.getPlayer().getName(), (long) point * word.length());
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ColorUtils.applyColor(correctAnswer.replace("{player}", e.getPlayer().getName())));
            }
            if (debugActive) {
                debugUtils.addLine("add " + (point * word.length()) + " points at " + e.getPlayer().getName());
            }
            word = "";
        }
        if (debugActive) {
            debugUtils.addLine("execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug();
        }
    }

    public void start(long time) {
        execute(time);
    }

    public void execute(long time) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, () -> {
            if (debugActive) {
                debugUtils.addLine("world broadcasted: " + word);
            }
            word = "";
            quest = "";
            generateWord();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!quest.equalsIgnoreCase("")) {
                    p.sendMessage(ColorUtils.applyColor(quest));
                } else {
                    p.sendMessage(ColorUtils.applyColor(message.replace("{points}", (word.length() * point) + "").replace("{word}", word)));
                }
            }
        }, 0, time);
        Main.instance.getConfigGestion().getTasks().addExternalTasks(task, "ChatEvent", false);
    }

    public void generateWord() {
        Random random = new Random();
        if (quests.size() == 1 && quests.get(0).contains("Formatter")) {
            String alphabet = quests.get(0).split(":")[1];
            //numero fra 1 e 15
            int number = random.nextInt(15) + 1;
            for (int i = 0; i < number; i++) {
                word = word + alphabet.charAt(random.nextInt(alphabet.length()));
            }
            if (debugActive) {
                debugUtils.addLine("world generated: " + word);
            }
        } else {
            String quest = quests.get(random.nextInt(quests.size()));
            word = quest.split(":")[1];
            this.quest = quest.split(":")[0];
        }
    }
}
