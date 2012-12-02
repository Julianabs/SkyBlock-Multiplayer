package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.GameMode;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (!pi.isPlaying() || SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.PVP || pi.getIslandLocation() == null) {
			player.getInventory().setContents(pi.getOldInventory());
			player.getInventory().setArmorContents(pi.getOldArmor());
			player.setExp(pi.getOldExp());
			player.setLevel(pi.getOldLevel());
			player.setFoodLevel(pi.getOldFood());
			player.setHealth(player.getMaxHealth());

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD && SkyBlockMultiplayer.getInstance().getSettings().getRespawnWithInventory()) {
			if (pi.isPlaying() && pi.getIslandLocation() != null) {
				player.getInventory().setContents(pi.getIslandInventory());
				player.getInventory().setArmorContents(pi.getIslandArmor());
				player.setExp(pi.getIslandExp());
				player.setLevel(pi.getIslandLevel());
				player.setFoodLevel(20);
				player.setHealth(player.getMaxHealth());

				SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

				// check if bedrock is still there
				int px = pi.getIslandLocation().getBlockX();
				int py = pi.getIslandLocation().getBlockY() - 3;
				int pz = pi.getIslandLocation().getBlockZ();

				if (new Location(player.getWorld(), px, py, pz).getBlock().getType() != Material.BEDROCK) {
					event.setRespawnLocation(player.getWorld().getSpawnLocation());

					player.getInventory().setContents(pi.getOldInventory());
					player.getInventory().setArmorContents(pi.getOldArmor());
					player.setExp(pi.getOldExp());
					player.setLevel(pi.getOldLevel());
					player.setFoodLevel(pi.getOldFood());
					player.setHealth(player.getMaxHealth());

					SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
					return;
				}

				if (pi.getHomeLocation() == null) {
					SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
					event.setRespawnLocation(pi.getIslandLocation());
				} else {
					Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi.getIslandInfo());
					if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
						player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
						return;
					}

					SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
					event.setRespawnLocation(homeSweetHome);
				}
				return;
			}
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD) {
			if (pi.isPlaying() && !pi.isPlaying()) {
				if (pi.getHomeLocation() == null) {
					event.setRespawnLocation(pi.getIslandLocation());
				} else {
					Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi.getIslandInfo());
					if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
						player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
						return;
					}

					SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
					event.setRespawnLocation(homeSweetHome);
				}
			}
		}
	}
}
