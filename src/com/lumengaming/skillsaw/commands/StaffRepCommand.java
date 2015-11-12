/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.commands.IRepCommand;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Taylor
 */
public class StaffRepCommand extends IRepCommand{

	public StaffRepCommand(Main plugin){
		super(plugin);
	}

	@Override
	protected void printHelp(CommandSender cs){
		cs.sendMessage(STATIC.C_ERROR + "/srep <target>");
		cs.sendMessage(STATIC.C_ERROR + "/srep <target> <amount> <reason>");
	}

	@Override
	protected boolean doRep(CommandSender cs, final User target, final double amount, final String reason){
		// Permissions?
		if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_STAFF)){
			
			// Check sender is valid.
			final User issuer = dh.getUser(cs.getName());
			if (issuer == null){
				cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
				return false;
			}
			
			// Cap amount of rep to send.
			if (Math.abs(amount) > 10 ){
				cs.sendMessage(STATIC.C_ERROR+"Remember that staff rep is weighted differently than natural rep. Do not give over powered amounts of rep! (+- 1 is recommended.)");
				return false;
			}
			
			target.addStaffRep(amount);
            dh.saveUser(target);
            
			if (target.p() != null){
				String format = plugin.getConfigHandler().getSRepFormatToTarget();
				format = plugin.getConfigHandler().compileMessageFormat(format,cs.getName(),  target.getName(), amount, reason);
				target.p().sendMessage(format);
			}
			
			String format = plugin.getConfigHandler().getSRepFormatToIssuer();
			format = plugin.getConfigHandler().compileMessageFormat(format, cs.getName(),  target.getName(), amount, reason);
			cs.sendMessage(format);
			
			dh.logRep(issuer,target,amount, RepType.StaffRep, reason);
			
		}else{
			cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.REP_STAFF));
			return false;
		}
		return true;
	
	}
	
}
