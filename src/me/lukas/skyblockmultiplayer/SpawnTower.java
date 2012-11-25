package me.lukas.skyblockmultiplayer;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class SpawnTower {

	private static void makeBlock(int x, int y, int z, Material m) {
		if (!SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(x, y, z).getType().equals(m)) {
			SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(x, y, z).setType(m);
		}
	}

	private static void quader(int x, int y, int z, Material m) {
		if (y < 0)
			return;
		SpawnTower.makeBlock(x, y, z, m);
		SpawnTower.makeBlock(x, y, z + 1, m);
		SpawnTower.makeBlock(x + 1, y, z, m);
		SpawnTower.makeBlock(x + 1, y, z + 1, m);
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
			SpawnTower.quader(0, y, 0, Material.OBSIDIAN);
			// create wall inside
			for (int xw = -2; xw < 4; xw++) {
				SpawnTower.makeBlock(xw, y, -2, Material.GLASS);
				SpawnTower.makeBlock(xw, y, 3, Material.GLASS);
			}
			for (int zw = -2; zw < 4; zw++) {
				SpawnTower.makeBlock(-2, y, zw, Material.GLASS);
				SpawnTower.makeBlock(3, y, zw, Material.GLASS);
			}
			// create lava steps
			SpawnTower.quader(x, y, z, Material.getMaterial(43));
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
			SpawnTower.quader(x, y - 3, z, Material.GLASS);
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
			SpawnTower.quader(x, y, z, Material.getMaterial(43));
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
			SpawnTower.quader(x, y, z, Material.getMaterial(44));
			x += stairsHalf[i][0];
			z += stairsHalf[i][1];
			i++;
			if (i == stairsHalf.length)
				i = 0;
		}

		// place lava
		SpawnTower.makeBlock(2, yEnde - 3, 2, Material.LAVA);
		// place water
		SpawnTower.makeBlock(-1, yEnde - 3, 0, Material.WATER);

		// create roof
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				SpawnTower.makeBlock(x, yEnde - 2, z, Material.getMaterial(43));
			}
		}

		// fence
		for (x = -2; x < 4; x++) {
			SpawnTower.makeBlock(x, yEnde - 1, -2, Material.FENCE);
		}
		for (z = -2; z < 4; z++) {
			SpawnTower.makeBlock(-2, yEnde - 1, z, Material.FENCE);
			SpawnTower.makeBlock(3, yEnde - 1, z, Material.FENCE);
		}

		// torches
		SpawnTower.makeBlock(-2, yEnde, -2, Material.TORCH);
		SpawnTower.makeBlock(-2, yEnde, 3, Material.TORCH);
		SpawnTower.makeBlock(3, yEnde, -2, Material.TORCH);
		SpawnTower.makeBlock(3, yEnde, 3, Material.TORCH);

		// create the tower floor
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				for (int y = 0; y < yStart; y++) {
					SpawnTower.makeBlock(x, y, z, Material.AIR);
				}
				SpawnTower.makeBlock(x, yStart, z, Material.getMaterial(43));
			}
		}

		//create signs
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).getState();
		s1.setLine(0, Language.MSGS_SIGN1LINE1.getSentence());
		s1.setLine(1, Language.MSGS_SIGN1LINE2.getSentence());
		s1.setLine(2, Language.MSGS_SIGN1LINE3.getSentence());
		s1.update();
		s1.getBlock().setData((byte) 8);
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).getState();
		s2.setLine(0, Language.MSGS_SIGN2LINE1.getSentence());
		s2.setLine(1, Language.MSGS_SIGN2LINE2.getSentence());
		s2.setLine(2, Language.MSGS_SIGN2LINE3.getSentence());
		s2.setLine(3, Language.MSGS_SIGN2LINE4.getSentence());
		s2.update();
		s2.getBlock().setData((byte) 8);
	}
}
