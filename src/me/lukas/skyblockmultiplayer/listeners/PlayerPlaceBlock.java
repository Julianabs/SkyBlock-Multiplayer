package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.GameMode;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlock implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlockPlaced();

		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			return;
		}
		
		System.out.println("ca.ee");

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			event.setBuild(true);
			return;
		}

		if (b.getLocation().getBlockX() >= -20 && b.getLocation().getBlockX() <= 20) {
			if (b.getLocation().getBlockZ() >= -20 && b.getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.PVP || !SkyBlockMultiplayer.getInstance().getSettings().getWithProtectedArea()) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			event.setCancelled(true);
			return;
		}

		if (pi.havePermissionThere(b.getLocation())) {
			return;
		}
		event.setCancelled(true);
	}
}
