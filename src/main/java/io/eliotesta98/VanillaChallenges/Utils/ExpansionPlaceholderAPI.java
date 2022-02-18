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
		if (identifier.contains("points")) {
			return MoneyUtils.transform(Main.dailyChallenge.getPointFromPLayerName(p.getName()));
		}
		// %vanillachallenges_dailychallenge_nome%
		if(identifier.contains("dailychallenge_nome")) {
			return Main.currentlyChallengeDB.getNomeChallenge();
		}
		// %vanillachallenges_dailychallenge_time%
		if(identifier.contains("dailychallenge_time")) {
			int timeResume = (Main.currentlyChallengeDB.getTimeResume()/60)/60;
			return timeResume + "";
		}
		// %vanillachallenges_dailychallenge_name_top_#% # = number
		if(identifier.contains("dailychallenge_name_top_")) {
			String number = identifier.replace("dailychallenge_name_top_", "");
			int numberTop = 1;
			try {
				numberTop = Integer.parseInt(number);
			} catch (Exception ex) {
				return "%vanillachallenges_dailychallenge_name_top_" + number + "% is not valid please use a number! Ex: %vanillachallenges_dailychallenge_name_top_1";
			}
			if(numberTop < 1) {
				numberTop = 1;
			}
			ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(numberTop);
			if(top.size() >= numberTop) {
				return top.get(numberTop-1).getNomePlayer();
			} else {
				return top.get(top.size()-1).getNomePlayer();
			}
		}
		// %vanillachallenges_dailychallenge_points_top_#% # = number
		if(identifier.contains("dailychallenge_points_top_")) {
			String number = identifier.replace("dailychallenge_points_top_", "");
			int numberTop = 1;
			try {
				numberTop = Integer.parseInt(number);
			} catch (Exception ex) {
				return "%vanillachallenges_dailychallenge_points_top_" + number + "% is not valid please use a number! Ex: %vanillachallenges_dailychallenge_points_top_1";
			}
			if(numberTop < 1) {
				numberTop = 1;
			}
			ArrayList<Challenger> top = Main.dailyChallenge.getTopPlayers(numberTop);
			if(top.size() >= numberTop) {
				return top.get(numberTop-1).getPoints()+"";
			} else {
				return top.get(top.size()-1).getPoints()+"";
			}
		}
		return "Placeholder Not Found";
	}

}
