package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
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

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
				return;
			}

			SkyBlockMultiplayer.getInstance().changeToIslandInventory(player);
			return;
		}

		if (event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				SkyBlockMultiplayer.getInstance().locationIsOnTower(event.getTo());
				return;
			}
		}

		if (event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			SkyBlockMultiplayer.getInstance().changeToOldInventory(player);
		}
	}
}
