package me.lukas.skyblockmultiplayer;

public enum EnumIslandInfo {

	ISLAND_NUMBER("islandNumber"),
	FRIENDS("friends"),
	ISLAND_OWNER("islandOwner"),
	ISLAND_LOCATION("islandLocation"),
	HOME_LOCATION("homeLocation");

	private StringBuilder path;

	private EnumIslandInfo(String path) {
		this.path = new StringBuilder(path);
	}
	
	public String getPath() {
		return this.path.toString();
	}
}
