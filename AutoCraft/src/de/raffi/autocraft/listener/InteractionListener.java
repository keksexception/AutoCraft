package de.raffi.autocraft.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.GameMode;
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

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import de.raffi.autocraft.blocks.BasicBlock;
import de.raffi.autocraft.blocks.BlockAutoCrafter;
import de.raffi.autocraft.blocks.BlockAutoEnchanter;
import de.raffi.autocraft.blocks.BlockCrusher;
import de.raffi.autocraft.blocks.BlockOreAnalysizer;
import de.raffi.autocraft.blocks.ConnectableBlock;
import de.raffi.autocraft.blocks.Interactable;
import de.raffi.autocraft.blocks.QueueableConnectedBlock;
import de.raffi.autocraft.callback.CallBackSign;
import de.raffi.autocraft.config.Messages;
import de.raffi.autocraft.main.AutoCraft;
import de.raffi.autocraft.recipes.Recipe;
import de.raffi.autocraft.recipes.RecipeRegistry;
import de.raffi.autocraft.utils.BlockManager;
import de.raffi.autocraft.utils.InventoryTitles;
import de.raffi.autocraft.utils.PlayerInteractionStorage;
import de.raffi.autocraft.utils.SignGui;

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
			
			ConnectableBlock connect =  (ConnectableBlock) block;
			connect.addConnected(e.getBlock());
			e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_HOPPER_CONNECTED);
			break;
		case WORKBENCH:
			if (e.getItemInHand().getItemMeta().getDisplayName() == null) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equals(AutoCraft.getAutoCraft().getAutoCrafter().getItemMeta().getDisplayName()))return;
			BlockAutoCrafter crafter = new BlockAutoCrafter(Material.WORKBENCH, 0, e.getBlockPlaced().getLocation(),
					null, RecipeRegistry.getRecipes().get(0));
			BlockManager.registerBlock(crafter);
			e.getPlayer().sendMessage(Messages.PREFIX + " " + Messages.BLOCK_PLACED.replace("%block%", "AutoCrafter"));
			break;
		case ENCHANTMENT_TABLE:
			if (e.getItemInHand().getItemMeta().getDisplayName() == null) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equals(AutoCraft.getAutoCraft().getAutoEnchanter().getItemMeta().getDisplayName()))return;
			BlockAutoEnchanter enchanter = new BlockAutoEnchanter(e.getBlockPlaced().getLocation(),null);
			BlockManager.registerBlock(enchanter);
			e.getPlayer().sendMessage(Messages.PREFIX + " " + Messages.BLOCK_PLACED.replace("%block%", "AutoEnchanter"));
			break;
		case DIAMOND_ORE:
			if (e.getItemInHand().getItemMeta().getDisplayName() == null) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equals(AutoCraft.getAutoCraft().getOreBlock().getItemMeta().getDisplayName()))return;
			BlockOreAnalysizer ore = new BlockOreAnalysizer(Material.DIAMOND_ORE, 0, e.getBlockPlaced().getLocation());
			BlockManager.registerBlock(ore);
			e.getPlayer().sendMessage(Messages.PREFIX + " " + Messages.BLOCK_PLACED.replace("%block%", "CobbleConverter"));
			break;
		case NOTE_BLOCK:
			if (e.getItemInHand().getItemMeta().getDisplayName() == null) return;
			if (!e.getItemInHand().getItemMeta().getDisplayName().equals(AutoCraft.getAutoCraft().getBlockCrusher().getItemMeta().getDisplayName()))return;
			BlockCrusher crusher = new BlockCrusher(Material.ANVIL, 0, e.getBlockPlaced().getLocation(), null);
			BlockManager.registerBlock(crusher);
			e.getPlayer().sendMessage(Messages.PREFIX + " " + Messages.BLOCK_PLACED.replace("%block%", "Blockcrusher"));
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
			e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_REMOVED.replace("%block%", "AutoCrafter"));
			
			if(e.getPlayer().getGameMode()==GameMode.CREATIVE) return;
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), AutoCraft.getAutoCraft().getAutoCrafter());
			break;
		case ENCHANTMENT_TABLE:
			BasicBlock enchant = BlockManager.getBlockAt(e.getBlock());
			
			if(enchant == null) return;
			
			BlockManager.unregisterBlock(enchant);
			e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_REMOVED.replace("%block%", "AutoEnchanter"));
			
			if(e.getPlayer().getGameMode()==GameMode.CREATIVE) return;
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), AutoCraft.getAutoCraft().getAutoEnchanter());
			break;
		case DIAMOND_ORE:
			BasicBlock ore = BlockManager.getBlockAt(e.getBlock());
			
			if(ore == null) return;
			
			BlockManager.unregisterBlock(ore);
			e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_REMOVED.replace("%block%", "CobbleConverter"));
			
			if(e.getPlayer().getGameMode()==GameMode.CREATIVE) return;
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), AutoCraft.getAutoCraft().getOreBlock());
			break;
		case NOTE_BLOCK:
			BasicBlock crusher  = BlockManager.getBlockAt(e.getBlock());
			
			if(crusher == null) return;
			
			BlockManager.unregisterBlock(crusher);
			e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_REMOVED.replace("%block%", "Blockcrusher"));
			
			if(e.getPlayer().getGameMode()==GameMode.CREATIVE) return;
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), AutoCraft.getAutoCraft().getBlockCrusher());
			break;
		case HOPPER:
			for(BasicBlock basic : BlockManager.getBlocks()) {
				if(!(basic instanceof ConnectableBlock)) continue;
				ConnectableBlock connectableBlock = (ConnectableBlock) basic;
				for(Block connected : connectableBlock.getConnected()) {
					if(connected.equals(e.getBlock())) {
						connectableBlock.removeConnected(e.getBlock());
						e.getPlayer().sendMessage(Messages.PREFIX+" " +Messages.BLOCK_HOPPER_DISCONNECTED);
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
	public void onInventoryClick(InventoryClickEvent e) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InvocationTargetException {
		if(e.getInventory()==null) return;
		if(e.getClickedInventory()==null) return;
		if(e.getView().getTitle().equals(Messages.INVENTORY_TITLE_SELECTOPTION)) {
			e.setCancelled(true);
			switch (e.getCurrentItem().getType()) {
			case PAPER:
				Player p = (Player) e.getWhoClicked();						
				p.openInventory(InventoryTitles.getRecipes(p,InventoryTitles.getFilter(p),InventoryTitles.getPage(p)));
				break;
			case CHEST:
				e.getWhoClicked().openInventory(PlayerInteractionStorage.getCurrentBlock((Player) e.getWhoClicked()).getInventory());
				break;
			case DIAMOND:
				e.getWhoClicked().openInventory(((QueueableConnectedBlock) PlayerInteractionStorage.getCurrentBlock((Player) e.getWhoClicked())).getQueueInventory());
				break;
			default:
				break;
			}
		} else if(e.getView().getTitle().equals(Messages.INVENTORY_TITLE_RECIPES)) {
			Player p = (Player) e.getWhoClicked();	
			int page = InventoryTitles.getPage(p);
			e.setCancelled(true);
			if(e.getCurrentItem()==null) return;
			if(e.getCurrentItem().getType()==Material.AIR) return;
			
			if(e.getCurrentItem().getItemMeta().getDisplayName()!=null) {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e<<")) {
					p.openInventory(InventoryTitles.getRecipes(p,InventoryTitles.getFilter(p),page-1));
					InventoryTitles.setPage(p, page-1);
					return;
				} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e>>")) {
					p.openInventory(InventoryTitles.getRecipes(p,InventoryTitles.getFilter(p),page+1));
					InventoryTitles.setPage(p, page+1);
					return;
				} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§dSearch")) {
					p.closeInventory();
					SignGui.registerRequest(p, new CallBackSign() {
						
						@Override
						public void packetReceived(WrappedChatComponent[] component) {
							InventoryTitles.setPage(p, 0);
							String request = component[0].getJson().replace("\"", "");
							List<Recipe> filter = RecipeRegistry.filter(request);
							InventoryTitles.setFilter(p, filter);
							p.openInventory(InventoryTitles.getRecipes(p, filter, 0));
						}
					});
					return;
				} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§dReset search")) {
					InventoryTitles.setPage(p, 0);
					InventoryTitles.setFilter(p, null);
					p.openInventory(InventoryTitles.getRecipes(p, InventoryTitles.getFilter(p), page));
					return;
				}
			}
			
			if(e.getSlot()>9*3) return;
			
			int clickedSlot = e.getSlot();
			int get = clickedSlot+page*9*3;
			((BlockAutoCrafter) PlayerInteractionStorage.getCurrentBlock(p)).setTarget(InventoryTitles.getFilter(p).get(get));
			p.openInventory(InventoryTitles.getRecipes(p, InventoryTitles.getFilter(p), page));
		} 
		
	}
	
	
	}
