package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;

public class IslandInfo {

	private int nr;
	private StringBuilder islandOwner;
	private ArrayList<StringBuilder> friends;
	private Location homeLocation;
	private Location islandLocation;

	public IslandInfo(int islandNr, String islandOwner) { // for new Islands
		this.nr = islandNr;
		this.islandOwner = new StringBuilder(islandOwner);
		this.friends = new ArrayList<StringBuilder>();
	}

	public IslandInfo(int islandNr) { // for loading yml files
		this.nr = islandNr;

		try {
			this.loadIslandInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIslandOwner() {
		return this.islandOwner.toString();
	}

	public boolean isIslandOwner(String playerName) {
		if (this.islandOwner.equals(new StringBuilder(playerName))) {
			return true;
		}
		return false;
	}

	public void addFriend(StringBuilder friend) {
		this.friends.add(friend);
	}

	public void removeFriend(String friend) {
		this.friends.remove(new StringBuilder(friend));
	}

	public boolean containsFriend(StringBuilder playerName) {
		return this.friends.contains(playerName);
	}

	public ArrayList<StringBuilder> getFriends() {
		return this.friends;
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
		if (this.homeLocation == null) {
			return this.islandLocation;
		}
		return this.homeLocation;
	}

	public int getIslandNr() {
		return this.nr;
	}
	
	
	public Location getSafeTeleportLocation() {
		if (this.homeLocation != null) {
		
			if (this.isSafeLocation(this.homeLocation)) {
				return this.homeLocation;
			}
			// b) check if a suitable y exists on this x and z
			for (int y =  this.homeLocation.getBlockY(); y > 0; y--) {
				Location n = new Location( this.homeLocation.getWorld(),  this.homeLocation.getBlockX(), y,  this.homeLocation.getBlockZ());
				if (this.isSafeLocation(n)) {
					return n;
				}
			}
			for (int y = this.homeLocation.getBlockY(); y < 255; y++) {
				Location n = new Location(this.homeLocation.getWorld(), this.homeLocation.getBlockX(), y, this.homeLocation.getBlockZ());
				if (this.isSafeLocation(n)) {
					return n;
				}
			}
		}

		// c) check island Location
		
		if (this.isSafeLocation(this.islandLocation)) {
			return this.islandLocation;
		}

		for (int y = this.islandLocation.getBlockY(); y > 0; y--) {
			Location n = new Location(this.islandLocation.getWorld(), this.islandLocation.getBlockX(), y, this.islandLocation.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = this.islandLocation.getBlockY(); y < 255; y++) {
			Location n = new Location(this.islandLocation.getWorld(), this.islandLocation.getBlockX(), y, this.islandLocation.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		return null;
	}

	private boolean isSafeLocation(Location l) {
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

	@SuppressWarnings("unchecked")
	public void loadIslandInfo() throws Exception {
		YamlConfiguration yamlPlayerInfo = new YamlConfiguration();
		File fileIslandInfo = new File("islands", this.nr + ".yml");
		yamlPlayerInfo.load(fileIslandInfo);

		String islandOwner = "";
		if (yamlPlayerInfo.contains(EnumIslandInfo.ISLAND_OWNER.getPath())) {
			islandOwner = yamlPlayerInfo.get(EnumIslandInfo.ISLAND_OWNER.getPath()).toString();
		} else {
			yamlPlayerInfo.set(EnumIslandInfo.ISLAND_OWNER.getPath(), false);
		}
		this.islandOwner = new StringBuilder(islandOwner);

		Location islandLocation = null;
		if (yamlPlayerInfo.contains(EnumIslandInfo.ISLAND_LOCATION.getPath())) {
			islandLocation = LocationParser.parseStringToLocation(yamlPlayerInfo.get(EnumIslandInfo.ISLAND_LOCATION.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumIslandInfo.ISLAND_LOCATION.getPath(), LocationParser.getStringFromLocation(this.islandLocation));
		}
		this.islandLocation = islandLocation;

		Location homeLocation = null;
		if (yamlPlayerInfo.contains(EnumIslandInfo.HOME_LOCATION.getPath())) {
			islandLocation = LocationParser.parseStringToLocation(yamlPlayerInfo.get(EnumIslandInfo.HOME_LOCATION.getPath()).toString());
		} else {
			yamlPlayerInfo.set(EnumIslandInfo.HOME_LOCATION.getPath(), LocationParser.getStringFromLocation(this.homeLocation));
		}
		this.homeLocation = homeLocation;

		ArrayList<String> friends = new ArrayList<String>();
		if (yamlPlayerInfo.contains(EnumIslandInfo.FRIENDS.getPath())) {
			friends = (ArrayList<String>) yamlPlayerInfo.get(EnumIslandInfo.FRIENDS.getPath());
		} else {
			yamlPlayerInfo.set(EnumIslandInfo.FRIENDS.getPath(), this.friends);
		}

		this.friends = new ArrayList<StringBuilder>();
		for (String s : friends) {
			this.friends.add(new StringBuilder(s));
		}

		yamlPlayerInfo.save(fileIslandInfo);
	}

	public void saveIslandInfo() throws Exception {
		YamlConfiguration yamlIslandInfo = new YamlConfiguration();
		File filePlayerInfo = new File("islands", this.nr + ".yml");

		yamlIslandInfo.set(EnumIslandInfo.ISLAND_OWNER.getPath(), this.islandOwner.toString());
		yamlIslandInfo.set(EnumIslandInfo.ISLAND_LOCATION.getPath(), LocationParser.getStringFromLocation(this.islandLocation));
		yamlIslandInfo.set(EnumIslandInfo.HOME_LOCATION.getPath(), LocationParser.getStringFromLocation(this.islandLocation));
		
		ArrayList<String> friends = new ArrayList<String>();
		for (StringBuilder s: this.friends) {
			friends.add(s.toString());
		}
		yamlIslandInfo.set(EnumIslandInfo.FRIENDS.getPath(), friends);
		yamlIslandInfo.save(filePlayerInfo);
	}
	
	
}
