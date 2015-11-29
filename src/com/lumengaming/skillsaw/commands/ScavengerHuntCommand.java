package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.model.ScavengerHuntLogEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class ScavengerHuntCommand implements CommandExecutor{

	private final Main plugin;

	public ScavengerHuntCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
		try{
			Location loc = null;
			if (cs instanceof BlockCommandSender){
				BlockCommandSender csp = (BlockCommandSender) cs;
				loc = csp.getBlock().getLocation();
			}else if(cs instanceof Player && cs.isOp()){
				Player csp = (Player) cs;
				loc = csp.getLocation();
			}else{
				cs.sendMessage("§cOnly players and commandblocks can use this command.");
				return true;
			}
			
			Player target = Bukkit.getPlayerExact(args[0]);
			
			ScavengerHuntLogEntry e = new ScavengerHuntLogEntry(target.getName(),target.getUniqueId(),cs.getName(),args[1],args[2],loc);
			this.plugin.getDataService().logScavengerHuntEntry(e);
			cs.sendMessage("Logged the entry.");
			
		}catch(ArrayIndexOutOfBoundsException ex){
			cs.sendMessage("§c/elfhunt @p <GROUP_KEY> <PART_KEY> ");
		}
		return true;
	}

}