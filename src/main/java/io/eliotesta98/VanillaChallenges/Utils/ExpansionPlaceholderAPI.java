package io.eliotesta98.VanillaChallenges.Utils;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Challenger;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ExpansionPlaceholderAPI extends PlaceholderExpansion {

    public void PAPIunRegister() {
        unregister();
    }

    public static String applyPlaceholderAPI(OfflinePlayer p, String string) {
        return PlaceholderAPI.setPlaceholders(p, string);
    }

    public ExpansionPlaceholderAPI getInstance() {
        return this;
    }

    @Override
    public String getAuthor() {
        return ChatColor.translateAlternateColorCodes('&', "&celiotesta98 &e:3");
    }

    @Override
    public String getIdentifier() {
        return "vanillachallenges";
    }

    @Override
    public String getVersion() {
        return ChatColor.translateAlternateColorCodes('&', "&f" + Main.instance.getDescription().getVersion());
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(final Player p, final String identifier) {
        if (p == null) {
            return "Player not found";
        }
        // %vanillachallenges_points%
        if (identifier.equalsIgnoreCase("points")) {
            return MoneyUtils.transform(Main.dailyChallenge.getPointFromPLayerName(p.getName()));
        }
        // %vanillachallenges_dailychallenge_name%
        if (identifier.contains("dailychallenge_name")) {
            return Main.currentlyChallengeDB.getNomeChallenge();
        }
        // %vanillachallenges_dailychallenge_time%
        if (identifier.contains("dailychallenge_time")) {
            int timeResume = (Main.currentlyChallengeDB.getTimeResume() / 60) / 60;
            return timeResume + "";
        }
        // %vanillachallenges_dailychallenge_top_name_#% # = number
        if (identifier.contains("dailychallenge_top_name_")) {
            String number = identifier.replace("dailychallenge_top_name_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_name_" + number + "% is not valid please use a number! Ex: %vanillachallenges_dailychallenge_top_name_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(numberTop);
            if (top.size() >= numberTop) {
                return top.get(numberTop - 1).getNomePlayer();
            } else {
                return top.get(top.size() - 1).getNomePlayer();
            }
        }
        // %vanillachallenges_dailychallenge_top_points_#% # = number
        if (identifier.contains("dailychallenge_top_points_")) {
            String number = identifier.replace("dailychallenge_top_points_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_dailychallenge_top_points_" + number + "% is not valid please use a number! Ex: %vanillachallenges_dailychallenge_top_points_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(numberTop);
            if (top.size() >= numberTop) {
                return MoneyUtils.transform(top.get(numberTop - 1).getPoints());
            } else {
                return MoneyUtils.transform(top.get(top.size() - 1).getPoints());
            }
        }
        // %vanillachallenges_dailychallenge_boost_multiplier%
        if (identifier.equalsIgnoreCase("dailychallenge_boost_multiplier")) {
            if (Main.dailyChallenge.isActive()) {
                return Main.dailyChallenge.getMultiplier() + "";
            } else {
                return 1 + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_points_remain%
        if (identifier.equalsIgnoreCase("dailychallenge_boost_points_remain")) {
            if (!Main.dailyChallenge.isActive()) {
                long pointsRemain = Main.dailyChallenge.getPointsBoost() - Main.dailyChallenge.getCountPointsChallenge();
                return pointsRemain + "";
            } else {
                return Main.dailyChallenge.getPointsBoost() + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_multiplier_single_player%
        if (identifier.contains("dailychallenge_boost_multiplier_single_player")) {
            if (Main.dailyChallenge.isActiveSingleBoost(p.getName())) {
                return Main.dailyChallenge.getMultiplierSinglePlayer() + "";
            } else {
                return 1 + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_points_remain_single_player%
        if (identifier.contains("dailychallenge_boost_points_remain_single_player")) {
            if (!Main.dailyChallenge.isActiveSingleBoost(p.getName())) {
                if(Main.dailyChallenge.getBoostSinglePlayers().containsKey(p.getName())) {
                    long pointsRemain = Main.dailyChallenge.getBoostSinglePlayers().get(p.getName());
                    return pointsRemain + "";
                } else {
                    return Main.dailyChallenge.getPointsBoostSinglePlayer() + "";
                }
            } else {
                return Main.dailyChallenge.getPointsBoostSinglePlayer() + "";
            }
        }
        return "Placeholder Not Found";
    }

}
