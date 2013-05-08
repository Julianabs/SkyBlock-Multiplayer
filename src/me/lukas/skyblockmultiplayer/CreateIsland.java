package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.BlockVector;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class CreateIsland {

	private static void createIslandAtLocation(Location l) {
		try {
			File f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), SkyBlockMultiplayer.getInstance().getSettings().getIslandSchematic());
			if (f.exists() && f.isFile()) {
				Location islandLoc = new Location(l.getWorld(), l.getBlockX(), l.getBlockY() - 3, l.getBlockZ());
				int res = CreateIsland.createStructure(islandLoc, f);
				if (res != 1) {
					CreateIsland.createDefaultIsland(l);
					if (res == 0) {
						SkyBlockMultiplayer.getInstance().getLogger().warning("Island contains no bedrock.");
					} else {
						SkyBlockMultiplayer.getInstance().getLogger().warning("Island contains too much bedrock.");
					}
				}
			} else {
				CreateIsland.createDefaultIsland(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
			CreateIsland.createDefaultIsland(l);
		}
	}

	public static IslandInfo createIslandWithNr(int nr) {
		// Calculate Location from number
		Location l = CreateIsland.getIslandPosition(nr);
		CreateIsland.createIslandAtLocation(l);
		IslandInfo islandInfo = new IslandInfo(nr);
		SkyBlockMultiplayer.getInstance().getSettings().addIslandInfo(islandInfo);
		return islandInfo;
	}

	public static IslandInfo createNextIsland() {
		int numberIslands = 1;
		Location l = CreateIsland.getIslandPosition(numberIslands);

		while (CreateIsland.checkIfOccupied(l)) {
			numberIslands++;
			l = CreateIsland.getIslandPosition(numberIslands);
		}
		CreateIsland.createIslandAtLocation(l);
		IslandInfo islandInfo = new IslandInfo(numberIslands);
		islandInfo.setIslandLocation(l);
		SkyBlockMultiplayer.getInstance().getSettings().addIslandInfo(islandInfo);
		SkyBlockMultiplayer.getInstance().getSettings().addIslandInfo(islandInfo);
		return islandInfo;
	}

	public static void createIslands(int amount) {
		int numberIslands = 1;
		Location l = getIslandPosition(numberIslands);
		for (int i = 0; i < amount; i++) {
			while (checkIfOccupied(l)) {
				numberIslands++;
				l = getIslandPosition(numberIslands);
			}
			IslandInfo ii = new IslandInfo(numberIslands);
			ii.setIslandLocation(l);
			SkyBlockMultiplayer.getInstance().saveIslandInfo(ii);
			createIslandAtLocation(l);
		}
	}

	/*public static int getAmountOfIslands() {
		return new File(SkyBlockMultiplayer.getInstance()-.)
		int amountIslands = 1;
		do {
			Location locIsland = CreateIsland.getIslandPosition(amountIslands);
			int px = locIsland.getBlockX();
			int py = locIsland.getBlockY() - 3;
			int pz = locIsland.getBlockZ();
			if (!(new Location(SkyBlockMultiplayer.getInstance().getSkyBlockWorld(), px, py, pz).getBlock().getType() == Material.BEDROCK)) {
				break;
			}
			amountIslands++;
		} while (true);
		return amountIslands - 1;
	}*/

	public static int getIslandNumber(Location l) {
		int px = l.getBlockX();
		int pz = l.getBlockZ();

		float distance = SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();

		// tower
		if (px >= -(distance / 2.0) && px <= (distance / 2.0)) {
			if (pz >= -(distance / 2.0) && pz <= (distance / 2.0)) {
				return 0;
			}
		}

		if (SkyBlockMultiplayer.getInstance().getSettings().getWithProtectedBorder()) {
			//check if border
			int borderLength = 3 + 1;
			int xDistanceToBorder = Math.abs((px - (int) (distance / 2.0)) % (int) distance);
			if (xDistanceToBorder < borderLength || xDistanceToBorder > distance - borderLength) {
				return 0;
			}
			int zDistanceToBorder = Math.abs((pz - (int) (distance / 2.0)) % (int) distance);
			if (zDistanceToBorder < borderLength || zDistanceToBorder > distance - borderLength) {
				return 0;
			}
		}

		int xd = (int) Math.round(px / (distance));
		int zd = (int) Math.round(pz / (distance));
		int ring = Math.abs(xd) + Math.abs(zd);
		
		// seite and position auf seite
		int seite;
		int posSeite;
		if (xd < 0 && zd <= 0) {
			seite = 1;
			posSeite = -zd;
		} else if (xd >= 0 && zd < 0) {
			seite = 2;
			posSeite = xd;
		} else if (xd > 0 && zd >= 0) {
			seite = 3;
			posSeite = zd;
		} else {
			seite = 4;
			posSeite = -xd;
		}
		int nAufRing = posSeite + 1 + (seite - 1) * ring;
		int n = nAufRing + 2 * ring * (ring - 1);
		return n;
	}

	public static Location getIslandPosition(int n) {
		//System.out.println("Erstelle Inselnr.: "+n);
		int posX, posZ;
		// Suche den momentanen Ring
		int r = (int) (0.5 + Math.sqrt(n / 2.0 - 0.25));
		//System.out.println("Insel befindet sich in Ringnr "+r);
		// Bestimme die Anzahl bereits vorhanderer Inseln auf dem Ring
		int naufRing = n - 2 * r * (r - 1);
		//System.out.println("Die Insel ist im Ring die "+naufRing+" Insel.");
		// Bestimmen der Seite auf dem Ring
		int seite = (int) (Math.ceil(naufRing / (double) r));
		//System.out.println("Die Insel befindet sich auf der "+seite+" Seite.");
		// Bestimme die Position der Insel auf der Seite
		int posSeite = naufRing - (seite - 1) * r - 1;
		//System.out.println("und ist auf der Seite die "+posSeite+" Insel.");
		//System.out.println("Die Inseldistanz ist "+CreateNewIsland.IslandDistance);
		// Berechne die Positionen
		if (seite == 1) {
			posX = (posSeite - r) * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
			posZ = -posSeite * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
		} else if (seite == 2) {
			posX = posSeite * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
			posZ = (posSeite - r) * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
		} else if (seite == 3) {
			posX = (r - posSeite) * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
			posZ = posSeite * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
		} else {
			posX = -posSeite * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
			posZ = (r - posSeite) * SkyBlockMultiplayer.getInstance().getSettings().getIslandDistance();
		}
		//System.out.println("Die Insel befindet sich auf "+posX+" in X-Richtung.");
		//System.out.println("Die Insel befindet sich auf "+posZ+" in Z-Richtung.");

		// create location for island
		return new Location(SkyBlockMultiplayer.getInstance().getSkyBlockWorld(), posX, SkyBlockMultiplayer.getInstance().getSettings().getIslandYPosition(), posZ);
	}

	private static void createDefaultIsland(Location l) {
		int yPosition = SkyBlockMultiplayer.getInstance().getSettings().getIslandYPosition();

		// Erstelle unterste Erdebene
		createLayer(l, yPosition - 3, Material.DIRT);
		//Erstelle mittlere Erdebene
		createLayer(l, yPosition - 2, Material.DIRT);
		// Ersetze Erde durch Sand
		for (int x = 2; x <= 4; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(x + l.getBlockX(), yPosition - 2, z + l.getBlockZ()).setType(Material.SAND);
			}
		}
		//Erstelle oberste Grassebene
		createLayer(l, yPosition - 1, Material.GRASS);

		// create Chest		
		Block block = SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(0 + l.getBlockX(), yPosition, 4 + l.getBlockZ());
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		chest.getBlock().setData((byte) 2);

		for (int i = 0; i < SkyBlockMultiplayer.getInstance().getSettings().getItemsChest().length; i++) {
			try {
				chest.getInventory().addItem(SkyBlockMultiplayer.getInstance().getSettings().getItemsChest()[i]);
			} catch (Exception ex) {
			}
		}

		// create tree
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld().generateTree(new Location(SkyBlockMultiplayer.getInstance().getSkyBlockWorld(), 5 + l.getBlockX(), yPosition, l.getBlockZ()), TreeType.TREE);

		// place bedrock
		SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 3, l.getBlockZ()).setType(Material.BEDROCK);

	}

	private static void createLayer(Location l, int y, Material m) {
		for (int x = -1; x <= 6; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = 2; z <= 4; z++) {
				SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
	}

	private static boolean checkIfOccupied(Location l) {
		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				for (int z = -5; z <= 5; z++) {
					if (SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z).getType() != Material.AIR) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int createStructure(Location loc, File path) throws Exception {
		FileInputStream stream = new FileInputStream(path);
		NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

		// Schematic tag
		CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		if (!schematicTag.getName().equals("Schematic")) {
			throw new Exception("Tag \"Schematic\" does not exist or is not first");
		}

		// Check
		Map<String, Tag> schematic = schematicTag.getValue();
		if (!schematic.containsKey("Blocks")) {
			throw new Exception("Schematic file is missing a \"Blocks\" tag");
		}

		// Get information
		short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
		short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
		short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

		// Check type of Schematic
		String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
		if (!materials.equals("Alpha")) {
			throw new Exception("Schematic file is not an Alpha schematic");
		}

		// Get blocks
		byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
		byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();

		List<Tag> tileEntities = ((ListTag) getChildTag(schematic, "TileEntities", ListTag.class)).getValue();

		Map tileEntitiesMap = new HashMap();

		int amount = 0;
		for (byte b : blocks) {
			if (b == 7) {
				amount++;
			}
		}
		if (amount == 0) {
			return 0;
		}

		if (amount > 1) {
			return 2;
		}

		for (Tag tag : tileEntities) {
			if ((tag instanceof CompoundTag)) {
				CompoundTag t = (CompoundTag) tag;

				int x = 0;
				int y = 0;
				int z = 0;

				Map values = new HashMap();
				for (Map.Entry entry : t.getValue().entrySet()) {
					if (((String) entry.getKey()).equals("x")) {
						if ((entry.getValue() instanceof IntTag)) {
							x = ((IntTag) entry.getValue()).getValue().intValue();
						}

					} else if (((String) entry.getKey()).equals("y")) {
						if ((entry.getValue() instanceof IntTag)) {
							y = ((IntTag) entry.getValue()).getValue().intValue();
						}

					} else if ((((String) entry.getKey()).equals("z")) && ((entry.getValue() instanceof IntTag))) {
						z = ((IntTag) entry.getValue()).getValue().intValue();
					}

					values.put(entry.getKey(), entry.getValue());
				}

				BlockVector vec = new BlockVector(x, y, z);
				tileEntitiesMap.put(vec, values);
			}
		}

		int xB = 0, yB = 0, zB = 0;
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					int index = y * width * length + z * width + x;
					if (blocks[index] == 7) {
						xB = x;
						yB = y;
						zB = z;
						break;
					}
				}
			}
		}

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					int index = y * width * length + z * width + x;
					int id = blocks[index];
					int dat = blockData[index];

					int lx = loc.getBlockX();
					int ly = loc.getBlockY();
					int lz = loc.getBlockZ();
					Block b = SkyBlockMultiplayer.getInstance().getSkyBlockWorld().getBlockAt(lx + x - xB, ly + y - yB, lz + z - zB);

					b.setTypeIdAndData(id, (byte) dat, true);
					b.getState().update(true);

					BlockVector vec = new BlockVector(x, y, z);
					if (id == 63 || id == 68) { // sign
						if (id == 63) {
							b.setType(Material.SIGN_POST);
						} else {
							b.setType(Material.WALL_SIGN);
						}
						Sign s = (Sign) b.getState();
						s.setRawData((byte) dat);
						if (tileEntitiesMap.containsKey(vec)) {
							Map<String, Tag> values = (Map<String, Tag>) tileEntitiesMap.get(vec);
							String l1 = (String) values.get("Text1").getValue();
							String l2 = (String) values.get("Text2").getValue();
							String l3 = (String) values.get("Text3").getValue();
							String l4 = (String) values.get("Text4").getValue();

							s.setLine(0, l1);
							s.setLine(1, l2);
							s.setLine(2, l3);
							s.setLine(3, l4);
							s.update(true);
						}
					}

					if (id == 54) { // chest
						b.setType(Material.CHEST);
						Chest c = (Chest) b.getState();
						c.getData().setData((byte) dat);
						c.getInventory().setContents(CreateIsland.getContent(c.getInventory().getContents().length, tileEntitiesMap, vec));
						c.update(true);
					}
					if (id == 23) { // dispenser
						b.setType(Material.DISPENSER);
						Dispenser d = (Dispenser) b.getState();
						d.getData().setData((byte) dat);
						d.getInventory().setContents(CreateIsland.getContent(d.getInventory().getContents().length, tileEntitiesMap, vec));
						d.update(true);
					}
					/*if (id == 25) { // note block, need to be finished, note
						b.setType(Material.NOTE_BLOCK);
						NoteBlock n = (NoteBlock) b.getState();
						n.getData().setData((byte) dat);
						if (tileEntitiesMap.containsKey(vec)) {
							Map<String, Tag> values = (Map<String, Tag>) tileEntitiesMap.get(vec);
							byte note = (Byte) values.get("note").getValue();
							n.set
						}
						n.update(true);
					}*/
					if (id == 61 || id == 62) { // furnace
						b.setType(Material.FURNACE);
						Furnace f = (Furnace) b.getState();
						f.getData().setData((byte) dat);
						f.getInventory().setContents(CreateIsland.getContent(f.getInventory().getContents().length, tileEntitiesMap, vec));
						if (tileEntitiesMap.containsKey(vec)) {
							Map<String, Tag> values = (Map<String, Tag>) tileEntitiesMap.get(vec);
							short burn_time = (Short) values.get("BurnTime").getValue();
							short cook_time = (Short) values.get("CookTime").getValue();
							f.setBurnTime(burn_time);
							f.setCookTime(cook_time);
						}
						f.update(true);
					}
					if (id == 52) { // mob spawner
						b.setType(Material.MOB_SPAWNER);
						CreatureSpawner c = (CreatureSpawner) b.getState();
						c.getData().setData((byte) dat);
						if (tileEntitiesMap.containsKey(vec)) {
							Map<String, Tag> values = (Map<String, Tag>) tileEntitiesMap.get(vec);
							String entity = (String) values.get("EntityId").getValue();
							c.setCreatureTypeByName(entity);
						}
						c.update(true);
					}
				}
			}
		}
		return 1;
	}

	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws Exception {

		if (!items.containsKey(key)) {
			throw new Exception("Schematic file is missing a \"" + key + "\" tag");
		}
		Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new Exception(key + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ItemStack[] getContent(int size, Map tileEntitiesMap, BlockVector vec) {
		ItemStack[] items = new ItemStack[size];

		if (tileEntitiesMap.containsKey(vec)) {
			Map<String, Tag> values = (Map<String, Tag>) tileEntitiesMap.get(vec);
			for (Tag itemTag : ((ListTag) values.get("Items")).getValue()) {
				ItemStack itemStack = new ItemStack(1);
				ItemMeta itemMeta = null;
				int slot = 0;
				Map<Enchantment, Integer> itemEnchantments = new HashMap<Enchantment, Integer>();

				if (itemTag instanceof CompoundTag) {
					CompoundTag t = (CompoundTag) itemTag;
					Map<String, Tag> itemContent = t.getValue();
					if (itemContent.containsKey("id")) {
						int id = (Short) itemContent.get("id").getValue();
						itemStack.setTypeId(id);
						itemMeta = itemStack.getItemMeta();
					}
					if (itemContent.containsKey("Damage")) {
						short damage = (Short) itemContent.get("Damage").getValue();
						itemStack.setDurability(damage);
					}
					if (itemContent.containsKey("Count")) {
						int amount = (Byte) itemContent.get("Count").getValue();
						itemStack.setAmount(amount);
					}
					if (itemContent.containsKey("Slot"))
						slot = (Byte) itemContent.get("Slot").getValue();
					if (itemContent.containsKey("tag")) {
						Tag tE = itemContent.get("tag");
						Map<String, Tag> mE = (Map<String, Tag>) tE.getValue();

						// Start of enchantment for item
						if (mE.containsKey("ench")) {
							for (Tag enchs : ((ListTag) mE.get("ench")).getValue()) {
								CompoundTag ctEnchItem = (CompoundTag) enchs;
								Map<String, Tag> mapEnchs = ctEnchItem.getValue();

								int ench_id = (Short) mapEnchs.get("id").getValue();
								int ench_lvl = (Short) mapEnchs.get("lvl").getValue();
								itemEnchantments.put(Enchantment.getById(ench_id), ench_lvl);
							}
						}
						// End of enchantment for item

						// Start of stored enchantments
						if (mE.containsKey("StoredEnchantments")) {
							Map<Enchantment, Integer> storedEnchantments = new HashMap<Enchantment, Integer>();
							ListTag list = (ListTag) mE.get("StoredEnchantments");
							for (Tag tag : list.getValue()) {
								CompoundTag ct = (CompoundTag) tag;
								Map<String, Tag> mapEnchs = ct.getValue();
								int ench_id = (Short) mapEnchs.get("id").getValue();
								int ench_lvl = (Short) mapEnchs.get("lvl").getValue();
								storedEnchantments.put(Enchantment.getById(ench_id), ench_lvl);
							}
							EnchantmentStorageMeta m = (EnchantmentStorageMeta) itemMeta;
							for (Enchantment ench : storedEnchantments.keySet()) {
								m.addEnchant(ench, storedEnchantments.get(ench), true);
							}
							itemMeta = m;
						}
						// End of stored enchantments

						/*if (mE.containsKey("RepairCost")) { // Required or not required?
							repairCost = (Integer) mE.get("RepairCost").getValue();
						}*/

						// Start of display name of item
						if (mE.containsKey("display")) {
							Map<String, Tag> list = (Map<String, Tag>) mE.get("display").getValue();
							if (list.containsKey("Name")) {
								String itemName = (String) list.get("Name").getValue();
								itemMeta.setDisplayName(itemName);
							}
						}
						// End of display name of item

						// Start of BookMeta
						if (itemStack.getType() == Material.WRITTEN_BOOK) {
							BookMeta m = (BookMeta) itemMeta;
							if (mE.containsKey("author")) {
								String author = (String) mE.get("author").getValue();
								m.setAuthor(author);
								itemMeta = m;
							}
							if (mE.containsKey("title")) {
								String title = (String) mE.get("title").getValue();
								m.setTitle(title);
								itemMeta = m;
							}
							if (mE.containsKey("pages")) {
								ListTag list = (ListTag) mE.get("pages");
								ArrayList<String> pages = new ArrayList<String>();
								for (Tag tag : list.getValue()) {
									String page = (String) tag.getValue();
									pages.add(page);
								}
								m.setPages(pages);
								itemMeta = m;
							}
						}
						// End of BookMeta

						// Start of FireworkMeta
						if (itemStack.getType() == Material.FIREWORK) {
							if (mE.containsKey("Fireworks")) {
								FireworkMeta m = (FireworkMeta) itemMeta;

								Map<String, Tag> list = (Map<String, Tag>) mE.get("Fireworks").getValue();
								byte flight = (Byte) list.get("Flight").getValue();
								m.setPower(flight);

								if (list.containsKey("Explosions")) {
									ListTag ltExplosions = (ListTag) list.get("Explosions");
									for (Tag tExplosion : ltExplosions.getValue()) {
										Map<String, Tag> listEffect = (Map<String, Tag>) tExplosion.getValue();
										FireworkEffect fe = getFireworkEffect(listEffect);
										m.addEffect(fe);
									}
								}
								itemMeta = m;
							}
						}
						// End of FireworkMeta

						// Start of FireworkEffectMeta
						if (itemStack.getType() == Material.FIREWORK_CHARGE) {
							if (mE.containsKey("Explosion")) {
								Map<String, Tag> list = (Map<String, Tag>) mE.get("Explosion").getValue();
								FireworkEffect fb = getFireworkEffect(list);
								FireworkEffectMeta m = (FireworkEffectMeta) itemMeta;
								m.setEffect(fb);

								itemMeta = m;
							}
						}
						// End of FireworkEffectMeta

						// Start of LeatherArmorMeta
						ArrayList<Material> leather = new ArrayList<Material>();
						leather.add(Material.LEATHER_BOOTS);
						leather.add(Material.LEATHER_CHESTPLATE);
						leather.add(Material.LEATHER_HELMET);
						leather.add(Material.LEATHER_LEGGINGS);
						if (leather.contains(itemStack.getType())) {
							Map<String, Tag> list = (Map<String, Tag>) mE.get("display").getValue();
							LeatherArmorMeta m = (LeatherArmorMeta) itemMeta;
							if (list.containsKey("color")) {
								int color = (Integer) list.get("color").getValue();
								m.setColor(Color.fromRGB(color));
								itemMeta = m;
							}
						}
						// End of LeatherArmorMeta
					}
				}
				itemStack.setItemMeta(itemMeta);
				itemStack.addEnchantments(itemEnchantments); // If that is done before setting the itemMeta, the enchantments will be removed...
				items[slot] = itemStack;
			}
		}

		return items;
	}

	public static FireworkEffect getFireworkEffect(Map<String, Tag> list) {
		FireworkEffect.Builder fb = FireworkEffect.builder();

		int[] fadeColors = new int[0], colors = new int[0];

		if (list.containsKey("Type")) {
			int type = -1;
			type = (Byte) list.get("Type").getValue();
			if (type != -1) {
				FireworkEffect.Type feType = null;
				for (FireworkEffect.Type fet : FireworkEffect.Type.values()) {
					if (fet.ordinal() == type)
						feType = fet;
				}
				if (feType != null)
					fb.with(feType);
			}
		}
		if (list.containsKey("FadeColors")) {
			fadeColors = (int[]) list.get("FadeColors").getValue();
			for (int i = 0; i < fadeColors.length; i++) {
				fb.withFade(Color.fromRGB(fadeColors[i]));
			}
		}
		if (list.containsKey("Colors")) {
			colors = (int[]) list.get("Colors").getValue();
			for (int i = 0; i < colors.length; i++) {
				fb.withColor(Color.fromRGB(colors[i]));
			}
		}
		if (list.containsKey("Trail")) {
			int i = (Byte) list.get("Trail").getValue();
			if (i == 1)
				fb.withTrail();
		}
		if (list.containsKey("Flicker")) {
			int i = (Byte) list.get("Flicker").getValue();
			if (i == 1)
				fb.withFlicker();
		}

		return fb.build();
	}
}