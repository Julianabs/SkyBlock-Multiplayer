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

public class PlayerBreackBlockListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlock();

		if (!SkyBlockMultiplayer.settings.getIsOnline()) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) { // Check if player is in world SkyBlockMultiplayer
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

		if (SkyBlockMultiplayer.settings.getGameMode() == GameMode.PVP || !SkyBlockMultiplayer.settings.getWithProtectedArea()) {
			return;
		}

		if (SkyBlockMultiplayer.settings.getGameMode() == GameMode.BUILD) {
			PlayerInfo pi = SkyBlockMultiplayer.settings.getPlayerInfo(player.getName());
			if (pi == null) { // Check, if player is in playerlist
				return;
			}

			if (pi.havePermissionThere(b.getLocation())) {
				return;
			}
			event.setCancelled(true);
			return;

			/*PlayerInfo owner = SkyBlockMultiplayer.getOwner(b.getLocation());
			if (owner == null) {
				if (SkyBlockMultiplayer.canPlayerDoThat(pi, b.getLocation())) {
					return;
				}
				event.setCancelled(true);
				return;
			}

			if (owner.getPlayerName().equalsIgnoreCase(player.getName())) {
				return;
			}

			if (owner.getFriends().contains(player.getName())) {
				return;
			}*/
		}
	}
}
