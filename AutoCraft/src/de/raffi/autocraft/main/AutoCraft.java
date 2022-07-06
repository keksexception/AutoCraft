package de.raffi.autocraft.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.raffi.autocraft.listener.HopperHandler;
import de.raffi.autocraft.listener.InteractionListener;
import de.raffi.autocraft.recipes.RecipeRegistry;
import de.raffi.autocraft.utils.BlockManager;
import de.raffi.autocraft.utils.ItemBuilder;

public class AutoCraft extends JavaPlugin {
	
	private static AutoCraft autoCraft;
	
	@Override
	public void onEnable() {
		autoCraft = this;
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractionListener(), this);
		pm.registerEvents(new HopperHandler(), this);
		
		/*
		 * add custom recipe
		 */
		ShapedRecipe r = new ShapedRecipe(new ItemBuilder(Material.WORKBENCH).setName("§r§eAutoCrafter§r§r").build());
		r.shape("AAA","ABA","AAA");
		r.setIngredient('A', Material.WORKBENCH);
		r.setIngredient('B', Material.REDSTONE_BLOCK);
		Bukkit.getServer().addRecipe(r);
		
		/*
		 * init
		 */
		BlockManager.init();
		RecipeRegistry.init();
		BlockManager.readBlocks();
		
		
	}
	
	public static AutoCraft getAutoCraft() {
		return autoCraft;
	}
	@Override
	public void onDisable() {
		BlockManager.saveBlocks();
		BlockManager.getBlocks().forEach(b->b.remove(false));
	}

}
