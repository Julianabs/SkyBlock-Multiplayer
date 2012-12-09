package me.lukas.skyblockmultiplayer;

public enum EnumIslandConfig {

	ISLAND_NUMBER("islandNumber"),
	FRIENDS("friends"),
	ISLAND_OWNER("islandOwner"),
	ISLAND_LOCATION("islandLocation"),
	HOME_LOCATION("homeLocation"),
	FREE_BUILD("freeBuild");

	private StringBuilder path;

	private EnumIslandConfig(String path) {
		this.path = new StringBuilder(path);
	}
	
	public String getPath() {
		return this.path.toString();
	}
}
