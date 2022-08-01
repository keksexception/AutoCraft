package de.raffi.autocraft.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;

import de.raffi.autocraft.callback.CallBackSign;
import de.raffi.autocraft.main.AutoCraft;

public class SignGui {
	
	private static HashMap<Player, CallBackSign> requests = new HashMap<>();
	
	
	public static void registerLib() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(AutoCraft.getAutoCraft(), ListenerPriority.NORMAL, PacketType.Play.Client.UPDATE_SIGN) {
		    @Override
		    public void onPacketReceiving(PacketEvent event) {
		        Player player = event.getPlayer();
		        if(requests.get(player)==null) return;
		        	
				PacketContainer packet = event.getPacket();
				requests.get(player).packetReceived(packet.getChatComponentArrays().read(0));
				requests.remove(player);
		     
		        
		    }
		});
	}
	public static void registerRequest(Player p, CallBackSign callback) throws InvocationTargetException {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
		packet.getBlockPositionModifier().write(0, new BlockPosition(0, 0, 0));
		ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
		requests.put(p, callback);
	}

}
