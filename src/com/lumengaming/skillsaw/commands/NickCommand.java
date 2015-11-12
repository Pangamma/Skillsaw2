/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.STATIC.PERMISSION;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Taylor
 */
public class NickCommand implements CommandExecutor{
	private final Main plugin;
	private final DataService dh;
	public NickCommand(Main p_plugin){
		this.plugin = p_plugin;
		this.dh = plugin.getDataService();
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args){
		boolean canNickSelf = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_SELF,false);
		boolean canNickOthers = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_OTHERS,false);
		boolean canNickColors = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_STYLE_COLORS,false);
		boolean canNickBlack = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_STYLE_COLOR_BLACK,false);
		boolean canNickFormat = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_STYLE_FORMATTING,false);
		boolean canNickSpecialChars = STATIC.USER_HAS_PERMISSION(cs, PERMISSION.NICK_STYLE_SPECIAL_CHARS,false);
		
		
		User issuer = dh.getUser(cs.getName());
		if (issuer == null){
			cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
			return true;
		}
		
		int issuerLevel = issuer.getRepLevel();
		if (!canNickSelf && issuerLevel >= 4){ canNickSelf = true; }
			
			
		try{
			String nick = "";
			User target = null;
			if (args.length == 1){
				if (canNickSelf){
					nick = args[0];
					target = issuer;
				}else{
					cs.sendMessage("§cYour rep level must be at least level 4, or you need the "+PERMISSION.NICK_SELF+" permission node.");
					return false;
				}
			}else if (args.length == 2){
				nick = args[1];
				String fName = STATIC.getFullNameOfPlayer(args[0]);
				
				if (canNickOthers || (canNickSelf && fName != null && cs.getName().equalsIgnoreCase(fName))){
					if (fName != null){
						target = dh.getUser(fName);
					}
				}else{
					cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(PERMISSION.NICK_OTHERS));
					return false;
				}
			}else{
				printHelp(cs);
				return false;
			}
			
			if (target == null){
				cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
				return false;
			}
			
			
			nick = nick.replace("&", "§");
			nick = STATIC.removeColorCodes(nick,canNickFormat, canNickColors, canNickBlack);
			
			// Remove any weirdo characters.
			if (!canNickSpecialChars){
				nick = nick.replaceAll("[^a-zA-Z0-9_&§\\-]", "");
			}
			
			if (ChatColor.stripColor(nick).length() > 16){
				cs.sendMessage(STATIC.C_ERROR+"Nickname must not exceed 16 visible characters.");
				return false;
			}
			target.setDisplayName(nick);
			if (target.p() != null){
				target.p().setDisplayName(nick);
				if (!cs.getName().equalsIgnoreCase(target.p().getName())){
					cs.sendMessage("§aTarget's nickname has been set to "+nick+"§a.");
				}
				target.p().sendMessage("§aYour nickname has been set to "+nick+"§a.");
			}
			plugin.getDataService().saveUser(target);
		}catch(NumberFormatException | ArrayIndexOutOfBoundsException nfe){
			printHelp(cs);
		}
		return true;
	}

	private void printHelp(CommandSender cs){
		cs.sendMessage(STATIC.C_ERROR+"/nick [target name] <nickname>");
		cs.sendMessage(STATIC.C_ERROR+"You can give yourself a nickname. Example : My name is §fPangamma§c,"
				+ " but I want it to be §6RockLobster§c. I would type \"§e/nick &6RockLobster§c\".");
	}
	
}
