package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.GameMode;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Entity ent = event.getEntity();
		if (ent.getType() != EntityType.PLAYER) {
			return;
		}

		Player player = (Player) ent;
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) { // Exit, if player not in SkyBlock
			return;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		if (!pi.isPlaying()) {
			pi.setOldInventory(player.getInventory().getContents());
			pi.setOldArmor(player.getInventory().getArmorContents());
			pi.setOldExp(player.getExp());
			pi.setOldLevel(player.getLevel());
			pi.setOldFood(player.getFoodLevel());
			pi.setOldHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
			return;
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD && SkyBlockMultiplayer.getInstance().getSettings().getRespawnWithInventory()) {
			pi.setIslandInventory(player.getInventory().getContents());
			pi.setIslandArmor(player.getInventory().getArmorContents());
			pi.setIslandExp(player.getExp());
			pi.setIslandLevel(player.getLevel());
			pi.setIslandFood(player.getFoodLevel());
			pi.setIslandHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
			return;
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD) {
			return;
		}

		pi.setDead(true);
		if (!pi.getHasIsland()) {
			return;
		}

		pi.setLivesLeft(pi.getLivesLeft() - 1);
		if (pi.getIslandsLeft() != 0 || pi.getLivesLeft() != 0) {
			return;
		}

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

		/*if (Settings.numbersPlayers < 1) {
			return;
		}
		Settings.numbersPlayers--;*/

		int amount = this.getAmountOfPlayingPlayers();

		for (PlayerInfo pInfo : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
					pInfo.getPlayer().sendMessage(Language.MSGS_PLAYER_DIED1.getSentence() + amount + Language.MSGS_PLAYER_DIED2.getSentence());
				}
			}
		}

		if (amount == 1) {
			String winner = "";
			for (PlayerInfo pinfo : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
				if (pinfo.isDead() == false) {
					winner = pinfo.getPlayer().getName();
				}
			}

			for (PlayerInfo pInfo : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
				if (pInfo.getPlayer() != null) {
					if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
						pInfo.getPlayer().sendMessage(Language.MSGS_PLAYER_WIN_BROADCAST1.getSentence() + winner + Language.MSGS_PLAYER_WIN_BROADCAST2.getSentence());
					}
				}
			}
			return;
		}
	}

	private int getAmountOfPlayingPlayers() { // TODO: Looking
		int amount = 0;
		for (PlayerInfo pi : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
			if (pi.isPlaying())
				amount++;
		}
		return amount;
	}
}
