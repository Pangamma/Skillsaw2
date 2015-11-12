package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.STATIC.PERMISSION;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


/**
 * @author Taylor Love (Pangamma)
 */
public class SkillSawCommand  implements CommandExecutor{
	private final Main plugin;
	public SkillSawCommand(Main aThis) {
		this.plugin = aThis;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
		
		if (args.length != 1){
			plugin.printHelp(cs);
		}else{
			if (args[0].equalsIgnoreCase("reload") && STATIC.USER_HAS_PERMISSION(cs, PERMISSION.ALL)){
				plugin.onDisable();
				plugin.onEnable();
				cs.sendMessage("loading configs.");
				return true;
			}else if (args[0].equalsIgnoreCase("perms")){
				cs.sendMessage(STATIC.C_DIV_LINE);
				cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX+"Permissions");
				cs.sendMessage(STATIC.C_DIV_LINE);
				for (PERMISSION s: STATIC.PERMISSION.values()){
					cs.sendMessage(STATIC.C_MENU_CONTENT+s.node);
				}
				cs.sendMessage(STATIC.C_DIV_LINE);
				return true;
			}else{
				plugin.printHelp(cs);
			}
		}
		return true;
	}

}
