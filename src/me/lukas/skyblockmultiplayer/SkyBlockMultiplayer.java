package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import me.lukas.skyblockmultiplayer.listeners.EntityDeath;
import me.lukas.skyblockmultiplayer.listeners.PlayerBreackBlockListener;
import me.lukas.skyblockmultiplayer.listeners.PlayerInteract;
import me.lukas.skyblockmultiplayer.listeners.PlayerPlaceBlockListener;
import me.lukas.skyblockmultiplayer.listeners.PlayerRespawn;
import me.lukas.skyblockmultiplayer.listeners.PlayerTeleport;
import me.lukas.skyblockmultiplayer.listeners.PlayerUseBucketListener;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockMultiplayer extends JavaPlugin {

	public static World skyBlockWorld = null;
	private static SkyBlockMultiplayer instance;

	public static Settings settings;

	public FileConfiguration configPlugin;
	public File filePlugin;

	public FileConfiguration configLanguage;
	public File fileLanguage;

	private File directoryPlayers;
	private File directoryIslands;

	public String pName;

	@Override
	public void onDisable() {
		this.getLogger().info("v" + getDescription().getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		SkyBlockMultiplayer.instance = this;

		this.pName = ChatColor.WHITE + "[" + ChatColor.GREEN + this.getDescription().getName() + ChatColor.WHITE + "] ";

		// register events
		this.registerEvents();

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}

		this.configPlugin = this.getConfig();
		this.filePlugin = new File(this.getDataFolder(), "config.yml");
		this.loadPluginConfig();

		this.configLanguage = new YamlConfiguration();
		this.fileLanguage = new File(this.getDataFolder() + File.separator + "language", settings.getLanguage() + ".yml");
		try {
			this.loadLanguageConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.directoryIslands = new File(this.getDataFolder() + File.separator + "islands");
		if (!this.directoryIslands.exists()) {
			this.directoryIslands.mkdir();
		} else {
			this.loadIslandFiles();
		}

		this.directoryPlayers = new File(this.getDataFolder() + File.separator + "players");
		if (!this.directoryPlayers.exists()) {
			this.directoryPlayers.mkdir();
		} else {
			this.loadPlayerFiles();
		}

		// register command
		this.getCommand("skyblock").setExecutor(new SkyBlockCommand());

		this.getLogger().info("v" + getDescription().getVersion() + " enabled.");
	}

	public static SkyBlockMultiplayer getInstance() {
		return instance;
	}

	/**
	 * Register the events
	 * 
	 */
	public void registerEvents() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new PlayerPlaceBlockListener(), this);
		manager.registerEvents(new PlayerBreackBlockListener(), this);
		manager.registerEvents(new PlayerUseBucketListener(), this);
		manager.registerEvents(new EntityDeath(), this);
		manager.registerEvents(new PlayerRespawn(), this);
		manager.registerEvents(new PlayerInteract(), this);
		manager.registerEvents(new PlayerTeleport(), this);
	}

	/**
	 * Creates or loads the config file.
	 * 
	 */
	public void loadPluginConfig() {
		settings = new Settings();
		ItemStack[] itemsChest = { new ItemStack(Material.ICE, 2), new ItemStack(Material.SAPLING, 5), new ItemStack(Material.MELON, 3), new ItemStack(Material.CACTUS, 1), new ItemStack(Material.LAVA_BUCKET, 1), new ItemStack(Material.PUMPKIN, 1) };

		if (!this.filePlugin.exists()) {
			settings.setIslandDistance(50);
			settings.setItemsChest(itemsChest);
			settings.setIsOnline(true);
			settings.setAllowContent(false);
			settings.setLanguage("english");
			settings.setGameMode(GameMode.BUILD);
			settings.setIslandsPerPlayer(1);
			settings.setLivesPerIsland(1);
			settings.setRespawnWithInventory(true);
			settings.setWithProtectedArea(false);
			settings.setAllowEnderPearl(false);
			settings.setWorldName(this.getDescription().getName());
			settings.setRemoveCreaturesByTeleport(true);
			settings.setIslandSchematic("");
			settings.setIslandYPosition(64);
			settings.setTowerSchematic("");
			settings.setTowerYPosition(80);

			for (EnumPluginConfig c : EnumPluginConfig.values()) {
				this.setStringbyPath(this.configPlugin, this.filePlugin, c.getPath(), c.getValue());
			}
		} else {
			try {
				this.configPlugin.load(this.filePlugin);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				settings.setIslandDistance(Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_ISLANDDISTANCE.getPath(), 50, true)));
			} catch (Exception e) {
				settings.setIslandDistance(50);
			}

			String[] dataItems = this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_CHESTITEMS.getPath(), "79:2 6:5 360:3 81:1 327:1 86:1", true).split(" ");
			ArrayList<ItemStack> alitemsChest = new ArrayList<ItemStack>();

			for (String s : dataItems) {
				if (s.trim() != "") {
					String[] dataValues = s.split(":");
					try {
						int id = Integer.parseInt(dataValues[0]);
						int amount = Integer.parseInt(dataValues[1]);

						Material m = Material.matchMaterial("" + dataValues[0]);
						if (m != null) {
							if (dataValues.length == 2) {
								alitemsChest.add(new ItemStack(id, amount));
							} else if (dataValues.length == 3) {
								alitemsChest.add(new ItemStack(id, amount, (short) 0, Byte.parseByte(dataValues[2])));
							}
						}

					} catch (Exception ex) {
					}
				}
			}

			itemsChest = new ItemStack[alitemsChest.size()];
			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}

			try {
				settings.setLivesPerIsland(Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_PVP_LIVESPERISLAND.getPath(), 1, true)));
			} catch (Exception e) {
				settings.setLivesPerIsland(1);
			}

			try {
				settings.setIslandsPerPlayer(Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_PVP_ISLANDSPERPLAYER.getPath(), 1, true)));
			} catch (Exception e) {
				settings.setIslandsPerPlayer(1);
			}

			try {
				settings.setTowerYPosition(Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_SCHEMATIC_TOWER_YHEIGHT.getPath(), 80, true)));
			} catch (Exception e) {
				settings.setTowerYPosition(80);
			}

			/*try {
				Settings.islandYHeight = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_ISLAND_YHEIGHT.path, 64, true));
				if (Settings.islandYHeight < 0) {
					Settings.islandYHeight = 64;
				}
			} catch (Exception e) {
				Settings.islandYHeight = 64;
			}*/

			settings.setIslandYPosition(64);
			settings.setItemsChest(itemsChest);
			settings.setIsOnline(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_SKYBLOCKONLINE.getPath(), true, true)));
			settings.setLanguage(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_LANGUAGE.getPath(), "english", true));
			settings.setAllowContent(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_ALLOWCONTENT.getPath(), false, true)));
			settings.setGameMode(GameMode.valueOf(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_GAMEMODE.getPath(), "build", true).toUpperCase()));
			settings.setRespawnWithInventory(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_BUILD_RESPAWNWITHINVENTORY.getPath(), true, true)));
			settings.setWithProtectedArea(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_BUILD_WITHPROTECTEDAREA.getPath(), true, true)));
			settings.setAllowEnderPearl(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_BUILD_ALLOWENDERPEARL.getPath(), false, true)));
			settings.setWithProtectedBorder(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_BUILD_WITHPROTECTEDBORDER.getPath(), true, true)));
			settings.setWorldName(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_WORLDNAME.getPath(), this.getDescription().getName(), true));
			settings.setIsLocked(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_CLOSED.getPath(), false, true)));
			settings.setRemoveCreaturesByTeleport(Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_REMOVECREATURESBYTELEPORT.getPath(), true, true)));
			settings.setIslandSchematic(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_SCHEMATIC_ISLAND_FILENAME.getPath(), "", true));
			settings.setTowerSchematic(this.getStringbyPath(this.configPlugin, this.filePlugin, EnumPluginConfig.OPTIONS_SCHEMATIC_TOWER_FILENAME.getPath(), "", true));
		}
	}

	public void loadIslandFiles() {
		for (String f : new File(this.directoryIslands.getAbsolutePath()).list()) {
			if (new File(this.directoryIslands, f).isFile()) {
				IslandInfo ii = this.loadIslandInfo(f);
				if (ii != null) {
					settings.addIslandInfo(ii);
					System.out.println("aded island from " + ii.getIslandOwner());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public IslandInfo loadIslandInfo(String island) {
		YamlConfiguration yamlIslandInfo = new YamlConfiguration();
		File fileIslandInfo = new File(this.directoryIslands, island);
		if (!fileIslandInfo.exists()) {
			return null;
		}
		try {
			yamlIslandInfo.load(fileIslandInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		int islandNumber = Integer.parseInt(fileIslandInfo.getName().replace(".yml", ""));
		IslandInfo ii = new IslandInfo(islandNumber);

		String islandOwner = "";
		if (yamlIslandInfo.contains(EnumIslandConfig.ISLAND_OWNER.getPath())) {
			islandOwner = yamlIslandInfo.get(EnumIslandConfig.ISLAND_OWNER.getPath()).toString();
		} else {
			yamlIslandInfo.set(EnumIslandConfig.ISLAND_OWNER.getPath(), false);
		}
		ii.setIslandOwner(islandOwner);

		Location islandLocation = null;
		if (yamlIslandInfo.contains(EnumIslandConfig.ISLAND_LOCATION.getPath())) {
			islandLocation = LocationParser.parseStringToLocation(yamlIslandInfo.get(EnumIslandConfig.ISLAND_LOCATION.getPath()).toString());
		} else {
			yamlIslandInfo.set(EnumIslandConfig.ISLAND_LOCATION.getPath(), LocationParser.getStringFromLocation(ii.getIslandLocation()));
		}
		ii.setIslandLocation(islandLocation);

		Location homeLocation = null;
		if (yamlIslandInfo.contains(EnumIslandConfig.HOME_LOCATION.getPath())) {
			islandLocation = LocationParser.parseStringToLocation(yamlIslandInfo.get(EnumIslandConfig.HOME_LOCATION.getPath()).toString());
		} else {
			yamlIslandInfo.set(EnumIslandConfig.HOME_LOCATION.getPath(), LocationParser.getStringFromLocation(ii.getHomeLocation()));
		}
		ii.setHomeLocation(homeLocation);

		ArrayList<String> friends = new ArrayList<String>();
		if (yamlIslandInfo.contains(EnumIslandConfig.FRIENDS.getPath())) {
			friends = (ArrayList<String>) yamlIslandInfo.get(EnumIslandConfig.FRIENDS.getPath());
		} else {
			yamlIslandInfo.set(EnumIslandConfig.FRIENDS.getPath(), ii.getFriends());
		}

		ii.setFriends(friends);

		try {
			yamlIslandInfo.save(fileIslandInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return ii;
	}

	public void saveIslandInfo(IslandInfo ii) {
		YamlConfiguration yamlIslandInfo = new YamlConfiguration();
		File filePlayerInfo = new File(this.directoryIslands, ii.getIslandNumber() + ".yml");

		yamlIslandInfo.set(EnumIslandConfig.ISLAND_OWNER.getPath(), ii.getIslandOwner());
		yamlIslandInfo.set(EnumIslandConfig.ISLAND_LOCATION.getPath(), LocationParser.getStringFromLocation(ii.getIslandLocation()));
		yamlIslandInfo.set(EnumIslandConfig.HOME_LOCATION.getPath(), LocationParser.getStringFromLocation(ii.getHomeLocation()));

		yamlIslandInfo.set(EnumIslandConfig.FRIENDS.getPath(), ii.getFriends());
		try {
			yamlIslandInfo.save(filePlayerInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load all informations from player who exists in folder players and who have a island.
	 * 
	 */
	public void loadPlayerFiles() {
		for (String f : new File(this.directoryPlayers.getAbsolutePath()).list()) {
			if (new File(this.directoryPlayers, f).isFile()) {

				PlayerInfo pi = this.loadPlayerInfo(f.replace(".yml", ""));
				if (pi != null) {
					// add player, if missing
					String playerName = pi.getPlayerName();

					Player playerOnline = this.getServer().getPlayer(playerName);
					if (playerOnline == null || !playerOnline.isOnline() || pi.getIslandLocation() == null || !playerOnline.getWorld().getName().equalsIgnoreCase(settings.getWorldName())) {
						continue;
					}

					settings.addPlayer(playerName, pi);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public PlayerInfo loadPlayerInfo(String playerName) {
		YamlConfiguration yamlPlayerInfo = new YamlConfiguration();
		File filePlayerInfo = new File(this.directoryPlayers, playerName + ".yml");
		if (!filePlayerInfo.exists()) {
			return null;
		}

		try {
			yamlPlayerInfo.load(filePlayerInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		PlayerInfo pi = new PlayerInfo(playerName);

		// Find own Island
		int ownsIslandNr = -1;
		if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_NUMBER.getPath())) {
			ownsIslandNr = yamlPlayerInfo.getInt(EnumPlayerConfig.ISLAND_NUMBER.getPath());
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_NUMBER.getPath(), ownsIslandNr);
		}
		pi.setIslandInfo(settings.getIslandInfo(ownsIslandNr));

		// Check consistency
		if (pi.getHasIsland() && !pi.getIslandInfo().isIslandOwner(playerName)) {
			pi.setIslandInfo(null);
		}

		// Find Buildpermissions
		// buildPermissions = new HashMap<Integer, IslandInfo>();

		ArrayList<Integer> builtlist = new ArrayList<Integer>();
		if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_BUILTLIST.getPath())) {
			builtlist = (ArrayList<Integer>) yamlPlayerInfo.getList(EnumPlayerConfig.ISLAND_BUILTLIST.getPath());
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_BUILTLIST.getPath(), builtlist);
		}
		// Go through buildlist with Islandnr to get IslandInfos
		for (int islandnr : builtlist) {
			IslandInfo friend = settings.getIslandInfo(islandnr);
			if (friend != null && friend.containsFriend(playerName)) {
				pi.addBuildPermission(islandnr, friend);
			}
		}

		boolean isOnIsland = false;
		if (yamlPlayerInfo.contains(EnumPlayerConfig.IS_ON_ISLAND.getPath())) {
			isOnIsland = Boolean.parseBoolean(yamlPlayerInfo.get(EnumPlayerConfig.IS_ON_ISLAND.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.IS_ON_ISLAND.getPath(), false);
		}
		pi.setIsOnIsland(isOnIsland);

		boolean isDead = false;
		if (yamlPlayerInfo.contains(EnumPlayerConfig.IS_DEAD.getPath())) {
			isDead = Boolean.parseBoolean(yamlPlayerInfo.get(EnumPlayerConfig.IS_DEAD.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.IS_DEAD.getPath(), false);
		}
		pi.setDead(isDead);

		int islandFood = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_FOOD.getPath())) {
				islandFood = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_FOOD.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		pi.setIslandFood(islandFood);

		int islandHealth = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_HEALTH.getPath())) {
				islandHealth = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_HEALTH.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		pi.setIslandHealth(islandHealth);

		int islandExp = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_EXP.getPath())) {
				islandExp = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_EXP.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		pi.setIslandExp(islandExp);

		int islandLevel = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_LEVEL.getPath())) {
				islandLevel = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_LEVEL.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		pi.setIslandLevel(islandLevel);

		ItemStack[] islandInventory = new ItemStack[36];
		if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_INVENTORY.getPath())) {
			ArrayList<String> listIslandInventory = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_INVENTORY.getPath());
			islandInventory = ItemParser.getItemStackArrayFromList(listIslandInventory, 36);
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_INVENTORY.getPath(), new ArrayList<String>());
		}
		pi.setIslandInventory(islandInventory);

		ItemStack[] islandArmor = new ItemStack[4];
		if (yamlPlayerInfo.contains(EnumPlayerConfig.ISLAND_ARMOR.getPath())) {
			ArrayList<String> listIslandArmor = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerConfig.ISLAND_ARMOR.getPath());
			islandArmor = ItemParser.getItemStackArrayFromList(listIslandArmor, 4);
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_ARMOR.getPath(), new ArrayList<String>());
		}
		pi.setIslandArmor(islandArmor);

		Location oldLocation = null;
		if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_LOCATION.getPath())) {
			oldLocation = LocationParser.parseStringToLocation(yamlPlayerInfo.get(EnumPlayerConfig.OLD_LOCATION.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.OLD_LOCATION.getPath(), "");
		}
		pi.setOldLocation(oldLocation);

		int oldFood = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_FOOD.getPath())) {
				oldFood = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.OLD_FOOD.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.OLD_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		pi.setOldFood(oldFood);

		int oldHealth = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_HEALTH.getPath())) {
				oldHealth = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.OLD_HEALTH.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.OLD_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		pi.setOldHealth(oldHealth);

		int oldExp = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_EXP.getPath())) {
				oldExp = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.OLD_EXP.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.OLD_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		pi.setOldExp(oldExp);

		int oldLevel = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_LEVEL.getPath())) {
				oldLevel = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerConfig.OLD_LEVEL.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerConfig.OLD_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		pi.setOldLevel(oldLevel);

		ItemStack[] oldInventory = new ItemStack[36];
		if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_INVENTORY.getPath())) {
			ArrayList<String> listOldInventory = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerConfig.OLD_INVENTORY.getPath());
			oldInventory = ItemParser.getItemStackArrayFromList(listOldInventory, 36);
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.OLD_INVENTORY.getPath(), new ArrayList<String>());
		}
		pi.setOldInventory(oldInventory);

		ItemStack[] oldArmor = new ItemStack[4];
		if (yamlPlayerInfo.contains(EnumPlayerConfig.OLD_ARMOR.getPath())) {
			ArrayList<String> listOldArmor = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerConfig.OLD_ARMOR.getPath());
			oldArmor = ItemParser.getItemStackArrayFromList(listOldArmor, 4);
		} else {
			yamlPlayerInfo.set(EnumPlayerConfig.OLD_ARMOR.getPath(), new ArrayList<String>());
		}
		pi.setOldArmor(oldArmor);

		/*yamlPlayerData.load(filePlayerData);
		for (String friend : yamlPlayerData.getConfigurationSection("friends").getKeys(false)) {
			
		}*/

		try {
			yamlPlayerInfo.save(filePlayerInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return pi;
	}

	public void savePlayerInfo(PlayerInfo pi) {
		YamlConfiguration yamlPlayerInfo = new YamlConfiguration();
		File filePlayerInfo = new File(this.directoryPlayers, pi.getPlayerName() + ".yml");

		// island info
		if (pi.getIslandInfo() != null) {
			yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_NUMBER.getPath(), pi.getIslandInfo().getIslandNumber());
		}

		yamlPlayerInfo.set(EnumPlayerConfig.IS_ON_ISLAND.getPath(), pi.getIsOnIsland());
		yamlPlayerInfo.set(EnumPlayerConfig.IS_DEAD.getPath(), pi.isDead());
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_FOOD.getPath(), "" + pi.getIslandFood());
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_HEALTH.getPath(), "" + pi.getIslandHealth());
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_EXP.getPath(), "" + pi.getIslandExp());
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_LEVEL.getPath(), "" + pi.getIslandLevel());
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_INVENTORY.getPath(), ItemParser.getListFromItemStackArray(pi.getIslandInventory()));
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_ARMOR.getPath(), ItemParser.getListFromItemStackArray(pi.getIslandArmor()));
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_LOCATION.getPath(), LocationParser.getStringFromLocation(pi.getOldLocation()));
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_FOOD.getPath(), "" + pi.getOldFood());
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_HEALTH.getPath(), "" + pi.getOldHealth());
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_EXP.getPath(), "" + pi.getOldExp());
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_LEVEL.getPath(), "" + pi.getOldLevel());
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_INVENTORY.getPath(), ItemParser.getListFromItemStackArray(pi.getOldInventory()));
		yamlPlayerInfo.set(EnumPlayerConfig.OLD_ARMOR.getPath(), ItemParser.getListFromItemStackArray(pi.getOldArmor()));
		yamlPlayerInfo.set(EnumPlayerConfig.ISLAND_BUILTLIST.getPath(), pi.getBuildListNumbers());

		try {
			yamlPlayerInfo.save(filePlayerInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the language file, that is setted in conig.yml
	 * 
	 */
	public void loadLanguageConfig() throws Exception {
		if (!new File(this.getDataFolder() + File.separator + "language").exists()) {
			new File(this.getDataFolder() + File.separator + "language").mkdirs();
		}

		String encoding = "UTF-8";
		if (!this.fileLanguage.exists()) {
			this.fileLanguage.createNewFile(); //create file
			this.writeLanguageConfig(); //write standard language
		} else {
			Scanner scanner = new Scanner(new FileInputStream(this.fileLanguage), encoding);
			String contentToRead = "";
			while (scanner.hasNextLine()) {
				contentToRead += scanner.nextLine() + System.getProperty("line.separator");
			}
			scanner.close();

			try {
				this.configLanguage.loadFromString(contentToRead);
			} catch (InvalidConfigurationException e) {
				encoding = "Cp1252";
				scanner = new Scanner(new FileInputStream(this.fileLanguage), encoding);
				contentToRead = "";
				while (scanner.hasNextLine()) {
					contentToRead += scanner.nextLine() + System.getProperty("line.separator");
				}
				scanner.close();
				this.configLanguage.loadFromString(contentToRead);
			}

			boolean missing = false;
			for (Language g : Language.values()) {
				String path = g.getPath();
				if (!this.configLanguage.contains(path)) {
					this.configLanguage.set(path, g.getSentence());
					missing = true;
				} else {
					g.setSentence(this.replaceColor(this.configLanguage.getString(path)));
				}
			}

			if (missing) {
				String contentToSave = this.configLanguage.saveToString();
				Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), encoding);
				out.write(contentToSave);
				out.flush();
				out.close();
			}
		}
		SkyBlockMultiplayer.getSkyBlockWorld();
	}

	/**
	 * This replace �0-�f with ChatColor.
	 * 
	 * @param s the given String
	 * @return string with ChatColor.
	 */
	private String replaceColor(String s) {
		for (ChatColor c : ChatColor.values()) {
			s = s.replaceAll("�" + c.getChar(), "" + ChatColor.getByChar(c.getChar()));
		}
		return s;
	}

	/**
	 * Parse a String to a location.
	 * 
	 * @param s 
	 * @return location or null
	 */
	public Location getLocationString(String s) {
		if (s == null || s.trim() == "") {
			return null;
		}
		String[] parts = s.split(":");
		if (parts.length == 4) {
			World w = this.getServer().getWorld(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = Integer.parseInt(parts[3]);
			return new Location(w, x, y, z);
		}
		return null;
	}

	/**
	 * Returns a string of the given location, can be empty.
	 * 
	 * @param l get a string of it.
	 * @return string.
	 */
	public String getStringLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
	}

	/**
	 * This writes the the language yml file.
	 * @throws IOException 
	 * 
	 */
	private void writeLanguageConfig() throws IOException {
		for (Language g : Language.values()) {
			String path = g.getPath();
			this.configLanguage.set(path, g.getSentence());
		}

		String contentToSave = this.configLanguage.saveToString();
		Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), "UTF-8");
		out.write(contentToSave);
		out.flush();
		out.close();
	}

	/**
	 * Creates the path and set the value.
	 * 
	 * @param fc a instance of FileConfiguration.
	 * @param f a instance of File.
	 * @param path the path to be created.
	 * @param value the given value to be included.
	 */
	public void setStringbyPath(FileConfiguration fc, File f, String path, Object value) {
		fc.set(path, value.toString());
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Get the value from the config by path, if path not exists, it will be created with the given standard value.
	 * 
	 * @param fc a instance of FileConfiguration.
	 * @param file a instance of File.
	 * @param path the path to the value in the file.
	 * @param stdValue the standard content, will be return if the path not exists.
	 * @return object.
	 */
	public String getStringbyPath(FileConfiguration fc, File file, String path, Object stdValue, boolean addMissing) {
		if (!fc.contains(path)) {
			if (addMissing) {
				this.setStringbyPath(fc, file, path, stdValue);
			}
			return stdValue.toString();
		}
		return fc.getString(path);
	}

	/**
	 * Creates the world and the tower of SkyBlock.
	 * 
	 * @return world instance of SkyBlock
	 */
	public static World getSkyBlockWorld() {
		if (skyBlockWorld == null) {
			boolean folderExists = new File(settings.getWorldName()).exists();
			skyBlockWorld = WorldCreator.name(settings.getWorldName()).type(WorldType.FLAT).environment(Environment.NORMAL).generator(new SkyBlockChunkGenerator()).createWorld();
			if (!folderExists) {
				File f = new File(settings.getIslandSchematic());
				if (f.exists() && f.isFile()) {
					try {
						CreateIsland.createStructure(new Location(getSkyBlockWorld(), 0, settings.getTowerYPosition(), 0), f);
						skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0);
						return skyBlockWorld;
					} catch (Exception e) {
						e.printStackTrace();
						SkyBlockMultiplayer.createSpawnTower();
						skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
						return skyBlockWorld;
					}
				}

				f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), settings.getTowerSchematic());
				if (f.exists() && f.isFile()) {
					try {
						CreateIsland.createStructure(new Location(getSkyBlockWorld(), 0, settings.getTowerYPosition(), 0), f);
						skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0);
						// System.out.println("Spawn set: " + skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0) + " location = " + SkyBlockMultiplayer.getInstance().getStringLocation(skyBlockWorld.getSpawnLocation()));
						return skyBlockWorld;
					} catch (Exception e) {
						e.printStackTrace();
						SkyBlockMultiplayer.createSpawnTower();
						skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
						return skyBlockWorld;
					}
				}

				SkyBlockMultiplayer.createSpawnTower();
				skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
				return skyBlockWorld;
			}
		}
		return skyBlockWorld;
	}

	/**
	 * Clear armor content.
	 * 
	 * @param player
	 */
	public void clearArmorContents(Player player) {
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
	}

	private ArrayList<File> sfiles;

	/**
	 * Get all files, directories inside of the given path.
	 * 
	 * @param path the directory. 
	 */
	public void getAllFiles(String path) {

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
				this.getLogger().warning(ex.getMessage());
			}
		}
	}

	/**
	 * Check if player is on tower.
	 * 
	 * @param player
	 * @return boolean true if player is on tower, false if not
	 */
	public boolean playerIsOnTower(Player player) {
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();

		if (px >= -20 && px <= 20 && pz >= -20 && pz <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * Check if a location is on tower.
	 * 
	 * @param player
	 * @return boolean true if player is on tower, false if not
	 */
	public boolean locationIsOnTower(Location l) {
		int px = l.getBlockX();
		int pz = l.getBlockZ();

		if (px >= -20 && px <= 20 && pz >= -20 && pz <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * Comparing part of a player name with all players
	 * 
	 * @param partName
	 * @return
	 */
	public String getFullPlayerName(PlayerInfo pi, String partName) {
		int amount = 0;
		String pName = "";
		for (Player p : this.getServer().getOnlinePlayers()) {
			if (p.getName().toLowerCase().startsWith(partName.toLowerCase())) {
				amount++;
				pName = p.getName();
			}
		}
		if (amount == 1)
			return pName;
		else if (amount > 1)
			return "0";

		for (IslandInfo ii : pi.getBuiltPermissionList().values()) {
			if (ii.getIslandOwner().toLowerCase().startsWith(partName.toLowerCase())) {
				if (ii.containsFriend(pi.getPlayerName())) {
					amount++;
					pName = ii.getIslandOwner();
				}
			}
		}

		if (amount == 1)
			return pName;
		else if (amount > 1)
			return "0";
		return "-1";
	}

	public Location getYLocation(Location l) {
		for (int y = 0; y < 254; y++) {
			int px = l.getBlockX();
			int py = y;
			int pz = l.getBlockZ();
			Block b1 = new Location(l.getWorld(), px, py, pz).getBlock();
			Block b2 = new Location(l.getWorld(), px, py + 1, pz).getBlock();
			Block b3 = new Location(l.getWorld(), px, py + 2, pz).getBlock();
			if (!b1.getType().equals(Material.AIR) && b2.getType().equals(Material.AIR) && b3.getType().equals(Material.AIR)) {
				return b2.getLocation();
			}
		}
		return l;
	}

	public Location getSafeHomeLocation(IslandInfo ii) {
		// a) check original location
		Location home = ii.getHomeLocation();

		if (this.isSafeLocation(home)) {
			return home;
		}

		// b) check if a suitable y exists on this x and z
		for (int y = home.getBlockY(); y > 0; y--) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = home.getBlockY(); y < 255; y++) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}

		// c) check island Location
		Location island = ii.getIslandLocation();
		if (this.isSafeLocation(island)) {
			return island;
		}

		for (int y = island.getBlockY(); y > 0; y--) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = island.getBlockY(); y < 255; y++) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		return null;
	}

	public boolean isSafeLocation(Location l) {
		if (l == null) {
			return false;
		}

		Block ground = l.getBlock().getRelative(BlockFace.DOWN);
		Block air1 = l.getBlock();
		Block air2 = l.getBlock().getRelative(BlockFace.UP);

		if (ground.getType().equals(Material.AIR))
			return false;
		if (ground.getType().equals(Material.LAVA))
			return false;
		if (ground.getType().equals(Material.STATIONARY_LAVA))
			return false;
		if (ground.getType().equals(Material.WATER))
			return false;
		if (ground.getType().equals(Material.STATIONARY_WATER))
			return false;
		if (air1.getType().equals(Material.AIR) && air2.getType().equals(Material.AIR))
			return true;
		return false;
	}

	/**
	 * Remove creatures from chunk at and around the given location.
	 * 
	 * @param l
	 */
	public void removeCreatures(Location l) {
		if (!settings.getRemoveCreaturesByTeleport() || l == null) {
			return;
		}

		int px = l.getBlockX();
		int py = l.getBlockY();
		int pz = l.getBlockZ();
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Chunk c = l.getWorld().getChunkAt(new Location(l.getWorld(), px + x * 16, py, pz + z * 16));
				for (Entity e : c.getEntities()) {
					if (e.getType() == EntityType.SPIDER || e.getType() == EntityType.CREEPER || e.getType() == EntityType.ENDERMAN || e.getType() == EntityType.SKELETON || e.getType() == EntityType.ZOMBIE) {
						e.remove();
					}
				}
			}
		}
	}

	/**
	 * Remove a island from SkyBlock.
	 * 
	 * @param l given location
	 */
	public void removeIsland(Location l) {
		if (l != null) {
			int px = l.getBlockX();
			int py = l.getBlockY();
			int pz = l.getBlockZ();
			for (int x = -15; x <= 15; x++) {
				for (int y = -15; y <= 15; y++) {
					for (int z = -15; z <= 15; z++) {
						Block b = new Location(l.getWorld(), px + x, py + y, pz + z).getBlock();
						if (!b.getType().equals(Material.AIR)) {
							if (b.getType().equals(Material.CHEST)) {
								Chest c = (Chest) b.getState();
								ItemStack[] items = new ItemStack[c.getInventory().getContents().length];
								c.getInventory().setContents(items);
							} else if (b.getType().equals(Material.FURNACE)) {
								Furnace f = (Furnace) b.getState();
								ItemStack[] items = new ItemStack[f.getInventory().getContents().length];
								f.getInventory().setContents(items);
							} else if (b.getType().equals(Material.DISPENSER)) {
								Dispenser d = (Dispenser) b.getState();
								ItemStack[] items = new ItemStack[d.getInventory().getContents().length];
								d.getInventory().setContents(items);
							}
							b.setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new SkyBlockChunkGenerator();
	}

	private static void makeBlock(int x, int y, int z, Material m) {
		if (!SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x, y, z).getType().equals(m)) {
			SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x, y, z).setType(m);
		}
	}

	private static void quader(int x, int y, int z, Material m) {
		if (y < 0)
			return;
		SkyBlockMultiplayer.makeBlock(x, y, z, m);
		SkyBlockMultiplayer.makeBlock(x, y, z + 1, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z + 1, m);
	}

	public static void createSpawnTower() {
		int yStart = 2;
		int yEnde = 90;
		int[][] lavatreppe = { { 2, 0 }, { 2, 0 }, { 0, 2 }, { 0, 2 }, { -2, 0 }, { -2, 0 }, { 0, -2 }, { 0, -2 } };
		int i = 0;
		int x = -2;
		int z = -2;
		for (int y = yStart; y < yEnde - 2; y++) {
			// create obsidan tower
			SkyBlockMultiplayer.quader(0, y, 0, Material.OBSIDIAN);
			// create wall inside
			for (int xw = -2; xw < 4; xw++) {
				SkyBlockMultiplayer.makeBlock(xw, y, -2, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(xw, y, 3, Material.GLASS);
			}
			for (int zw = -2; zw < 4; zw++) {
				SkyBlockMultiplayer.makeBlock(-2, y, zw, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(3, y, zw, Material.GLASS);
			}
			// create lava steps
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(43));
			x += lavatreppe[i][0];
			z += lavatreppe[i][1];
			i++;
			if (i == lavatreppe.length)
				i = 0;

		}
		// water steps
		i = 0;
		x = -2;
		z = -2;
		for (int y = yStart; y <= yEnde; y++) {
			SkyBlockMultiplayer.quader(x, y - 3, z, Material.GLASS);
			x += lavatreppe[i][0];
			z += lavatreppe[i][1];
			i++;
			if (i == lavatreppe.length)
				i = 0;
		}

		// place the full stepes
		i = 0;
		x = -2;
		z = -4;
		int[][] stairsWhole = { { 4, 0 }, { 2, 2 }, { 0, 4 }, { -2, 2 }, { -4, 0 }, { -2, -2 }, { 0, -4 }, { 2, -2 } };
		for (int y = yStart + 1; y < yEnde - 1; y++) {
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(43));
			x += stairsWhole[i][0];
			z += stairsWhole[i][1];
			i++;
			if (i == stairsWhole.length)
				i = 0;
		}
		// place the half steps
		i = 0;
		x = -4;
		z = -4;
		int[][] stairsHalf = { { 4, 0 }, { 4, 0 }, { 0, 4 }, { 0, 4 }, { -4, 0 }, { -4, 0 }, { 0, -4 }, { 0, -4 } };
		for (int y = yStart + 1; y < yEnde - 1; y++) {
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(44));
			x += stairsHalf[i][0];
			z += stairsHalf[i][1];
			i++;
			if (i == stairsHalf.length)
				i = 0;
		}

		// place lava
		SkyBlockMultiplayer.makeBlock(2, yEnde - 3, 2, Material.LAVA);
		// place water
		SkyBlockMultiplayer.makeBlock(-1, yEnde - 3, 0, Material.WATER);

		// create roof
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				SkyBlockMultiplayer.makeBlock(x, yEnde - 2, z, Material.getMaterial(43));
			}
		}

		// fence
		for (x = -2; x < 4; x++) {
			SkyBlockMultiplayer.makeBlock(x, yEnde - 1, -2, Material.FENCE);
		}
		for (z = -2; z < 4; z++) {
			SkyBlockMultiplayer.makeBlock(-2, yEnde - 1, z, Material.FENCE);
			SkyBlockMultiplayer.makeBlock(3, yEnde - 1, z, Material.FENCE);
		}

		// torches
		SkyBlockMultiplayer.makeBlock(-2, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(-2, yEnde, 3, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, 3, Material.TORCH);

		// create the tower floor
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				for (int y = 0; y < yStart; y++) {
					SkyBlockMultiplayer.makeBlock(x, y, z, Material.AIR);
				}
				SkyBlockMultiplayer.makeBlock(x, yStart, z, Material.getMaterial(43));
			}
		}

		//create signs
		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).getState();
		s1.setLine(0, Language.MSGS_SIGN1LINE1.getSentence());
		s1.setLine(1, Language.MSGS_SIGN1LINE2.getSentence());
		s1.setLine(2, Language.MSGS_SIGN1LINE3.getSentence());
		s1.update();
		s1.getBlock().setData((byte) 8);
		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).getState();
		s2.setLine(0, Language.MSGS_SIGN2LINE1.getSentence());
		s2.setLine(1, Language.MSGS_SIGN2LINE2.getSentence());
		s2.setLine(2, Language.MSGS_SIGN2LINE3.getSentence());
		s2.setLine(3, Language.MSGS_SIGN2LINE4.getSentence());
		s2.update();
		s2.getBlock().setData((byte) 8);
	}

	public static String getOwner(Location l) {
		int islandNumber = CreateIsland.getIslandNumber(l);
		if (islandNumber == 0) {
			return "";
		}

		IslandInfo ii = settings.getIslandInfo(islandNumber);

		if (ii == null) {
			return "";
		}
		return ii.getIslandOwner();
	}

	/*public static boolean checkBuildPermission(PlayerInfo pi, Location l) {
		if (l == null || pi == null) {
			return false;
		}

		int islandNumber = CreateIsland.getIslandNumber(l);
		IslandInfo ii = settings.getIslandInfo(islandNumber);
		if (ii == null) {
			return false;
		}

		if (ii.isIslandOwner(pi.getPlayerName()) || ii.containsFriend(pi.getPlayerName())) {
			return true;
		}

		return false;
	}
	/*public static boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		if (pi == null || pi.getIslandLocation() == null) {
			return false;
		}
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

		int dist = 0;
		if (Settings.build_withProtectedBorder) {
			dist = (Settings.distanceIslands / 2) - 3;
		} else {
			dist = Settings.distanceIslands / 2;
		}

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}*/
}
