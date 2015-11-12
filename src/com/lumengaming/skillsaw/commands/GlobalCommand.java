package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class GlobalCommand implements CommandExecutor{

	private final Main plugin;

	public GlobalCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_GLOBAL)){
			return false;
		}
		try{
			String msg = String.join(" ", args);
			if (cs instanceof Player){
				Player p = (Player) cs;
				User u = plugin.getDataService().getUser(p.getUniqueId());
				if (u == null){
					cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
					return true;
				}
				Bukkit.broadcastMessage("§f[§eg§f]["+u.getDisplayName()+"§f]:§7 "+u.getChatColor()+msg);
			}else{
				Bukkit.broadcastMessage("§f[§eg§f]["+cs.getName()+"§f]:§7 "+msg);
			}
			
		}catch(ArrayIndexOutOfBoundsException ex){
			cs.sendMessage("§c/g <message>");
		}
		return true;
	}

}