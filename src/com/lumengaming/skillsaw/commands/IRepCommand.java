/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.util.ArrayList;
import java.util.Calendar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Taylor
 */
public abstract class IRepCommand implements CommandExecutor{
	protected final Main plugin;
	protected final DataService dh;

	public IRepCommand(Main plugin){
		this.plugin = plugin;
		this.dh = plugin.getDataService();
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args){
		try{
			if (args.length > 0){
				String name = args[0];
				String fullName = STATIC.getFullNameOfPlayer(name);
				final String fName = (fullName != null) ? fullName : name;

				dh.getOfflineUser(fName, true, (User target) -> {
					if (target == null){
						cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
						return;
					}
					
					if (args.length == 1){
						// View Info?
						target.showStatisticsTo(cs);
					}else if (args.length >= 3){
						
						if (!(cs instanceof Player)){
							cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
							return;
						}
						
						Player p = (Player) cs;
						if (fName.equalsIgnoreCase(cs.getName()) && !cs.getName().equalsIgnoreCase("Pangamma")){
							cs.sendMessage("§cYou cannot rep yourself!");
							return;
						}



						// Already have our target.
						double amount = Double.parseDouble(args[1]);

						String reason = "";
						for (int i = 2; i < args.length; i++){
							reason += args[i];
							if (i < args.length-1){
								reason += " ";
							}
						}

						// Cap amount of rep to send.
						if (reason.length() < 10){
							cs.sendMessage(STATIC.C_ERROR+"Leave a better reason than that. The reasons are saved so we can look at them in the future.");
							return;
						}
						doRep(cs, target, amount, reason);
					}else{
						printHelp(cs);
					}
				});

			}else{
				printHelp(cs);
			}
		}catch(NumberFormatException | ArrayIndexOutOfBoundsException ex){
			printHelp(cs);
		}
		return true;
	}

	protected abstract void printHelp(CommandSender cs);
	/**
	 * Assumes CS, Target, Amount, and Reason are all VALID. 
	 * @param cs
	 * @param target
	 * @param amount
	 * @param reason 
	
		// Okay try to rep then.
			// Permissions?
		// Check number times repped within time area.
		// Check sender is valid.
		// Cap amount of rep to send.
		// Send the rep.
		// Log the rep.
		// Update the user in the DB.
	 */
	protected abstract boolean doRep(final CommandSender cs, final User target,double amount, final String reason);

}
