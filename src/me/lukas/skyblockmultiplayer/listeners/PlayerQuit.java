package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.settings.getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}
		
		SkyBlockMultiplayer.settings.removePlayer(player.getName());
	}
}
