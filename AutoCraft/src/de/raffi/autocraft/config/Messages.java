package de.raffi.autocraft.config;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	
	@Config("messageprefix")
	public static  String PREFIX = "§eAutoCraft §8|";
	
	@Config("message.nopermission")
	public static  String NO_PERMISSION = "§cNo permission";
	
	@Config("message.page")
	public static  String PAGE = "§7Page§e";
	
	@Config("block.place")
	public static  String BLOCK_PLACED = "§aPlaced %block%";
	@Config("block.remove")
	public static  String BLOCK_REMOVED = "§cRemoved %block%";
	@Config("block.hopperconnected")
	public static  String BLOCK_HOPPER_CONNECTED = "§aConnected hopper";
	@Config("block.hopperdisconnected")
	public static  String BLOCK_HOPPER_DISCONNECTED = "§cDisconnected hopper";
	
	@Config("item.recipes.name")
	public static  String ITEM_RECIPES_NAME = "§aRecipes";
	@Config("item.recipes.lore")
	public static  String ITEM_RECIPES_LORE = "§7Select crafting recipe";
	@Config("item.interninventory.name")
	public static  String ITEM_INTERNINV_NAME = "§cIntern inventory";
	@Config("item.interninventory.lore")
	public static  String ITEM_INTERNINV_LORE = "§7Show inventory";
	@Config("item.craftinginventory.name")
	public static  String ITEM_OVERFLOW_NAME = "§cOverflow inventory";
	@Config("item.craftinginventory.lore")
	public static  String ITEM_OVERFLOW_LORE = "§7Show inventory for crafted items";
	@Config("item.autocrafter.lore")
	public static  String ITEM_AUTOCRAFTER_LORE = "§7Automatic §ecrafting §7block";
	@Config("item.autoenchanter.lore")
	public static  String ITEM_AUTOENCHANTER_LORE = "§7Automatic §5enchanting §7block";
	@Config("item.oreblock.lore")
	public static  String ITEM_OREBLOCK_LORE = "§7Converts cobblestone into §ddiamonds";
	@Config("item.blockcrusher.lore")
	public static  String ITEM_BOCKCRUSHER_LORE = "§7Uncraft items";
	
	@Config("inventorytitles.autocrafter.recipe")
	public static  String INVENTORY_TITLE_RECIPES = "§dRecipes";
	@Config("inventorytitles.autocrafter.menue")
	public static  String INVENTORY_TITLE_SELECTOPTION = "§dSelect";
	
	@Config("setting.block.blockconverter.probability")
	public static final int DIAMOND_PROBABILITY = 5;
	
	
	
	private static File configFile = new File("plugins/AutoCraft/messages.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	
	public static void loadMessages()  {
		try {
			for(Field f : Messages.class.getFields()) {
				if(!f.isAnnotationPresent(Config.class)) continue;
				f.setAccessible(true);
				Config annotation = f.getAnnotation(Config.class);
				if(config.isSet(annotation.value())) {
					if(f.getType().equals(String.class))
						f.set(null, config.getString(annotation.value()).replace("&", "§"));
					else if(f.getType().equals(Integer.class))
						f.set(null, config.getInt(annotation.value()));
				} else
					config.set(annotation.value(), String.valueOf(f.get(null)).replace("§", "&"));
			}
			config.save(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
	}

}
