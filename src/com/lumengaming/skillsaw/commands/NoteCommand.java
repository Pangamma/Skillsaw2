package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class NoteCommand implements CommandExecutor{

	private final Main plugin;

	public NoteCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
		try{
			if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_NOTE)){
				return false;
			}
			if (!(cs instanceof Player)){
				cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
				return false;
			}
			Player p = (Player) cs;
			
			String targName = args[0];
			Player targPlayerObj = STATIC.getPlayer(targName);
			if (targPlayerObj != null){
				targName = targPlayerObj.getName();
			}
			final DataService ds = plugin.getDataService();
			
			final User issuer = ds.getUser(p.getUniqueId());
			
			if (issuer == null){
				cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
				return true;
			}
			
			String msg = "";
			for(int i = 1; i < args.length; i++){
				if (i != 1) msg += " ";
				msg += args[i];
			}
            
            if (msg.length() < 5){
                cs.sendMessage("§cYour note isn't long enough. Use more detailed.");
                return false;
            }

			final String fMessage = msg;
			ds.getOfflineUser(targName, true, (User target) -> {
				if (target == null){
					cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
				}else{
					ds.logRep(issuer, target, 0, RepType.Note, fMessage);
					cs.sendMessage("§aSuccessfully added a note for '"+target.getName()+"'.");
				}
				
			});
			
		}catch(ArrayIndexOutOfBoundsException ex){
			printHelp(cs);
		}
		return true;
	}

	private void printHelp(CommandSender cs){
		cs.sendMessage("§c/note <player> <message>");
	}

}