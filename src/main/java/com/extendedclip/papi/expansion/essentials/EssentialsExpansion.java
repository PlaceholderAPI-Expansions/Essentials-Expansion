/*
 *
 * Essentials-Expansion
 * Copyright (C) 2019 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.essentials;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

public class EssentialsExpansion extends PlaceholderExpansion {

	private Essentials essentials;
	
	private final String VERSION = getClass().getPackage().getImplementationVersion();
	
	@Override
	public boolean canRegister() {
		return Bukkit.getPluginManager().getPlugin("Essentials") != null;
	}
	
	@Override
	public boolean register() {
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if (essentials != null) {
			return super.register();
		}
		return false;
	}

	@Override
	public String getAuthor() {
		return "clip";
	}

	@Override
	public String getIdentifier() {
		return "essentials";
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String onRequest(OfflinePlayer p, String identifier) {

		if (p == null) return "";

        if (identifier.startsWith("has_kit_")) {
            if (!p.isOnline()) return "";

            String kit = identifier.split("has_kit_")[1];
            return p.getPlayer().hasPermission("essentials.kits." + kit) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
        }

		switch (identifier) {
            case "unique":
                return NumberFormat.getInstance().format(essentials.getUserMap().getUniqueUsers());
            case "safe_online":
                int playerHidden = 0;
                for (User onlinePlayer : essentials.getOnlineUsers()) {
                    if (onlinePlayer.isHidden())
                        playerHidden++;
                }
                return String.valueOf((essentials.getOnlinePlayers().size() - playerHidden));
            case "worth":
                if (!p.isOnline()) return "";
                if (p.getPlayer().getItemInHand() == null || p.getPlayer().getItemInHand().getType() == Material.AIR) return "";

                BigDecimal worth = essentials.getWorth().getPrice(null, p.getPlayer().getItemInHand());
                if (worth == null) return "";

                return String.valueOf(worth.doubleValue());
        }

        User user = essentials.getUser(p.getUniqueId());
		if (user == null) return "";
		
		if (identifier.startsWith("kit_last_use_")) {
			String kit = identifier.split("kit_last_use_")[1];
			
			Kit k;
			
			try {
				k = new Kit(kit, essentials);
			} catch (Exception e) {
				return "invalid kit";
			}
			
			long time = user.getKitTimestamp(k.getName());
			
			if (time == 1 || time <= 0) {
				return "1";
			}
			return PlaceholderAPIPlugin.getDateFormat().format(new Date(time));
		}
		
		if (identifier.startsWith("kit_is_available_")) {
			String kit = identifier.split("kit_is_available_")[1];
			
			Kit k;
			
			try {
				k = new Kit(kit, essentials);
			} catch (Exception e) {
				return PlaceholderAPIPlugin.booleanFalse();
			}
			
			long time;
			
			try {
				time = k.getNextUse(user);
			} catch (Exception e) {
				return PlaceholderAPIPlugin.booleanFalse();
			}
			
			return time == 0 ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		}
		
		if (identifier.startsWith("kit_time_until_available_")) {
			String kit = identifier.split("kit_time_until_available_")[1];
			
			Kit k;
			
			try {
				k = new Kit(kit, essentials);
			} catch (Exception e) {
				return PlaceholderAPIPlugin.booleanFalse();
			}
			
			long time;
			
			try {
				time = k.getNextUse(user);
			} catch (Exception e) {
				return "-1";
			}
			int seconds = (int)(time - System.currentTimeMillis())/1000;
			
			if (seconds <= 0 || time == 0) {
				return "0";
			}
			return TimeUtil.getTime(seconds);
		}
		
		switch (identifier) {
		case "is_pay_confirm":
			return user.isPromptingPayConfirm() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "is_pay_enabled":
			return user.isAcceptingPay() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "is_teleport_enabled":
			return user.isTeleportEnabled() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
        case "is_muted":
            return user.isMuted() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "vanished":
			return user.isVanished() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "afk":
			return user.isAfk() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "afk_reason":
			if (user.getAfkMessage() == null) return "";
			return ChatColor.translateAlternateColorCodes('&', user.getAfkMessage());
        case "msg_ignore":
            return user.isIgnoreMsg() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
        case "fly":
			return user.isFlyClickJump() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "nickname":
			return user.getNickname() != null ? user.getNickname() : p.getName();
		case "godmode":
			return user.isGodModeEnabled() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
		case "homes_set":
            return user.getHomes().size() == 0 ? String.valueOf(0) : String.valueOf(user.getHomes().size());
        case "homes_max":
            return String.valueOf(essentials.getSettings().getHomeLimit(user));
		case "jailed":
			return String.valueOf(user.isJailed());
		case "pm_recipient":
			return user.getReplyRecipient() != null ? user.getReplyRecipient().getName() : "";
		}
		return null;
	}
}
