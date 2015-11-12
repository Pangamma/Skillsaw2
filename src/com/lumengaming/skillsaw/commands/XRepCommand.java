package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.commands.IRepCommand;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.model.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class XRepCommand extends IRepCommand{

	public XRepCommand(Main plugin){
		super(plugin);
	}

	@Override
	protected void printHelp(CommandSender cs){
		cs.sendMessage(STATIC.C_ERROR+"/xrep <name>");
		cs.sendMessage(STATIC.C_ERROR+"/xrep <name> <amount> <reason>");
	}

	@Override
	protected boolean doRep(CommandSender cs, final User target, final double amount, final String reason){
		// Permissions?
		if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_FIX)){
			
			// Check sender is valid.
			final User issuer = dh.getUser(cs.getName());
			if (issuer == null){
				cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
				return false;
			}
			
			if (target.p() != null){
				String format = plugin.getConfigHandler().getXRepFormatToTarget();
				format = plugin.getConfigHandler().compileMessageFormat(format,cs.getName(),  target.getName(), amount, reason);
				target.p().sendMessage(format);
			}
			
			target.addNaturalRep(amount);
            dh.saveUser(target);
			String format = plugin.getConfigHandler().getXRepFormatToIssuer();
			format = plugin.getConfigHandler().compileMessageFormat(format, cs.getName(),  target.getName(), amount, reason);
			cs.sendMessage(format);
			dh.logRep(issuer,target,amount, RepType.XRep, reason);
			
		}else{
			cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.REP_FIX));
			return false;
		}
		return true;
	
	}
	
}
