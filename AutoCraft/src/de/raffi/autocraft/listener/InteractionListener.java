package de.raffi.autocraft.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.raffi.autocraft.blocks.BasicBlock;
import de.raffi.autocraft.blocks.BlockAutoCrafter;
import de.raffi.autocraft.blocks.ConnectableBlock;
import de.raffi.autocraft.blocks.Interactable;
import de.raffi.autocraft.recipes.Recipe;
import de.raffi.autocraft.recipes.RecipeRegistry;
import de.raffi.autocraft.utils.BlockManager;
import de.raffi.autocraft.utils.InventoryTitles;
import de.raffi.autocraft.utils.PlayerInteractionStorage;

public class InteractionListener implements Listener {
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(e.getPlayer().isSneaking()) return;
		BasicBlock block = BlockManager.getBlockAt(e.getClickedBlock());
		
		if(block == null) return;
		
		if(block instanceof Interactable) {
			((Interactable)block).onInteract(e.getPlayer());
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		
		switch (e.getBlock().getType()) {
		case HOPPER:
			BasicBlock block = BlockManager.getBlockAt(e.getBlockAgainst());
			
			
			if(!(block instanceof ConnectableBlock)) return;
			
			if(block instanceof BlockAutoCrafter) {
				BlockAutoCrafter autoCrafter = (BlockAutoCrafter) block;
				autoCrafter.addConnected(e.getBlock());
				e.getPlayer().sendMessage("§aHopper have been connected");
			}
			break;
		
		case WORKBENCH:
			if(e.getItemInHand().getItemMeta().getDisplayName()==null)return;
			if(!e.getItemInHand().getItemMeta().getDisplayName().equals("§r§eAutoCrafter§r§r")) return;
			BlockAutoCrafter crafter = new BlockAutoCrafter(Material.WORKBENCH, 0, e.getBlockPlaced().getLocation(), null, RecipeRegistry.getRecipes().get(0));
			BlockManager.registerBlock(crafter);
			e.getPlayer().sendMessage("§aAutoCrafter placed.");
			break;

		default:
			break;
		}

	
		
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		switch (e.getBlock().getType()) {
		case WORKBENCH:
			BasicBlock b = BlockManager.getBlockAt(e.getBlock());
			
			if(b == null) return;
			
			BlockManager.unregisterBlock(b);
			e.getPlayer().sendMessage("§cRemoved block");
			break;
			
		case HOPPER:
			for(BasicBlock basic : BlockManager.getBlocks()) {
				if(!(basic instanceof ConnectableBlock)) continue;
				ConnectableBlock connectableBlock = (ConnectableBlock) basic;
				for(Block connected : connectableBlock.getConnected()) {
					if(connected.equals(e.getBlock())) {
						connectableBlock.removeConnected(e.getBlock());
						e.getPlayer().sendMessage("§cHopper is no longer connected");
						break;
					}
				}
			}
			break;
		default:
			break;
		}

	
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory()==null) return;
		if(e.getClickedInventory()==null) return;
		
		switch (e.getClickedInventory().getTitle()) {
		case InventoryTitles.AUTOCRAFTER_MENUE:
			e.setCancelled(true);
			switch (e.getCurrentItem().getType()) {
			case PAPER:
				Player p = (Player) e.getWhoClicked();						
				p.openInventory(InventoryTitles.getRecipes(p,InventoryTitles.getPage(p)));
				break;
			case CHEST:
				e.getWhoClicked().openInventory(PlayerInteractionStorage.getCurrentBlock((Player) e.getWhoClicked()).getInventory());
				break;
			case DIAMOND:
				e.getWhoClicked().openInventory(((BlockAutoCrafter) PlayerInteractionStorage.getCurrentBlock((Player) e.getWhoClicked())).getQueueInventory());
				break;
			default:
				break;
			}
			break;
		case InventoryTitles.RECIPES:
			Player p = (Player) e.getWhoClicked();	
			int page = InventoryTitles.getPage(p);
			e.setCancelled(true);
			if(e.getCurrentItem()==null) return;
			if(e.getCurrentItem().getType()==Material.AIR) return;
			
			if(e.getCurrentItem().getItemMeta().getDisplayName()!=null) {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e<<")) {
					p.openInventory(InventoryTitles.getRecipes(p,page-1));
					InventoryTitles.setPage(p, page-1);
					return;
				} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e>>")) {
					p.openInventory(InventoryTitles.getRecipes(p,page+1));
					InventoryTitles.setPage(p, page+1);
					return;
				}
			}
		
			
			for(Recipe r : RecipeRegistry.getRecipes()) {
				if(r.getTarget().getType()==e.getCurrentItem().getType()&&r.getTarget().getDurability()==e.getCurrentItem().getDurability()) {
					((BlockAutoCrafter) PlayerInteractionStorage.getCurrentBlock(p)).setTarget(r);
					p.openInventory(InventoryTitles.getRecipes(p,page));
					break;
				}
			}
			
			
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onManipulate(PlayerArmorStandManipulateEvent e) {
		if(PlayerInteractionStorage.armorstands.contains(e.getRightClicked().getUniqueId())) e.setCancelled(true);
	}
	
	}
