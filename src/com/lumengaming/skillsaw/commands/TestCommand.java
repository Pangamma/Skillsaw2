package com.lumengaming.skillsaw.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lumengaming.skillsaw.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class TestCommand implements CommandExecutor{

	private final Main plugin;

	public TestCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
//        ByteArrayDataOutput out = ByteStreams.newDataOutput();
//  out.writeUTF("Subchannel");
//  out.writeUTF("Argument");
//  plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
//  
//		cs.sendMessage("Getting ready to lock up the main thread.");
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {cs.sendMessage("5");}, 20);
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {cs.sendMessage("4");}, 40);
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {cs.sendMessage("3");}, 60);
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {cs.sendMessage("2");}, 80);
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {cs.sendMessage("1");}, 100);
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {
//			try{
//				Bukkit.broadcastMessage("Locked!");
//				Thread.sleep(10000);
//				Bukkit.broadcastMessage("Unlocked.");
//			}
//			catch (InterruptedException ex){
//				Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}, 120);
		return true;
	}

}