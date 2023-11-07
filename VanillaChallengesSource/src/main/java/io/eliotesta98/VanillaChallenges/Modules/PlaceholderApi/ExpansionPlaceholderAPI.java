package io.eliotesta98.VanillaChallenges.Modules.PlaceholderApi;

import io.eliotesta98.VanillaChallenges.Core.Main;
import io.eliotesta98.VanillaChallenges.Database.Objects.Challenger;
import io.eliotesta98.VanillaChallenges.Database.Objects.PlayerStats;
import io.eliotesta98.VanillaChallenges.Utils.MoneyUtils;
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
            return MoneyUtils.transform(Main.instance.getDailyChallenge().getPointFromPLayerName(p.getName()));
        }
        // %vanillachallenges_dailychallenge_name%
        if (identifier.contains("dailychallenge_name")) {
            return Main.instance.getDailyChallenge().getChallengeName();
        }
        // %vanillachallenges_dailychallenge_displayName%
        if (identifier.contains("dailychallenge_displayName")) {
            return Main.instance.getDailyChallenge().getNameChallenge();
        }
        // %vanillachallenges_dailychallenge_time%
        if (identifier.contains("dailychallenge_time")) {
            return Main.instance.getDailyChallenge().getTimeChallenge() + "";
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
            ArrayList<Challenger> top = Main.instance.getDailyChallenge().getTopPlayers(numberTop);
            if (top.size() >= numberTop) {
                return top.get(numberTop - 1).getNomePlayer();
            } else {
                return "Nobody";
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
            ArrayList<Challenger> top = Main.instance.getDailyChallenge().getTopPlayers(numberTop);
            if (top.size() >= numberTop) {
                return MoneyUtils.transform(top.get(numberTop - 1).getPoints());
            } else {
                return 0 + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_multiplier%
        if (identifier.equalsIgnoreCase("dailychallenge_boost_multiplier")) {
            if (Main.instance.getDailyChallenge().isActive()) {
                return Main.instance.getDailyChallenge().getMultiplier() + "";
            } else {
                return 1 + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_points_remain%
        if (identifier.equalsIgnoreCase("dailychallenge_boost_points_remain")) {
            if (!Main.instance.getDailyChallenge().isActive()) {
                long pointsRemain = Main.instance.getDailyChallenge().getPointsBoost() - Main.instance.getDailyChallenge().getCountPointsChallenge();
                return MoneyUtils.transform(pointsRemain) + "";
            } else {
                return MoneyUtils.transform(Main.instance.getDailyChallenge().getPointsBoost()) + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_multiplier_single_player%
        if (identifier.contains("dailychallenge_boost_multiplier_single_player")) {
            if (Main.instance.getDailyChallenge().isActiveSingleBoost(p.getName())) {
                return Main.instance.getDailyChallenge().getMultiplierSinglePlayer() + "";
            } else {
                return 1 + "";
            }
        }
        // %vanillachallenges_dailychallenge_boost_points_remain_single_player%
        if (identifier.contains("dailychallenge_boost_points_remain_single_player")) {
            if (!Main.instance.getDailyChallenge().isActiveSingleBoost(p.getName())) {
                if (Main.instance.getDailyChallenge().getBoostSinglePlayers().containsKey(p.getName())) {
                    long pointsRemain = Main.instance.getDailyChallenge().getBoostSinglePlayers().get(p.getName());
                    return pointsRemain + "";
                } else {
                    return MoneyUtils.transform(Main.instance.getDailyChallenge().getPointsBoostSinglePlayer()) + "";
                }
            } else {
                return MoneyUtils.transform(Main.instance.getDailyChallenge().getPointsBoostSinglePlayer()) + "";
            }
        }
        // %vanillachallenges_top_victories_name_#% # = number
        if (identifier.contains("top_victories_name_")) {
            String number = identifier.replace("top_victories_name_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_victories_name_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_victories_name_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopVictories().get(numberTop - 1).getPlayerName();
            } catch (IndexOutOfBoundsException exception) {
                return "";
            }
        }
        // %vanillachallenges_top_victories_points_#% # = number
        if (identifier.contains("top_victories_points_")) {
            String number = identifier.replace("top_victories_points_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_victories_points_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_victories_points_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopVictories().get(numberTop - 1).getNumberOfVictories() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_first_place_name_#% # = number
        if(identifier.contains("top_first_place_name_")) {
            String number = identifier.replace("top_first_place_name_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_first_place_name_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_first_place_name_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopFirstPlace().get(numberTop - 1).getPlayerName() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_first_place_points_#% # = number
        if(identifier.contains("top_first_place_points_")) {
            String number = identifier.replace("top_first_place_points_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_first_place_points_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_first_place_points_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopFirstPlace().get(numberTop - 1).getNumberOfFirstPlace() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_second_place_name_#% # = number
        if(identifier.contains("top_second_place_name_")) {
            String number = identifier.replace("top_second_place_name_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_second_place_name_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_second_place_name_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopSecondPlace().get(numberTop - 1).getPlayerName() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_second_place_points_#% # = number
        if(identifier.contains("top_second_place_points_")) {
            String number = identifier.replace("top_second_place_points_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_second_place_points_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_second_place_points_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopSecondPlace().get(numberTop - 1).getNumberOfSecondPlace() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_third_place_name_#% # = number
        if(identifier.contains("top_third_place_name_")) {
            String number = identifier.replace("top_third_place_name_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_third_place_name_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_third_place_name_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopThirdPlace().get(numberTop - 1).getPlayerName() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        // %vanillachallenges_top_third_place_points_#% # = number
        if(identifier.contains("top_third_place_points_")) {
            String number = identifier.replace("top_third_place_points_", "");
            int numberTop = 1;
            try {
                numberTop = Integer.parseInt(number);
            } catch (Exception ex) {
                return "%vanillachallenges_top_third_place_points_" + number + "% is not valid please use a number! Ex: %vanillachallenges_top_third_place_points_1%";
            }
            if (numberTop < 1) {
                numberTop = 1;
            }
            try {
                return Main.db.getTopThirdPlace().get(numberTop - 1).getNumberOfThirdPlace() + "";
            } catch (IndexOutOfBoundsException exception) {
                return "0";
            }
        }

        return "Placeholder Not Found";
    }

}
