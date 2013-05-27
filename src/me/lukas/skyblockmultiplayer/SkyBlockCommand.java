package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyBlockCommand implements CommandExecutor {

	private HashMap<String, Long> listRestartIslands = new HashMap<String, Long>();

	/**
	 * 	
	 * @param sender that types the com mand.
	 * @param cmd
	 * @param label 
	 * @param  args array that includes all given arguments.
	 * @return boolean command exists or not
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				sender.sendMessage(SkyBlockMultiplayer.getInstance().getDescription().getName() + " v" + SkyBlockMultiplayer.getInstance().getDescription().getVersion());
				sender.sendMessage(Language.MSGS_SKYBLOCK.getSentence());
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1) {
					return this.getListCommands(sender, "1");
				} else {
					return this.getListCommands(sender, args[1]);
				}
			}

			// only for testing
			if (args[0].equalsIgnoreCase("create")) {
				CreateIsland.createIslands(Integer.parseInt(args[1]));
				return true;
			}

			if (args[0].equalsIgnoreCase("amount")) {
				sender.sendMessage("" + SkyBlockMultiplayer.getInstance().getAmountOfIslands());
				return true;
			}
			// ----

			if (args[0].equalsIgnoreCase("tower")) {
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("recreate")) {
						if (!Permissions.SKYBLOCK_BUILD.has(sender)) {
							return this.notAuthorized(sender);
						}

						Location locTower = new Location(SkyBlockMultiplayer.getInstance().getSkyBlockWorld(), 0, SkyBlockMultiplayer.getInstance().getSettings().getTowerYPosition(), 0);

						File f = new File(SkyBlockMultiplayer.getInstance().getSettings().getTowerSchematic());
						if (f.exists() && f.isFile()) {
							try {
								int res = CreateIsland.createStructure(locTower, f);
								if (res != 1) {
									SpawnTower.createSpawnTower();
									if (res == 0) {
										SkyBlockMultiplayer.getInstance().getLogger().warning("Tower contains no bedrock.");
									} else {
										SkyBlockMultiplayer.getInstance().getLogger().warning("Tower contains too much bedrock.");
									}
								}
								SkyBlockMultiplayer.getInstance().getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.getSentence());
								return true;
							} catch (Exception e) {
								e.printStackTrace();
								SpawnTower.createSpawnTower();
								SkyBlockMultiplayer.getInstance().getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								return true;
							}
						}

						f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), SkyBlockMultiplayer.getInstance().getSettings().getTowerSchematic());
						if (f.exists() && f.isFile()) {
							try {
								int res = CreateIsland.createStructure(locTower, f);
								if (res != 1) {
									SpawnTower.createSpawnTower();
									if (res == 0) {
										SkyBlockMultiplayer.getInstance().getLogger().warning("Tower contains no bedrock.");
									} else {
										SkyBlockMultiplayer.getInstance().getLogger().warning("Tower contains too much bedrock.");
									}
								}
								SkyBlockMultiplayer.getInstance().getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.getSentence());
								return true;
							} catch (Exception e) {
								e.printStackTrace();
								SpawnTower.createSpawnTower();
								SkyBlockMultiplayer.getInstance().getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								return true;
							}
						}

						SpawnTower.createSpawnTower();
						SkyBlockMultiplayer.getInstance().getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
						sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.getSentence());
						return true;
					}
				}
			}

			if (args[0].equalsIgnoreCase("set")) {
				if (!Permissions.SKYBLOCK_SET.has(sender)) {
					return this.notAuthorized(sender);
				}

				if (args.length < 2) {
					sender.sendMessage("Missing arguments.");
					sender.sendMessage("/skb set offline");
					sender.sendMessage("/skb set online");
					return true;
				}
				if (args[1].equalsIgnoreCase("offline")) {
					return this.setSkyBlockOffline(sender);
				}
				if (args[1].equalsIgnoreCase("online")) {
					return this.setSkyBlockOnline(sender);
				}
				if (args.length < 3) {
					sender.sendMessage("Missing arguments.");
					sender.sendMessage("/skb set language <language>");
					sender.sendMessage("/skb set gamemode <pvp, build>");
					return true;
				}
				if (args[1].equalsIgnoreCase("language")) {
					return this.setLanguage(sender, args[2]);
				}
				if (args[1].equalsIgnoreCase("gamemode") || args[1].equalsIgnoreCase("gm")) {
					return this.setGameMode(sender, args[2]);
				}
			}
			if (args[0].equalsIgnoreCase("reset")) {
				return this.resetSkyBlock(sender);
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length < 2) {
					sender.sendMessage("Missing arguments.");
					sender.sendMessage("/skb reload config");
					sender.sendMessage("/skb reload language");
					return true;
				}
				if (args[1].equalsIgnoreCase("config")) {
					return this.reloadConfig(sender);
				}
				if (args[1].equalsIgnoreCase("language")) {
					return this.reloadLanguage(sender);
				}
			}
			if (args[0].equalsIgnoreCase("status")) {
				return this.getStatus(sender);
			}

			if (!(sender instanceof Player)) {
				return true;
			}

			Player player = (Player) sender;

			/*if (args[0].equalsIgnoreCase("setowner")) { // TODO
				if (args.length < 2) {
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.getSentence());
					return true;
				}
				return this.setOwner(player, args[1], args[2]);
			}*/

			if (args[0].equalsIgnoreCase("tower")) {
				return this.toTower(player);
			}

			if (args[0].equalsIgnoreCase("join")) {
				return this.playerJoin(player);
			}
			if (args[0].equalsIgnoreCase("play")) {
				return this.playerStart(player);
			}
			if (args[0].equalsIgnoreCase("leave")) {
				return this.playerLeave(player);
			}
			if (args[0].equalsIgnoreCase("newIsland")) {
				String s = "";
				if (args.length < 2) {
					s = "";
				} else {
					s = args[1];
				}
				return this.playerNewIsland(player, s);
			}

			if (args[0].equalsIgnoreCase("newisland")) {
				if (args.length == 1) {
					return this.restartIsland(player, "");
				}

				return this.restartIsland(player, args[1]);
			}

			if (args[0].equalsIgnoreCase("remove")) { // TODO: Rechange this
				if (args.length == 0) {
					sender.sendMessage("Missing arguments.");
					player.sendMessage("/skb remove <island number>");
					return true;
				}
				return this.removeIsland(player, args[1]);
			}

			if (args[0].equalsIgnoreCase("home")) {
				if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.PVP) {
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INBUILD_MODE.getSentence());
					return true;
				}

				if (args.length == 1) {
					return this.homeTeleport(player);
				}

				if (args[1].equalsIgnoreCase("set")) {
					return this.homeSet(player);
				}

				if (args[1].equalsIgnoreCase("list")) {
					return this.homeList(player);
				}

				if (args[1].equalsIgnoreCase("join")) {
					if (args.length == 2) {
						sender.sendMessage("Missing arguments.");
						player.sendMessage("/skb home join <player name>");
						return true;
					}
					return this.homeJoin(player, args[2]);
				}

				if (args[1].equalsIgnoreCase("add")) {
					if (args.length == 2) {
						sender.sendMessage("Missing arguments.");
						player.sendMessage("/skb home add <player name>");
						return true;
					}
					return this.homeAdd(player, args[2]);
				}

				if (args[1].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						sender.sendMessage("Missing arguments.");
						player.sendMessage("/skb home remove <player name>");
						return true;
					}
					return this.homeRemove(player, args[2]);
				}
			}

			// player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.getSentence());
			player.sendMessage("Wrong or missing argument.");
			return true;
		}
		return false;
	}

	/**
	 * Send a list of all commands.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean getListCommands(CommandSender sender, String p) {
		String sb_join = Language.MSGS_COMMAND_JOIN.getSentence() + "\n";
		String sb_start = Language.MSGS_COMMAND_START.getSentence() + "\n";
		String sb_tower = Language.MSGS_COMMAND_TOWER.getSentence() + "\n";
		String sb_leave = Language.MSGS_COMMAND_LEAVE.getSentence() + "\n";
		String sb_status = Language.MSGS_COMMAND_STATUS.getSentence() + "\n";
		String sb_home = Language.MSGS_COMMAND_HOME.getSentence() + "\n";
		String sb_home_add = Language.MSGS_COMMAND_HOME_ADD.getSentence() + "\n";
		String sb_home_remove = Language.MSGS_COMMAND_HOME_REMOVE.getSentence() + "\n";
		String sb_home_join = Language.MSGS_COMMAND_HOME_JOIN.getSentence() + "\n";
		String sb_home_list = Language.MSGS_COMMAND_HOME_LIST.getSentence() + "\n";
		String sb_home_set = Language.MSGS_COMMAND_HOME_SET.getSentence() + "\n";

		String sb_newIsland = Language.MSGS_COMMAND_NEW_ISLAND.getSentence() + "\n";
		String sb_setOffline = Language.MSGS_COMMAND_SET_OFFLINE.getSentence() + "\n";
		String sb_setOnline = Language.MSGS_COMMAND_SET_ONLINE.getSentence() + "\n";
		String sb_tower_recreate = Language.MSGS_COMMAND_TOWER_RECREATE.getSentence() + "\n";
		String sb_setLanguage = Language.MSGS_COMMAND_SET_LANGUAGE.getSentence() + "\n";
		String sb_setGameMode = Language.MSGS_COMMAND_SET_GAMEMODE.getSentence() + "\n";
		String sb_removeIsland = "�6/skyblock remove �c<island number>�7 - remove island with given number\n";
		String sb_setOwner = Language.MSGS_COMMAND_SET_OWNER.getSentence() + "\n";
		String sb_reset = Language.MSGS_COMMAND_RESET.getSentence() + "\n";
		String sb_reload_config = Language.MSGS_COMMAND_RELOAD_CONFIG.getSentence() + "\n";
		String sb_reload_language = Language.MSGS_COMMAND_RELOAD_LANGUAGE.getSentence() + "\n";

		int page = 1;
		try {
			page = Integer.parseInt(p);
		} catch (Exception e) {
			page = 1;
		}

		String pluginName = SkyBlockMultiplayer.getInstance().pName.replace("[", "").replace("]", "");
		if (page <= 1) {
			String top = ChatColor.GOLD + "----- " + pluginName + " help index (1/2) " + ChatColor.GOLD + " -----\n" + ChatColor.WHITE;
			String msgs = top + sb_join + sb_start + sb_tower + sb_leave + sb_status + sb_home + sb_home_add + sb_home_remove + sb_home_join + sb_home_list + sb_home_set;

			for (String s : msgs.split("\n")) {
				if (!s.trim().equalsIgnoreCase("")) {
					sender.sendMessage(s);
				}
			}
			return true;
		} else if (page >= 2) {
			String top = ChatColor.GOLD + "----- " + pluginName + " help index (2/2) " + ChatColor.GOLD + " -----\n" + ChatColor.WHITE;
			String msgs = top + sb_newIsland + sb_setOffline + sb_setOnline + sb_tower_recreate + sb_setLanguage + sb_setGameMode + sb_removeIsland + sb_setOwner + sb_reset + sb_reload_config + sb_reload_language;

			for (String s : msgs.split("\n")) {
				if (!s.trim().equalsIgnoreCase("")) {
					sender.sendMessage(s);
				}
			}
			return true;
		}
		return true;
	}

	/**
	 * Send message not authorized to the sender.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean notAuthorized(CommandSender s) {
		s.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NOT_AUTHORIZED.getSentence());
		return true;
	}

	/**
	 * Activate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyBlockOffline(CommandSender sender) {
		try {
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_STOPPING.getSentence());
			if (SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getPlayers().size() > 0) {
				sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_PLAYERS_IN_SB.getSentence());
				return true;
			}

			Bukkit.getServer().unloadWorld(SkyBlockMultiplayer.getInstance().getSettings().getWorldName(), true);
			SkyBlockMultiplayer.getInstance().setSkyBlockWorldNull();
			SkyBlockMultiplayer.getInstance().getSettings().setIsOnline(false);
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, EnumPluginConfig.OPTIONS_SKYBLOCKONLINE.getPath(), false);
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_OFFLINE.getSentence());
			return true;

		} catch (Exception ex) {
			SkyBlockMultiplayer.getInstance().getLogger().warning(ex.getMessage());
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ERROR_OCCURED.getSentence());
			return true;
		}
	}

	/**
	 * Deactivate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyBlockOnline(CommandSender sender) {
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_STARTING.getSentence());
		SkyBlockMultiplayer.getInstance().setSkyBlockWorldNull();
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld();
		SkyBlockMultiplayer.getInstance().getSettings().setIsOnline(true);
		SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, EnumPluginConfig.OPTIONS_SKYBLOCKONLINE.getPath(), true);
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_ONLINE.getSentence());
		return true;
	}

	/**
	 * Lock SkyBlock.
	 * 
	 * @param sender
	 * @return
	 */
	private boolean setOpened(CommandSender sender) {
		SkyBlockMultiplayer.getInstance().getSettings().setIsLocked(false);
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_OPENED.getSentence());
		return true;
	}

	/**
	 * Unlock SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	private boolean setClosed(CommandSender sender) {
		SkyBlockMultiplayer.getInstance().getSettings().setIsLocked(true);
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_CLOSED.getSentence());
		return true;
	}

	/**
	 * Change the lanugage.
	 * 
	 * @param sender that types the command.
	 * @param The given language.
	 * @return returns true
	 */
	private boolean setLanguage(CommandSender sender, String s) {
		if (!SkyBlockMultiplayer.getInstance().getSettings().getLanguage().equalsIgnoreCase(s)) {
			File f = new File(SkyBlockMultiplayer.getInstance().getDataFolder() + File.separator + "language", s + ".yml");
			File sf = SkyBlockMultiplayer.getInstance().fileLanguage;
			String encoding = "UTF-8";
			if (f.exists()) {
				try {
					SkyBlockMultiplayer.getInstance().fileLanguage = f;
					Scanner scanner = new Scanner(new FileInputStream(SkyBlockMultiplayer.getInstance().fileLanguage), encoding);
					String contentToRead = "";
					while (scanner.hasNextLine()) {
						contentToRead += scanner.nextLine() + System.getProperty("line.separator");
					}
					scanner.close();
					try {
						SkyBlockMultiplayer.getInstance().configLanguage.loadFromString(contentToRead);
					} catch (Exception e) {
						encoding = "Cp1252";
						scanner = new Scanner(new FileInputStream(SkyBlockMultiplayer.getInstance().fileLanguage), encoding);
						contentToRead = "";
						while (scanner.hasNextLine()) {
							contentToRead += scanner.nextLine() + System.getProperty("line.separator");
						}
						scanner.close();
						SkyBlockMultiplayer.getInstance().configLanguage.loadFromString(contentToRead);
					}
					SkyBlockMultiplayer.getInstance().loadLanguageConfig();
					SkyBlockMultiplayer.getInstance().getSettings().setLanguage(s);
					SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, EnumPluginConfig.OPTIONS_LANGUAGE.getPath(), s);
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_CHANGED.getSentence());
					return true;
				} catch (Exception e) {
					SkyBlockMultiplayer.getInstance().fileLanguage = sf;
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ERROR_OCCURED.getSentence() + ": " + e.getLocalizedMessage());
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_NOT_CHANGED.getSentence());
					return true;
				}
			} else {
				sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_FILE_NOT_EXISTS.getSentence());
				return true;
			}
		} else {
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_NOT_CHANGED.getSentence());
			return true;
		}
	}

	/**
	 * Change gamemode of SkyBlock.
	 * 
	 * @param sender
	 * @param s
	 * @return
	 */
	private boolean setGameMode(CommandSender sender, String s) {
		if (s.equalsIgnoreCase("build")) {
			SkyBlockMultiplayer.getInstance().getSettings().setGameMode(GameMode.BUILD);
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, EnumPluginConfig.OPTIONS_GAMEMODE.getPath(), "build");
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GAMEMODE_CHANGED.getSentence());
			return true;
		}
		if (s.equalsIgnoreCase("pvp")) {
			SkyBlockMultiplayer.getInstance().getSettings().setGameMode(GameMode.PVP);
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, EnumPluginConfig.OPTIONS_GAMEMODE.getPath(), "pvp");
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GAMEMODE_CHANGED.getSentence());
			return true;
		}
		sender.sendMessage("/skb set gamemode <pvp, build>");
		return true;
	}

	/**
	 * Reset the world SkyBlock, and the players.yml.
	 * 
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean resetSkyBlock(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RESET.has(sender)) {
			return this.notAuthorized(sender);
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + ChatColor.RED + Language.MSGS_MUST_BEOFFLINE.getSentence());
			return true;
		}

		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_RESETING.getSentence());
		Bukkit.getServer().unloadWorld(SkyBlockMultiplayer.getInstance().getSettings().getWorldName(), true);

		for (PlayerInfo pi : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
			pi.setIslandInfo(null);
			pi.setIslandsLeft(SkyBlockMultiplayer.getInstance().getSettings().getIslandsPerPlayer());
			pi.setLivesLeft(SkyBlockMultiplayer.getInstance().getSettings().getLivesPerIsland());
			pi.setIslandLocation(null);
			pi.setHomeLocation(null);
			pi.setDead(false);
			pi.setIslandFood(20);
			pi.setIslandExp(0);
			pi.setIslandLevel(0);

			pi.setIslandInventory(null);
			pi.setIslandArmor(null);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		}

		this.sfiles = new ArrayList<File>();
		this.getAllFiles(SkyBlockMultiplayer.getInstance().getSettings().getWorldName());

		for (File f : this.sfiles) {
			f.delete();
		}

		// Create Skyblock
		SkyBlockMultiplayer.getInstance().setSkyBlockWorldNull();
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld();
		SpawnTower.createSpawnTower();

		// Reset informations
		SkyBlockMultiplayer.getInstance().getSettings().resetPlayerInfos();
		//SkyBlockMultiplayer.getInstance().getSettings().numberIslands = 0;

		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_RESETED.getSentence());
		return true;
	}

	private ArrayList<File> sfiles;

	/**
	 * Get all files, directories inside of the given path.
	 * 
	 * @param path the directory. 
	 */
	private void getAllFiles(String path) {

		File dirpath = new File(path);
		if (!dirpath.exists()) {
			return;
		}

		for (File f : dirpath.listFiles()) {
			try {
				if (!f.isDirectory()) {
					this.sfiles.add(f);
				} else {
					this.getAllFiles(f.getAbsolutePath());
				}
			} catch (Exception ex) {
				SkyBlockMultiplayer.getInstance().getLogger().warning(ex.getMessage());
			}
		}
	}

	/**
	 * Reloads the config.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean reloadConfig(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		SkyBlockMultiplayer.getInstance().loadPluginConfig();
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_CONFIG_RELOADED.getSentence());
		return true;
	}

	/**
	 * Reloads the language.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 * @throws  
	 */
	private boolean reloadLanguage(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		try {
			SkyBlockMultiplayer.getInstance().loadLanguageConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_RELOADED.getSentence());
		return true;
	}

	/**
	 * Get informations about SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	private boolean getStatus(CommandSender sender) {
		if (SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			sender.sendMessage(Language.MSGS_STATUS_ONLINE.getSentence());
		} else {
			sender.sendMessage(Language.MSGS_STATUS_ONLINE.getSentence());
		}

		int islands = SkyBlockMultiplayer.getInstance().getAmountOfIslands();

		sender.sendMessage(Language.MSGS_NUMBER_OF_ISLANDS.getSentence() + islands);
		sender.sendMessage(Language.MSGS_NUMBER_OF_PLAYERS.getSentence() + SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().size());
		return true;
	}

	/**
	 * Join the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true
	 */
	private boolean playerJoin(Player player) {
		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.getSentence());
			return true;
		}

		if (player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.getSentence());
			return true;
		}

		/*if (!Permissions.SKYBLOCK_JOIN.has(player)) {
			return this.notAuthorized(player); // TODO: Add this after finish
		}*/

		if (SkyBlockMultiplayer.getInstance().getSettings().getIsLocked()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_CLOSED.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			pi = new PlayerInfo(player.getName());
		}
		pi.setOldLocation(player.getLocation());

		int islands = SkyBlockMultiplayer.getInstance().getAmountOfIslands();

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

		player.teleport(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getSpawnLocation()); // teleport player to the spawn tower
		// System.out.println("join: " + SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getSpawnLocation());
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME1.getSentence() + islands + Language.MSGS_WELCOME2.getSentence() + SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getPlayers().size() + Language.MSGS_WELCOME3.getSentence());
		return true;
	}

	/**
	 * Get an island in the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerStart(Player player) {
		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.getSentence());
			return true;
		}

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			pi = new PlayerInfo(player.getName());
		}

		if (pi.isPlaying()) {
			// player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.getSentence());
			return this.homeTeleport(player);
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD) {
			if (!pi.getHasIsland() || pi.getIslandLocation() == null) {
				// new player
				pi.setHomeLocation(null);
				pi.setIslandInfo(null);
				pi.setDead(false);
				pi.setIslandInfo(CreateIsland.createNextIsland());
				pi.getIslandInfo().setIslandOwner(player.getName());
				pi.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(pi.getIslandLocation()));

				if (!player.teleport(SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi.getIslandInfo())))
					return true;

				SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);
				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());

				// send message to all
				for (PlayerInfo pInfo : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
					if (pInfo.getPlayer() != null) {
						if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
							pInfo.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BROADCAST1.getSentence() + player.getName() + Language.MSGS_WELCOME_BROADCAST2.getSentence());
						}
					}
				}

				SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
				SkyBlockMultiplayer.getInstance().saveIslandInfo(pi.getIslandInfo());
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.getSentence());
				return true;
			}

			// teleport player
			if (pi.getHomeLocation() == null) {
				if (!player.teleport(pi.getIslandLocation()))
					return true;

				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
			} else {
				Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi.getIslandInfo());
				if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Cannot teleport to your home location, your island is probably missing.");
					return true;
				}
				if (!player.teleport(homeSweetHome))
					return true;

				SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
			}

			SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.getSentence() + player.getName());
			return true;
		}

		// Game mode is PVP
		if (pi.getHasIsland()) { // player have a island
			if (pi.isDead()) {
				if (pi.getLivesLeft() == 0) {
					if (pi.getIslandsLeft() == 0) {
						// no more lives and islands left
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_MORELIVES_AND_ISLANDS.getSentence());
						return true;
					}

					// no more lives left, decrement islandsLeft
					pi.setLivesLeft(SkyBlockMultiplayer.getInstance().getSettings().getLivesPerIsland());
					pi.setIslandsLeft(pi.getIslandsLeft() - 1);

					// create new island and teleport player
					pi.setIslandInfo(CreateIsland.createNextIsland());
					pi.getIslandInfo().setIslandOwner(player.getName());
					pi.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(pi.getIslandLocation()));
					pi.setDead(false);

					if (!player.teleport(pi.getIslandLocation()))
						return true;

					SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());

					SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

					SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
					SkyBlockMultiplayer.getInstance().saveIslandInfo(pi.getIslandInfo());
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.getSentence());
					return true;
				}

				// lives on island left
				pi.setDead(false);

				// teleport player
				if (!player.teleport(pi.getIslandLocation()))
					return true;

				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());

				SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

				SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.getSentence() + player.getName());
				return true;
			}

			// Player is not dead and has a island

			// teleport player
			if (!player.teleport(pi.getIslandLocation()))
				return true;

			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());

			SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.getSentence() + player.getName());
			return true;
		}

		// player is new
		pi.setIslandInfo(CreateIsland.createNextIsland());
		pi.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(pi.getIslandLocation()));

		pi.setIslandsLeft(SkyBlockMultiplayer.getInstance().getSettings().getIslandsPerPlayer());
		pi.setLivesLeft(SkyBlockMultiplayer.getInstance().getSettings().getLivesPerIsland());
		pi.setDead(false);
		pi.setIslandsLeft(pi.getIslandsLeft() - 1);

		// teleport player
		if (!player.teleport(pi.getIslandLocation()))
			return true;

		SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());

		SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.getSentence());
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

		// Message to all
		for (PlayerInfo pInfo : SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
					pInfo.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BROADCAST1.getSentence() + player.getName() + Language.MSGS_WELCOME_BROADCAST2.getSentence());
				}
			}
		}
		return true;
	}

	/**
	 * Leave SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerLeave(Player player) {
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LEFT_SKYBLOCK.getSentence());
			return true;
		}

		if (pi.isPlaying()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.getSentence());
			return true;
		}

		Location l = pi.getOldLocation();
		if (l == null) {
			player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}

		SkyBlockMultiplayer.getInstance().getSettings().removePlayerInfo(player.getName());

		for (IslandInfo ii : pi.getBuiltPermissionList().values()) {
			SkyBlockMultiplayer.getInstance().getSettings().removeIslandInfoIfNoBuilder(ii);
		}

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		return true;
	}

	/**
	 * Gives the sender or an other player a new island
	 * 
	 * @param player that types the command.
	 * @param target the player that is given.
	 * @return returns true.
	 */
	private boolean playerNewIsland(Player player, String target) {
		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.getSentence());
			return true;
		}

		/*if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.getSentence());
			return true;
		}*/

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() == GameMode.BUILD) {
			PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
			if (pi == null) {
				return true;
			}

			SkyBlockMultiplayer.getInstance().removeIsland(pi.getIslandLocation());
			pi.setIslandInfo(null);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
			this.playerStart(player);
			// player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEWISLANDPLAYER1.getSentence() + pi.getPlayer().getName() + Language.MSGS_NEWISLANDPLAYER2.getSentence());
			return true;
		}

		if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		PlayerInfo pi = null;
		String res = "";
		if (target.trim().equalsIgnoreCase("")) {
			pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		} else {
			res = SkyBlockMultiplayer.getInstance().getFullPlayerName(pi, target);
			if (res.equalsIgnoreCase("-1")) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
				return true;
			}
			if (res.equalsIgnoreCase("0")) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.getSentence());
				return true;
			}
			pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(res);
		}

		if (pi == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
			return true;
		}

		pi.setDead(true);
		pi.setIslandInventory(null);
		pi.setIslandArmor(null);
		pi.setIslandsLeft(pi.getIslandsLeft() + 1);

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEW_ISLAND_PLAYER1.getSentence() + pi.getPlayer().getName() + Language.MSGS_NEW_ISLAND_PLAYER2.getSentence());
		pi.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GOT_NEW_ISLAND1.getSentence() + player.getName() + Language.MSGS_GOT_NEW_ISLAND2.getSentence());
		return true;
	}

	/**
	 * Change the owner of an island.
	 * 
	 * @param player
	 * @param number of the island
	 * @param newOwner the new player who gets this island
	 * @return
	 */
	/*private boolean setOwner(Player player, String number, String newOwner) {
		if (!Permissions.SKYBLOCK_OWNER_SET.has(player)) {
			return this.notAuthorized(player);
		}

		int islandNumber = -1;
		try {
			islandNumber = Integer.parseInt(number);
			if (islandNumber <= 0 || islandNumber > CreateIsland.getAmountOfIslands()) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.getSentence());
				return true;
			}
		} catch (Exception e) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.getSentence());
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(pi, newOwner);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.getSentence());
			return true;
		}

		PlayerInfo pi = new PlayerInfo(res);
		if (SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().containsKey(res)) {
			PlayerInfo oldPi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().get(res);
			pi.setOldLocation(oldPi.getOldLocation());
			pi.setOldInventory(oldPi.getOldInventory());
			pi.setOldArmor(pi.getOldArmor());
			pi.setOldExp(oldPi.getOldExp());
			pi.setOldLevel(oldPi.getOldLevel());
			pi.setOldFood(oldPi.getOldFood());
			pi.setOldHealth(oldPi.getOldHealth());
			SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfos().remove(res);
		}

		pi.setIslandLocation(CreateIsland.getIslandPosition(islandNumber));

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);

		SkyBlockMultiplayer.getInstance().getSettings().addPlayer(res, pi);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_CHANGED_OWNER_TO.getSentence() + res);
		return true;
	}*/

	/**
	 * Teleport to tower.
	 * 
	 * @param player
	 * @return
	 */
	private boolean toTower(Player player) {
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
			return true;
		}

		if (SkyBlockMultiplayer.getInstance().locationIsOnTower(player.getLocation())) {
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			/*pi = new PlayerInfo(player.getName());
			// pi.setOldLocation(player.getLocation());

			SkyBlockMultiplayer.getInstance().getSettings().addPlayer(player.getName(), pi);
			if (!player.teleport(player.getWorld().getSpawnLocation()))
				return true;

			SkyBlockMultiplayer.getInstance().changeToOldInventory(pi);

			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);*/
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BACK_ON_TOWER.getSentence());
			return true;
		}

		if (!player.teleport(player.getWorld().getSpawnLocation()))
			return true;

		SkyBlockMultiplayer.getInstance().changeToOldInventory(pi);

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BACK_ON_TOWER.getSentence());
		return true;
	}

	/**
	 * Remove a island.
	 * 
	 * @param player
	 * @param number
	 * @return
	 */
	private boolean removeIsland(Player player, String number) {
		if (!SkyBlockMultiplayer.getInstance().getSettings().getIsOnline()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.getSentence());
			return true;
		}

		if (!Permissions.SKYBLOCK_REMOVE_ISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		int islandNumber = 0;
		try {
			islandNumber = Integer.parseInt(number);
		} catch (Exception e) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.getSentence());
			return true;
		}

		if (islandNumber <= 0 || islandNumber > SkyBlockMultiplayer.getInstance().getAmountOfIslands()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().loadPlayerInfo(SkyBlockMultiplayer.getInstance().getOwner(islandNumber));
		if (pi != null) {
			pi.setDead(false);
			pi.setIslandInfo(null);
			pi.setIslandArmor(null);
			pi.setIslandInventory(null);
			pi.setIslandExp(0);
			pi.setIslandLevel(0);
			pi.setIslandHealth(player.getMaxHealth());
			pi.setIslandFood(20);
			pi.setIslandLocation(null);
			pi.setHomeLocation(null);
			SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		}

		SkyBlockMultiplayer.getInstance().removeIsland(CreateIsland.getIslandPosition(islandNumber));
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Island removed!");
		return true;
	}

	/**
	 * Teleport player to home location.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeTeleport(Player player) {
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			this.playerStart(player);
			return true;
		}

		// player has a island
		if (pi.getHomeLocation() == null) {
			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
			if (!player.teleport(pi.getIslandLocation()))
				return true;

			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
		} else {
			Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi.getIslandInfo());
			if (homeSweetHome == null) { // if null, island is missing and / or home location returns no safe block
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Cannot teleport to your home location, your island is probably missing.");
				return true;
			}

			SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
			if (!player.teleport(homeSweetHome))
				return true;

			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
		}

		SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.getSentence() + player.getName());

		return true;
	}

	/**
	 * Set a home location.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeSet(Player player) {
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			return true;
		}

		int islandNumber = CreateIsland.getIslandNumber(player.getLocation());
		IslandInfo ii = SkyBlockMultiplayer.getInstance().getSettings().getIslandInfo(islandNumber);
		if (ii == null || !ii.isIslandOwner(player.getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_HOME_CHANGE_ONYL_IN_OWN_AREA.getSentence());
			return true;
		} else {
			pi.setHomeLocation(player.getLocation());
			SkyBlockMultiplayer.getInstance().saveIslandInfo(pi.getIslandInfo());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_LOCATION_CHANGED.getSentence());
			return true;
		}
	}

	/**
	 * Show a list of all friends.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeList(Player player) {
		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null || pi.getIslandInfo() == null) {
			return true;
		}

		String list = "";
		boolean first = true;
		for (String friend : pi.getIslandInfo().getFriends()) {
			if (first) {
				first = false;
			} else {
				list += ", ";
			}
			list += friend;
		}
		player.sendMessage(list);
		return true;
	}

	private boolean homeJoin(Player player, String toPlayer) {
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.getSentence());
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.getSentence());
			return true;
		}

		if (pi.getIslandLocation() == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.getSentence());
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(pi, toPlayer);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.getSentence());
			return true;
		}

		if (player.getName().equalsIgnoreCase(res)) {
			return true;
		}

		IslandInfo pTarget = pi.getIslandInfoFromFriend(res);
		if (pTarget == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NOT_FRIEND_FROM_YOU.getSentence());
			return true;
		}

		// player has a island

		Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pTarget);
		if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
			player.sendMessage("Cannot teleport to the friend home location, his island is probably missing.");
			return true;
		}

		if (!player.teleport(homeSweetHome))
			return true;

		SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
		SkyBlockMultiplayer.getInstance().changeToIslandInventory(pi);

		SkyBlockMultiplayer.getInstance().savePlayerInfo(pi);
		return true;
	}

	/**
	 * Add a player to friend list.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeAdd(Player player, String playerToAdd) {
		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			player.sendMessage("You have no island");
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(pi, playerToAdd);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.getSentence());
			return true;
		}

		if (res.equalsIgnoreCase(player.getName())) {
			return true;
		}

		Player newFriend = SkyBlockMultiplayer.getInstance().getServer().getPlayer(res);
		if (newFriend != null) {
			newFriend.sendMessage(SkyBlockMultiplayer.getInstance().pName + player.getName() + Language.MSGS_SOMEONE_ADDED_YOU.getSentence());
		}

		PlayerInfo piFriend = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(newFriend.getName());
		if (piFriend == null) {
			piFriend = new PlayerInfo(res);
		}

		pi.addFriend(res);
		piFriend.addBuildPermission(pi.getIslandInfo().getIslandNumber(), pi.getIslandInfo());
		SkyBlockMultiplayer.getInstance().savePlayerInfo(piFriend);

		SkyBlockMultiplayer.getInstance().saveIslandInfo(pi.getIslandInfo());
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_FRIEND_ADDED.getSentence());
		return true;
	}

	/**
	 * Remove a player from friend list.
	 * 
	 * @param player
	 * @param playerToRemove
	 * @return
	 */
	private boolean homeRemove(Player player, String playerToRemove) {
		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null) {
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(pi, playerToRemove);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.getSentence());
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.getSentence());
			return true;
		}

		if (res.equalsIgnoreCase(player.getName())) {
			return true;
		}

		pi.removeFriend(res);

		SkyBlockMultiplayer.getInstance().saveIslandInfo(pi.getIslandInfo());
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_FRIEND_REMOVED.getSentence());
		return true;
	}

	private boolean restartIsland(Player player, String arg) {  // TODO: Test needed.
		PlayerInfo pi = SkyBlockMultiplayer.getInstance().getSettings().getPlayerInfo(player.getName());
		if (pi == null || !pi.getHasIsland()) {
			return true;
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getGameMode() != GameMode.BUILD) {
			player.sendMessage("You can use this command only in build mode.");
			return true;
		}

		if (arg.equalsIgnoreCase("replace")) {
			if (this.listRestartIslands.containsKey(player.getName())) {
				long stopTime = System.currentTimeMillis();
				long startTime = this.listRestartIslands.get(player.getName());

				if ((stopTime - startTime) >= 15000) {
					this.listRestartIslands.put(player.getName(), System.currentTimeMillis());
					player.sendMessage("If you really want to restart your own island. Then type '/sky newisland replace' within 15 seconds.");
					return true;
				}

				SkyBlockMultiplayer.getInstance().removeIsland(pi.getIslandInfo().getIslandLocation());
				return this.playerStart(player);
			}
		}

		this.listRestartIslands.put(player.getName(), System.currentTimeMillis());
		player.sendMessage("If you really want to restart your own island. Then type '/sky newisland replace' within 15 seconds.");
		return true;
	}
}
