package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.CreateIsland;
import me.lukas.skyblockmultiplayer.GameMode;
import me.lukas.skyblockmultiplayer.IslandInfo;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block b = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (!SkyBlockMultiplayer.settings.getIsOnline()) {
			return;
		}

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.settings.getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			event.setCancelled(true);
			return;
		}

		boolean hasBuildPermission = false;
		if (b == null) {
			// hasBuildPermission = SkyBlockMultiplayer.checkBuildPermission(pi, player.getLocation());
			hasBuildPermission = pi.havePermissionThere(player.getLocation());
		} else {
			hasBuildPermission = pi.havePermissionThere(player.getLocation());
		}

		if (item != null) {
			if (SkyBlockMultiplayer.settings.getGameMode() == GameMode.BUILD) {
				if (item.getType() == Material.ENDER_PEARL) {
					if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
						if (SkyBlockMultiplayer.settings.getAllowEnderPearl()) {
							if (hasBuildPermission) {
								return;
							}

							event.setCancelled(true);
							return;
						}
						event.setCancelled(true);
						return;
					}
				}
			}

			if (item.getType() == Material.STICK) {
				if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
					String owner = "";
					int islandNumber = -1;
					
					if (b == null) {
						owner = SkyBlockMultiplayer.getOwner(player.getLocation());
					} else {
						owner = SkyBlockMultiplayer.getOwner(b.getLocation());
					}

					if (owner.equals("")) {
						if (b == null) {
							islandNumber = CreateIsland.getIslandNumber(player.getLocation());
						} else {
							islandNumber = CreateIsland.getIslandNumber(b.getLocation());
						}

						if (islandNumber == 0) {
							if (SkyBlockMultiplayer.getInstance().locationIsOnTower(player.getLocation())) {
								player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_AREA_OF_SPAWN_TOWER.getSentence());
								event.setCancelled(true);
								return;
							}

							player.sendMessage(Language.MSGS_AREA_BORDERS.getSentence());
							event.setCancelled(true);
							return;
						}
						player.sendMessage("Island number: " + islandNumber);
						event.setCancelled(true);
						return;
					} else {
						if (b == null) {
							islandNumber = CreateIsland.getIslandNumber(player.getLocation());
						} else {
							islandNumber = CreateIsland.getIslandNumber(b.getLocation());
						}

						if (islandNumber == -1) {
							player.sendMessage(Language.MSGS_AREA_BORDERS.getSentence());
							event.setCancelled(true);
							return;
						}
						player.sendMessage("Island number: " + islandNumber);
					}
					
					player.sendMessage("Owner: " + owner);

					// get friends
					IslandInfo ii = SkyBlockMultiplayer.settings.getIslandInfo(islandNumber);

					if (ii == null) {
						player.sendMessage("called");
						return;
					}

					String list = "";
					boolean first = true;
					for (String name : ii.getFriends()) {
						if (first) {
							first = false;
						} else {
							list += ", ";
						}
						list += name;
					}

					player.sendMessage("Friends: " + list);
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.getPlayer().getLocation().getBlockX() >= -20 && event.getPlayer().getLocation().getBlockX() <= 20) {
			if (event.getPlayer().getLocation().getBlockZ() >= -20 && event.getPlayer().getLocation().getBlockZ() <= 20) {
				if (Permissions.SKYBLOCK_BUILD.has(player)) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			return;
		}

		if (SkyBlockMultiplayer.settings.getGameMode() == GameMode.PVP || !SkyBlockMultiplayer.settings.getWithProtectedArea()) {
			return;
		}

		if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL) {
			if (action == Action.RIGHT_CLICK_BLOCK && item != null && b == null) { // this let allow the event BlockPlace calling
				return;
			}

			if (b == null) {
				if (pi.havePermissionThere(player.getLocation())) {
					return;
				}
			} else {
				if (pi.havePermissionThere(b.getLocation())) {
					return;
				}
			}

			event.setCancelled(true);
			return;
		}
	}
}
