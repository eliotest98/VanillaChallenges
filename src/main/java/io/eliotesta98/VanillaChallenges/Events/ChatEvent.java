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
    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private BukkitTask task;
    private String word = "";
    private final String message = Main.instance.getConfigGestion().getMessages().get("chatWord");
    private final String alphabet = "abcdefghijklmnopqrstuvywxz0123456789/*-+.:;_-!£$%&()=?'^òàèìù#@[]{}§*é°ç,";

    public ChatEvent() {
        //ogni 4 minuti
        start(20 * 60 * 4);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSneak(org.bukkit.event.player.AsyncPlayerChatEvent e) {
        long tempo = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (e.getMessage().equalsIgnoreCase(word)) {
                    Main.dailyChallenge.increment(e.getPlayer().getName(), (long) point * word.length());
                    word = "";
                }
            }
        });
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

    public void stop() {
        task.cancel();
    }


    public void execute(long time) {
        task = scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (word.equalsIgnoreCase("")) {
                    generateWord();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ColorUtils.applyColor(message.replace("{points}", word.length() + "").replace("{word}", word)));
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ColorUtils.applyColor(message.replace("{points}", word.length() + "").replace("{word}", word)));
                    }
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
    }
}
