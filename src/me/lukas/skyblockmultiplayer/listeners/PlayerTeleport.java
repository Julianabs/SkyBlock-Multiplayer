package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerTeleport implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			return;
		}

		if (!event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				return;
			}
		}

		if (!event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
				if (!SkyBlockMultiplayer.getInstance().locationIsOnTower(event.getTo())) {
					event.setCancelled(true);
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
					return;
				}
			}
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			if (SkyBlockMultiplayer.getInstance().locationIsOnTower(event.getTo())) {
				event.setCancelled(true);
				return;
			}
			if (SkyBlockMultiplayer.getInstance().getSettings().getWithProtectedArea()) {
				if (pi.havePermissionThere(event.getTo())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}

		if (event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			event.setCancelled(true);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.getSentence());
		}
	}
}
