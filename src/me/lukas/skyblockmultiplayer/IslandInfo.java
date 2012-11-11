package me.lukas.skyblockmultiplayer;

import java.util.ArrayList;

import org.bukkit.Location;

public class IslandInfo {

	private int islandNumber;
	private String islandOwner;
	private ArrayList<String> friends;
	private Location homeLocation;
	private Location islandLocation;

	public IslandInfo(int islandNr) {
		this.islandNumber = islandNr;
		this.friends = new ArrayList<String>();
	}

	public String getIslandOwner() {
		return this.islandOwner;
	}

	public void setIslandOwner(String islandOwner) {
		this.islandOwner = islandOwner;
	}

	public boolean isIslandOwner(String playerName) {
		return this.islandOwner.equals(playerName);
	}

	public void addFriend(String friend) {
		this.friends.add(friend);
	}

	public void setFriends(ArrayList<String> friends) {
		this.friends = friends;
	}

	public void removeFriend(String friend) {
		this.friends.remove(friend);
	}

	public boolean containsFriend(String playerName) {
		return this.friends.contains(playerName);
	}

	public ArrayList<String> getFriends() {
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

	public int getIslandNumber() {
		return this.islandNumber;
	}
}
