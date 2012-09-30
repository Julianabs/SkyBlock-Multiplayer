package me.lukas.skyblockmultiplayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationParser {

	public static Location parseStringToLocation(String s) {
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

	public static String getStringFromLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch();

	}
}
