package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerListener implements Listener{

	private final Main plugin;

	public PlayerListener(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		plugin.getDataService().loginUser(p);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		plugin.getDataService().logoutUser(p);
	}
	
}
