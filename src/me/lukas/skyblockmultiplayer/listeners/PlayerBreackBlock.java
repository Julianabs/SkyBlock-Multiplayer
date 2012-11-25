package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.GameMode;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreackBlock implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlock();

		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
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

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD) {
			PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
			if (pi == null) { // Check, if player is in playerlist
				return;
			}

			if (pi.havePermissionThere(b.getLocation())) {
				return;
			}
			event.setCancelled(true);
			return;
		}
	}
}
