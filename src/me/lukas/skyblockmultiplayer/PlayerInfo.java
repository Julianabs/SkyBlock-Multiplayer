package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo {

	private String playerName;
	private File filePlayerFolder;

	private IslandInfo islandInfo;
	private boolean isPlaying;
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
		this.playerName = playerName;

		this.isPlaying = false;
		this.isDead = false;

		this.livesLeft = SkyBlockMultiplayer.getInstance().getSettings().getLivesPerIsland();
		this.islandsLeft = SkyBlockMultiplayer.getInstance().getSettings().getIslandsPerPlayer();

		this.buildPermissions = new HashMap<Integer, IslandInfo>();

		this.oldLocation = null;

		this.islandInventory = new ItemStack[36];
		this.islandArmor = new ItemStack[4];

		this.oldInventory = new ItemStack[36];
		this.oldArmor = new ItemStack[4];

		this.islandFood = 0;
		this.oldFood = 0;

		this.islandHealth = 0;
		this.oldHealth = 0;

		this.islandExp = 0;
		this.oldExp = 0;

		this.islandLevel = 0;
		this.oldLevel = 0;

		this.filePlayerFolder = new File(SkyBlockMultiplayer.getInstance().getDataFolder() + File.separator + "players" + File.separator + playerName);
		if (!this.filePlayerFolder.exists())
			this.filePlayerFolder.mkdir();
	}

	public void setPlayerName(String s) {
		this.playerName = s;
	}

	public Player getPlayer() {
		return Bukkit.getPlayerExact(this.playerName);
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public File getPlayerFolder() {
		return this.filePlayerFolder;
	}

	public boolean getHasIsland() {
		return this.islandInfo != null;
	}

	public IslandInfo getIslandInfo() {
		return this.islandInfo;
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
		if (this.islandInfo == null) {
			return null;
		}
		return this.islandInfo.getHomeLocation();
	}

	public void setOldLocation(Location l) {
		if (l == null) {
			return;
		}
		if (!l.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getName())) {
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
			items = new ItemStack[4];
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
			items = new ItemStack[4];
		this.oldArmor = items;
	}

	public ItemStack[] getOldArmor() {
		return this.oldArmor;
	}

	public void addFriend(String playerName) {
		this.islandInfo.addFriend(playerName);
	}

	public void removeFriend(String s) {
		this.islandInfo.removeFriend(s);
	}

	public boolean havePermissionThere(Location l) {
		System.out.println("called");
		int islandNumber = CreateIsland.getIslandNumber(l);

		if (islandNumber == 0) {
			return false;
		}

		return this.havePermissionThere(islandNumber);
	}

	private boolean havePermissionThere(int islandnr) {
		// Check own island
		if (this.islandInfo != null && islandnr == this.islandInfo.getIslandNumber()) {
			return true;
		}
		// check island list
		IslandInfo built = this.buildPermissions.get(islandnr);
		if (built == null) {
			/*IslandInfo ii = SkyBlockMultiplayer.getInstance().getSettings().getIslandInfo(islandnr);
			if (ii != null && ii.isFreeBuild())
				return true;*/
			return false;
		}

		if (built.containsFriend(this.playerName)) {
			return true;
		}

		return false;
	}

	public void addBuildPermission(int islandNumber, IslandInfo ii) {
		this.buildPermissions.put(islandNumber, ii);
	}

	public IslandInfo getIslandInfoFromFriend(String playerName) {
		// check own islandlist for name
		for (IslandInfo islandinfo : this.buildPermissions.values()) {
			if (islandinfo.isIslandOwner(playerName) && islandinfo.containsFriend(this.playerName)) {
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

	public boolean isPlaying() {
		return this.isPlaying;
	}

	public void setIsPlaying(boolean b) {
		this.isPlaying = b;
	}

	public void setIslandInfo(IslandInfo ii) {
		this.islandInfo = ii;
	}

	public HashMap<Integer, IslandInfo> getBuiltPermissionList() {
		return this.buildPermissions;
	}

	public ArrayList<Integer> getBuildListNumbers() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer number : this.buildPermissions.keySet()) {
			list.add(number);
		}
		return list;
	}
}
