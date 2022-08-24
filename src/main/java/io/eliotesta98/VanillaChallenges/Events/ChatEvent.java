package io.eliotesta98.VanillaChallenges.Events;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Utils.ColorUtils;
import io.eliotesta98.VanillaChallenges.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class ChatEvent implements Listener {

    private DebugUtils debugUtils = new DebugUtils();
    private boolean debugActive = Main.instance.getConfigGestion().getDebug().get("ChatEvent");
    private int point = Main.dailyChallenge.getPoint();
    private final String alphabet = Main.dailyChallenge.getStringFormatter();
    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private static BukkitTask task;
    private String word = "";
    private final String message = Main.instance.getConfigGestion().getMessages().get("ChatWord");
    private final String correctAnswer = Main.instance.getConfigGestion().getMessages().get("CorrectAnswer");

    public ChatEvent() {
        //ogni 2 minuti
        start(20 * 60 * 2);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent e) {
        long tempo = System.currentTimeMillis();
        if (debugActive) {
            debugUtils.addLine("ChatEvent message: " + e.getMessage() + " word: " + word);
        }
        if (e.getMessage().equalsIgnoreCase(word)) {
            Main.dailyChallenge.increment(e.getPlayer().getName(), (long) point * word.length());
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ColorUtils.applyColor(correctAnswer.replace("{player}", e.getPlayer().getName())));
            }
            if (debugActive) {
                debugUtils.addLine("ChatEvent add " + (point * word.length()) + " points at " + e.getPlayer().getName());
            }
            word = "";
        }
        //Main.instance.getDailyChallenge().stampaNumero(e.getPlayer().getName());
        if (debugActive) {
            debugUtils.addLine("ChatEvent execution time= " + (System.currentTimeMillis() - tempo));
            debugUtils.debug("ChatEvent");
        }
        return;
    }

    public void start(long time) {
        execute(time);
    }

    public static void stop() {
        task.cancel();
    }

    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (debugActive) {
                    debugUtils.addLine("ChatEvent world broadcasted: " + word);
                }
                word = "";
                generateWord();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ColorUtils.applyColor(message.replace("{points}", (word.length() * point) + "").replace("{word}", word)));
                }
            }
        }, 0, time);
    }

    public void generateWord() {
        Random random = new Random();
        //numero fra 1 e 15
        int number = random.nextInt(15) + 1;
        for (int i = 0; i < number; i++) {
            word = word + alphabet.charAt(random.nextInt(alphabet.length()));
        }
        if (debugActive) {
            debugUtils.addLine("ChatEvent world generated: " + word);
        }
    }
}
