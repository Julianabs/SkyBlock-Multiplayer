package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo {

	private StringBuilder playerName;

	private boolean hasIsland;
	private boolean isOnIsland;
	private boolean isDead;

	private int livesLeft;
	private int islandsLeft;

	private Location islandLocation;
	private Location homeLocation;
	private Location oldLocation;

	private ItemStack[] islandInventory;
	private ItemStack[] islandArmor;

	private ItemStack[] oldInventory;
	private ItemStack[] oldArmor;

	private ArrayList<StringBuilder> friends;

	private int islandFood;
	private int oldFood;

	private int islandHealth;
	private int oldHealth;

	private float islandExp;
	private float oldExp;

	private int islandLevel;
	private int oldLevel;

	public PlayerInfo(String playerName) {

		// checke, ob playerfile existiert, ansonsten neu erstellen

		this.playerName = new StringBuilder(playerName);
		this.loadPlayerInfo();
		/*
		this.hasIsland = false;
		this.isDead = false;

		this.livesLeft = Settings.pvp_livesPerIsland;
		this.islandsLeft = Settings.pvp_islandsPerPlayer;

		this.islandLocation = null;
		this.homeLocation = null;
		this.oldLocation = null;

		this.islandInventory = new ArrayList<String>();
		this.islandArmor = new ArrayList<String>();

		this.oldInventory = new ArrayList<String>();
		this.oldArmor = new ArrayList<String>();

		this.friends = new ArrayList<String>();

		this.islandFood = 0;
		this.oldFood = 0;

		this.islandHealth = 0;
		this.oldHealth = 0;

		this.islandExp = 0;
		this.oldExp = 0;

		this.islandLevel = 0;
		this.oldLevel = 0;*/

	}

	public void setPlayerName(String s) {
		this.playerName = new StringBuilder(s);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.playerName.toString());
	}

	public String getPlayerName() {
		return this.playerName.toString();
	}

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public void setDead(boolean b) {
		this.isDead = b;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setLivesLeft(int i) {
		this.livesLeft = i;
	}

	public int getLivesLeft() {
		return this.livesLeft;
	}

	public void setIslandsLeft(int i) {
		this.islandsLeft = i;
	}

	public int getIslandsLeft() {
		return this.islandsLeft;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}

	public void setHomeLocation(Location l) {
		this.homeLocation = l;
	}

	public Location getHomeLocation() {
		return this.homeLocation;
	}

	public void setOldLocation(Location l) {
		if (!l.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			this.oldLocation = l;
		}
	}

	public Location getOldLocation() {
		return this.oldLocation;
	}

	public void setIslandInventory(ItemStack[] items) {
		if (items == null)
			items = new ItemStack[36];
		this.islandInventory = items;
	}

	public ItemStack[] getIslandInventory() {
		return this.islandInventory;
	}

	public void setIslandArmor(ItemStack[] items) {
		if (items == null)
			items = new ItemStack[36];
		this.islandArmor = items;
	}

	public ItemStack[] getIslandArmor() {
		return this.islandArmor;
	}

	public void setOldInventory(ItemStack[] items) {
		if (items == null)
			items = new ItemStack[36];
		this.oldInventory = items;
	}

	public ItemStack[] getOldInventory() {
		return this.oldInventory;
	}

	public void setOldArmor(ItemStack[] items) {
		if (items == null)
			items = new ItemStack[36];
		this.oldArmor = items;
	}

	public ItemStack[] getOldArmor() {
		return this.oldArmor;
	}

	public void addFriend(String s) {
		this.friends.add(new StringBuilder(s));
	}

	public void removeFriend(String s) {
		this.friends.remove(new StringBuilder(s));
	}

	public ArrayList<StringBuilder> getFriends() {
		return this.friends;
	}

	public void setIslandExp(float i) {
		this.islandExp = i;
	}

	public float getIslandExp() {
		return this.islandExp;
	}

	public void setOldExp(float i) {
		this.oldExp = i;
	}

	public float getOldExp() {
		return this.oldExp;
	}

	public void setIslandLevel(int i) {
		this.islandLevel = i;
	}

	public int getIslandLevel() {
		return this.islandLevel;
	}

	public void setOldLevel(int i) {
		this.oldLevel = i;
	}

	public int getOldLevel() {
		return this.oldLevel;
	}

	public void setIslandFood(int i) {
		this.islandFood = i;
	}

	public int getIslandFood() {
		return this.islandFood;
	}

	public void setOldFood(int i) {
		this.oldFood = i;
	}

	public int getOldFood() {
		return this.oldFood;
	}

	public void setIslandHealth(int i) {
		this.islandHealth = i;
	}

	public int getIslandHealth() {
		return this.islandHealth;
	}

	public void setOldHealth(int i) {
		this.oldHealth = i;
	}

	public int getOldHealth() {
		return this.oldHealth;
	}

	public boolean getIsOnIsland() {
		return this.isOnIsland;
	}

	public void setIsOnIsland(boolean b) {
		this.isOnIsland = b;
	}

	private Location parseStringToLocation(String s) {
		if (s == null || s.trim() == "") {
			return null;
		}
		String[] parts = s.split(":");
		if (parts.length == 6) {
			World w = Bukkit.getServer().getWorld(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = Integer.parseInt(parts[3]);
			float yaw = Float.parseFloat(parts[4]);
			float pitch = Float.parseFloat(parts[5]);
			return new Location(w, x, y, z, yaw, pitch);
		}
		return null;
	}

	private String getStringFromLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch();

	}

	@SuppressWarnings("unchecked")
	public void loadPlayerInfo() throws Exception {
		YamlConfiguration yamlPlayerData = new YamlConfiguration();
		File filePlayerData = new File("players", this.playerName + ".yml");
		yamlPlayerData.load(filePlayerData);

		boolean hasIsland = false;
		if (yamlPlayerData.contains(EnumPlayerConfig.HAS_ISLAND.getPath())) {
			hasIsland = yamlPlayerData.getBoolean(EnumPlayerConfig.HAS_ISLAND.getPath());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.HAS_ISLAND.getPath(), false);
		}
		this.hasIsland = hasIsland;

		boolean isOnIsland = false;
		if (yamlPlayerData.contains(EnumPlayerConfig.IS_ON_ISLAND.getPath())) {
			isOnIsland = Boolean.parseBoolean(yamlPlayerData.get(EnumPlayerConfig.IS_ON_ISLAND.getPath()).toString());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.IS_ON_ISLAND.getPath(), false);
		}
		this.isOnIsland = isOnIsland;

		boolean isDead = false;
		if (yamlPlayerData.contains(EnumPlayerConfig.IS_DEAD.getPath())) {
			isDead = Boolean.parseBoolean(yamlPlayerData.get(EnumPlayerConfig.IS_DEAD.getPath()).toString());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.IS_DEAD.getPath(), false);
		}
		this.isDead = isDead;

		Location homeLocation = null;
		if (yamlPlayerData.contains(EnumPlayerConfig.HOME_LOCATION.getPath())) {
			homeLocation = this.parseStringToLocation(yamlPlayerData.get(EnumPlayerConfig.HOME_LOCATION.getPath()).toString());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.HOME_LOCATION.getPath(), "");
		}
		this.homeLocation = homeLocation;

		Location islandLocation = null;
		if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_LOCATION.getPath())) {
			islandLocation = this.parseStringToLocation(yamlPlayerData.get(EnumPlayerConfig.ISLAND_LOCATION.getPath()).toString());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.ISLAND_LOCATION.getPath(), "");
		}
		this.islandLocation = islandLocation;

		ArrayList<StringBuilder> friends = new ArrayList<StringBuilder>();
		if (yamlPlayerData.contains(EnumPlayerConfig.FRIENDS.getPath())) {
			for (String friend : (ArrayList<String>) yamlPlayerData.get(EnumPlayerConfig.FRIENDS.getPath())) {
				friends.add(new StringBuilder(friend));
			}
		} else {
			yamlPlayerData.set(EnumPlayerConfig.FRIENDS.getPath(), "");
		}
		this.friends = friends;

		int islandFood = 20;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_FOOD.getPath())) {
				islandFood = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.ISLAND_FOOD.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.ISLAND_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.islandFood = islandFood;

		int islandHealth = 20;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_HEALTH.getPath())) {
				islandHealth = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.ISLAND_HEALTH.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.ISLAND_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.islandHealth = islandHealth;

		int islandExp = 0;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_EXP.getPath())) {
				islandExp = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.ISLAND_EXP.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.ISLAND_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.islandExp = islandExp;

		int islandLevel = 0;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_LEVEL.getPath())) {
				islandLevel = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.ISLAND_LEVEL.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.ISLAND_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.islandLevel = islandLevel;

		ItemStack[] islandInventory = new ItemStack[36];
		if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_INVENTORY.getPath())) {
			ArrayList<String> listIslandInventory = (ArrayList<String>) yamlPlayerData.get(EnumPlayerConfig.ISLAND_INVENTORY.getPath());
			for (String s : listIslandInventory) {
				String[] parts = s.split("-");
				int slot = Integer.parseInt(parts[0]);
				ItemStack item = ItemParser.getItemStackfromString(parts[1]);
				islandInventory[slot] = item;
			}
		} else {
			yamlPlayerData.set(EnumPlayerConfig.ISLAND_INVENTORY.getPath(), new ArrayList<String>());
		}
		this.islandInventory = islandInventory;

		ItemStack[] islandArmor = new ItemStack[4];
		if (yamlPlayerData.contains(EnumPlayerConfig.ISLAND_ARMOR.getPath())) {
			ArrayList<String> listIslandArmor = (ArrayList<String>) yamlPlayerData.get(EnumPlayerConfig.ISLAND_ARMOR.getPath());
			for (String s : listIslandArmor) {
				String[] parts = s.split("-");
				int slot = Integer.parseInt(parts[0]);
				ItemStack item = ItemParser.getItemStackfromString(parts[1]);
				islandArmor[slot] = item;
			}
		} else {
			yamlPlayerData.set(EnumPlayerConfig.ISLAND_ARMOR.getPath(), new ArrayList<String>());
		}
		this.islandArmor = islandArmor;

		Location oldLocation = null;
		if (yamlPlayerData.contains(EnumPlayerConfig.OLD_LOCATION.getPath())) {
			oldLocation = this.parseStringToLocation(yamlPlayerData.get(EnumPlayerConfig.OLD_LOCATION.getPath()).toString());
		} else {
			yamlPlayerData.set(EnumPlayerConfig.OLD_LOCATION.getPath(), "");
		}
		this.oldLocation = oldLocation;

		int oldFood = 20;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.OLD_FOOD.getPath())) {
				oldFood = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.OLD_FOOD.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.OLD_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.oldFood = oldFood;

		int oldHealth = 20;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.OLD_HEALTH.getPath())) {
				oldHealth = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.OLD_HEALTH.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.OLD_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.oldHealth = oldHealth;

		int oldExp = 0;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.OLD_EXP.getPath())) {
				oldExp = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.OLD_EXP.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.OLD_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.oldExp = oldExp;

		int oldLevel = 0;
		try {
			if (yamlPlayerData.contains(EnumPlayerConfig.OLD_LEVEL.getPath())) {
				oldLevel = Integer.parseInt(yamlPlayerData.get(EnumPlayerConfig.OLD_LEVEL.getPath()).toString());
			} else {
				yamlPlayerData.set(EnumPlayerConfig.OLD_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.oldLevel = oldLevel;

		ItemStack[] oldInventory = new ItemStack[36];
		if (yamlPlayerData.contains(EnumPlayerConfig.OLD_INVENTORY.getPath())) {
			ArrayList<String> listOldInventory = (ArrayList<String>) yamlPlayerData.get(EnumPlayerConfig.OLD_INVENTORY.getPath());
			for (String s : listOldInventory) {
				String[] parts = s.split("-");
				int slot = Integer.parseInt(parts[0]);
				ItemStack item = ItemParser.getItemStackfromString(parts[1]);
				oldInventory[slot] = item;
			}
		} else {
			yamlPlayerData.set(EnumPlayerConfig.OLD_INVENTORY.getPath(), new ArrayList<String>());
		}
		this.oldInventory = oldInventory;

		ItemStack[] oldArmor = new ItemStack[4];
		if (yamlPlayerData.contains(EnumPlayerConfig.OLD_ARMOR.getPath())) {
			ArrayList<String> listOldArmor = (ArrayList<String>) yamlPlayerData.get(EnumPlayerConfig.OLD_ARMOR.getPath());
			for (String s : listOldArmor) {
				String[] parts = s.split("-");
				int slot = Integer.parseInt(parts[0]);
				ItemStack item = ItemParser.getItemStackfromString(parts[1]);
				oldArmor[slot] = item;
			}
		} else {
			yamlPlayerData.set(EnumPlayerConfig.OLD_ARMOR.getPath(), new ArrayList<String>());
		}
		this.oldArmor = oldArmor;

		/*yamlPlayerData.load(filePlayerData);
		for (String friend : yamlPlayerData.getConfigurationSection("friends").getKeys(false)) {
			
		}*/

		yamlPlayerData.save(filePlayerData);
	}

	public void savePlayerInfo() {
		YamlConfiguration yamlPlayerData = new YamlConfiguration();
		File filePlayerData = new File("players", this.playerName + ".yml");
		
		yamlPlayerData.set(EnumPlayerConfig.HAS_ISLAND.getPath(), this.hasIsland);
		yamlPlayerData.set(EnumPlayerConfig.IS_ON_ISLAND.getPath(), this.isOnIsland);
		yamlPlayerData.set(EnumPlayerConfig.IS_DEAD.getPath(), this.isDead);
		
		
		
	}

}
