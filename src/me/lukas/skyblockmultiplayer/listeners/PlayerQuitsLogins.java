package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.IslandInfo;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitsLogins implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		SkyBlockMultiplayer.getInstance().getSettings().removePlayerInfo(player.getName());

		for (IslandInfo ii : pi.getBuiltPermissionList().values()) {
			SkyBlockMultiplayer.getInstance().getSettings().removeIslandInfoIfNoBuilder(ii);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		// remove entites around player
		SkyBlockMultiplayer.getInstance().removeCreatures(player.getLocation());
	}
}
