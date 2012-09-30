package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo {

	private StringBuilder playerName;

	private IslandInfo islandInfo;
	private boolean isOnIsland;
	private boolean isDead;

	private int livesLeft;
	private int islandsLeft;

	private Location oldLocation;

	private ItemStack[] islandInventory;
	private ItemStack[] islandArmor;

	private ItemStack[] oldInventory;
	private ItemStack[] oldArmor;

	private HashMap<Integer, IslandInfo> buildPermissions;

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
		try {
			this.loadPlayerInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.islandInfo == null) {

		}

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

	public boolean getHasIsland() {
		return this.islandInfo != null;
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
		if (this.islandInfo == null)
			return;
		this.islandInfo.setIslandLocation(l);
	}

	public Location getIslandLocation() {
		if (this.islandInfo == null)
			return null;
		return this.islandInfo.getIslandLocation();
	}

	public void setHomeLocation(Location l) {
		if (this.islandInfo == null)
			return;
		this.islandInfo.setHomeLocation(l);
	}

	public Location getHomeLocation() {
		if (this.islandInfo == null)
			return null;
		return this.islandInfo.getHomeLocation();
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

	/**
	 * TODO: Change friend methods 
	 * @param s
	 */

	public void addFriend(StringBuilder playerName) {
		this.islandInfo.addFriend(playerName);
	}

	public void removeFriend(String s) {
		this.islandInfo.removeFriend(s);
	}

	public boolean canBuildOnIslandNr(int islandnr) {
		// Check own island
		if (islandnr == this.islandInfo.getIslandNr()){
			return true;
		}
		// check island list
		IslandInfo built = this.buildPermissions.get(islandnr);
		if (built == null)
			return false;
		if (built.containsFriend(this.playerName)){
			return true;
		}
		return false;
	}
	
	public IslandInfo getIslandInfoFromFriend(String playername) {
		// check own islandlist for name
		for (IslandInfo islandinfo : this.buildPermissions.values()){
			if (islandinfo.isIslandOwner(playername) && islandinfo.containsFriend(this.playerName)){
				return islandinfo;
			}
		}
		return null;
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

	public void setIslandInfo(IslandInfo ii) {
		this.islandInfo = ii;
	}
	
	public HashMap<Integer,IslandInfo> getBuiltPermissionList(){
		return this.buildPermissions;
	}

	@SuppressWarnings("unchecked")
	private void loadPlayerInfo() throws Exception {
		YamlConfiguration yamlPlayerInfo = new YamlConfiguration();
		File filePlayerData = new File("players", this.playerName + ".yml");
		yamlPlayerInfo.load(filePlayerData);

		// Find own Island
		int ownsIslandNr = -1;
		if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_NUMBER.getPath())) {
			ownsIslandNr = yamlPlayerInfo.getInt(EnumPlayerInfo.ISLAND_NUMBER.getPath());
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_NUMBER.getPath(), ownsIslandNr);
		}
		this.islandInfo = Settings.islands.get(ownsIslandNr);
		// Check consistency
		if (this.islandInfo != null && !this.islandInfo.isIslandOwner(this.playerName.toString())) {
			this.islandInfo = null;
		}

		// Find Buildpermissions
		this.buildPermissions = new HashMap<Integer, IslandInfo>();

		ArrayList<Integer> builtlist = new ArrayList<Integer>();
		if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_BUILTLIST.getPath())) {
			builtlist = (ArrayList<Integer>) yamlPlayerInfo.getList(EnumPlayerInfo.ISLAND_BUILTLIST.getPath());
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_BUILTLIST.getPath(), builtlist);
		}
		// Go through buildlist with Islandnr to get IslandInfos
		for (int islandnr : builtlist) {
			IslandInfo friend = Settings.islands.get(islandnr);
			if (friend != null && friend.containsFriend(this.playerName)) {
				this.buildPermissions.put(islandnr, friend);
			}
		}
		
		
		boolean isOnIsland = false;
		if (yamlPlayerInfo.contains(EnumPlayerInfo.IS_ON_ISLAND.getPath())) {
			isOnIsland = Boolean.parseBoolean(yamlPlayerInfo.get(EnumPlayerInfo.IS_ON_ISLAND.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.IS_ON_ISLAND.getPath(), false);
		}
		this.isOnIsland = isOnIsland;

		boolean isDead = false;
		if (yamlPlayerInfo.contains(EnumPlayerInfo.IS_DEAD.getPath())) {
			isDead = Boolean.parseBoolean(yamlPlayerInfo.get(EnumPlayerInfo.IS_DEAD.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.IS_DEAD.getPath(), false);
		}
		this.isDead = isDead;

		int islandFood = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_FOOD.getPath())) {
				islandFood = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_FOOD.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.islandFood = islandFood;

		int islandHealth = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_HEALTH.getPath())) {
				islandHealth = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_HEALTH.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.islandHealth = islandHealth;

		int islandExp = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_EXP.getPath())) {
				islandExp = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_EXP.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.islandExp = islandExp;

		int islandLevel = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_LEVEL.getPath())) {
				islandLevel = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_LEVEL.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.islandLevel = islandLevel;

		ItemStack[] islandInventory = new ItemStack[36];
		if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_INVENTORY.getPath())) {
			ArrayList<String> listIslandInventory = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_INVENTORY.getPath());
			islandInventory = ItemParser.getItemStackArrayFromList(listIslandInventory, 36);
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_INVENTORY.getPath(), new ArrayList<String>());
		}
		this.islandInventory = islandInventory;

		ItemStack[] islandArmor = new ItemStack[4];
		if (yamlPlayerInfo.contains(EnumPlayerInfo.ISLAND_ARMOR.getPath())) {
			ArrayList<String> listIslandArmor = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerInfo.ISLAND_ARMOR.getPath());
			islandArmor = ItemParser.getItemStackArrayFromList(listIslandArmor, 4);
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.ISLAND_ARMOR.getPath(), new ArrayList<String>());
		}
		this.islandArmor = islandArmor;

		Location oldLocation = null;
		if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_LOCATION.getPath())) {
			oldLocation = LocationParser.parseStringToLocation(yamlPlayerInfo.get(EnumPlayerInfo.OLD_LOCATION.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.OLD_LOCATION.getPath(), "");
		}
		this.oldLocation = oldLocation;

		int oldFood = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_FOOD.getPath())) {
				oldFood = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.OLD_FOOD.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.OLD_FOOD.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.oldFood = oldFood;

		int oldHealth = 20;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_HEALTH.getPath())) {
				oldHealth = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.OLD_HEALTH.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.OLD_HEALTH.getPath(), 20);
			}
		} catch (Exception e) {
		}
		this.oldHealth = oldHealth;

		int oldExp = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_EXP.getPath())) {
				oldExp = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.OLD_EXP.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.OLD_EXP.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.oldExp = oldExp;

		int oldLevel = 0;
		try {
			if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_LEVEL.getPath())) {
				oldLevel = Integer.parseInt(yamlPlayerInfo.get(EnumPlayerInfo.OLD_LEVEL.getPath()).toString());
			} else {
				yamlPlayerInfo.set(EnumPlayerInfo.OLD_LEVEL.getPath(), 0);
			}
		} catch (Exception e) {
		}
		this.oldLevel = oldLevel;

		ItemStack[] oldInventory = new ItemStack[36];
		if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_INVENTORY.getPath())) {
			ArrayList<String> listOldInventory = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerInfo.OLD_INVENTORY.getPath());
			oldInventory = ItemParser.getItemStackArrayFromList(listOldInventory, 36);
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.OLD_INVENTORY.getPath(), new ArrayList<String>());
		}
		this.oldInventory = oldInventory;

		ItemStack[] oldArmor = new ItemStack[4];
		if (yamlPlayerInfo.contains(EnumPlayerInfo.OLD_ARMOR.getPath())) {
			ArrayList<String> listOldArmor = (ArrayList<String>) yamlPlayerInfo.get(EnumPlayerInfo.OLD_ARMOR.getPath());
			oldArmor = ItemParser.getItemStackArrayFromList(listOldArmor, 4);
		} else {
			yamlPlayerInfo.set(EnumPlayerInfo.OLD_ARMOR.getPath(), new ArrayList<String>());
		}
		this.oldArmor = oldArmor;

		/*yamlPlayerData.load(filePlayerData);
		for (String friend : yamlPlayerData.getConfigurationSection("friends").getKeys(false)) {
			
		}*/

		yamlPlayerInfo.save(filePlayerData);
	}

	public void savePlayerInfo() {
		YamlConfiguration yamlPlayerData = new YamlConfiguration();
		File filePlayerInfo = new File("players", this.playerName + ".yml");

		yamlPlayerData.set(EnumPlayerInfo.ISLAND_NUMBER.getPath(), this.islandInfo.getIslandNr());
		yamlPlayerData.set(EnumPlayerInfo.IS_ON_ISLAND.getPath(), this.isOnIsland);
		yamlPlayerData.set(EnumPlayerInfo.IS_DEAD.getPath(), this.isDead);

		/** Do-TO **/
		// yamlPlayerData.set(EnumPlayerConfig.FRIENDS.getPath(), ); 
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_FOOD.getPath(), "" + this.islandFood);
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_HEALTH.getPath(), "" + this.islandHealth);
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_EXP.getPath(), "" + this.islandExp);
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_LEVEL.getPath(), "" + this.islandLevel);
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_INVENTORY.getPath(), ItemParser.getListFromItemStackArray(this.islandInventory));
		yamlPlayerData.set(EnumPlayerInfo.ISLAND_ARMOR.getPath(), ItemParser.getListFromItemStackArray(this.islandArmor));
		yamlPlayerData.set(EnumPlayerInfo.OLD_LOCATION.getPath(), LocationParser.getStringFromLocation(this.oldLocation));
		yamlPlayerData.set(EnumPlayerInfo.OLD_FOOD.getPath(), "" + this.oldFood);
		yamlPlayerData.set(EnumPlayerInfo.OLD_HEALTH.getPath(), "" + this.oldHealth);
		yamlPlayerData.set(EnumPlayerInfo.OLD_EXP.getPath(), "" + this.oldExp);
		yamlPlayerData.set(EnumPlayerInfo.OLD_LEVEL.getPath(), "" + this.oldLevel);
		yamlPlayerData.set(EnumPlayerInfo.OLD_INVENTORY.getPath(), ItemParser.getListFromItemStackArray(this.oldInventory));
		yamlPlayerData.set(EnumPlayerInfo.OLD_ARMOR.getPath(), ItemParser.getListFromItemStackArray(this.oldArmor));

		try {
			yamlPlayerData.save(filePlayerInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
