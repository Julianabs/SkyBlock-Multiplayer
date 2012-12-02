package me.lukas.skyblockmultiplayer;

public enum EnumPlayerConfig {

	PLAYER_NAME("playerName"),
	ISLAND_NUMBER("islandNumber"),
	ISLAND_BUILTLIST("buildlist"),
	IS_DEAD("isDead"),
	IS_PLAYING("isPlaying"),
	LIVES_LEFT("livesLeft"),
	ISLANDS_LEFT("islandsLeft"),

	OLD_LOCATION("oldLocation"),
	OLD_INVENTORY("oldInventory"),
	OLD_ARMOR("oldArmor"),
	OLD_FOOD("oldFood"),
	OLD_HEALTH("oldHealth"),
	OLD_EXP("oldExp"),
	OLD_LEVEL("oldLevel"),

	ISLAND_LOCATION("islandocation"),
	ISLAND_INVENTORY("islandInventory"),
	ISLAND_ARMOR("islandArmor"),
	ISLAND_FOOD("islandFood"),
	ISLAND_HEALTH("islandHealth"),
	ISLAND_EXP("islandExp"),
	ISLAND_LEVEL("islandLevel");

	private StringBuilder path;

	private EnumPlayerConfig(String path) {
		this.path = new StringBuilder(path);
	}
	
	public String getPath() {
		return this.path.toString();
	}
}
