package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.CsWrapper;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.sql.Timestamp;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ViewLogCommand implements CommandExecutor {

	private final Main plugin;

	public ViewLogCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender csTemporary, Command cmnd, String cmdAlias, String[] args){
		CsWrapper cs = new CsWrapper(csTemporary);
		
		if (args.length == 0){
			args = new String[]{ csTemporary.getName()};
		}
		
		try{
			switch (cmdAlias.toLowerCase()){
				case "replog":
					getLogs(cs, RepType.NaturalRep, args[0]);
					break;
				case "sreplog":
					getLogs(cs, RepType.StaffRep, args[0]);
					break;
				case "xreplog":
					getLogs(cs, RepType.XRep, args[0]);
					break;
				case "notes":
					getLogs(cs, RepType.Note, args[0]);
					break;
				default:
					printHelp(csTemporary);
					break;
			}
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException nfe){
			printHelp(csTemporary);
		}

		return true;
	}

	private boolean getLogs(CsWrapper cs, RepType rt, String targetName){
		DataService ds = plugin.getDataService();
		// do they have permission?
		if (rt == RepType.NaturalRep && !STATIC.USER_HAS_PERMISSION(cs.getCs(), STATIC.PERMISSION.VIEWLOGS_NATURAL_REP)
				|| rt == RepType.Note && !STATIC.USER_HAS_PERMISSION(cs.getCs(), STATIC.PERMISSION.VIEWLOGS_NOTE)
				|| rt == RepType.StaffRep && !STATIC.USER_HAS_PERMISSION(cs.getCs(), STATIC.PERMISSION.VIEWLOGS_STAFF_REP)
				|| rt == RepType.XRep && !STATIC.USER_HAS_PERMISSION(cs.getCs(), STATIC.PERMISSION.VIEWLOGS_REP_FIX)){
			return false;
		}

		ds.getOfflineUser(targetName, true, (User target) -> {
			if (target == null){
				cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
				return;
			}
			ds.getLogEntriesByTarget(rt, target.getUuid(), 15, 0, (ArrayList<RepLogEntry> entries) -> {
				boolean colorA = false;
				cs.sendMessage(STATIC.C_DIV_LINE);
				cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX + rt.name() + " Log Entries");
				cs.sendMessage(STATIC.C_DIV_LINE);
				for (RepLogEntry e : entries){
					colorA = !colorA;
					String c1 = colorA ? "§7" : "§8";
					String c2 = colorA ? "§a" : "§2";
					String reason = e.getReason();
					String name = e.getIssuerName();
					Timestamp time = e.getTime();
					cs.sendMessage(CText.hoverText(c1 + "[" + c2 + e.getAmount() + c1 + "] " + c2 + name + c1 + " -> " + c2 + reason, e.getTime().toString()));
				}
				cs.sendMessage(STATIC.C_DIV_LINE);
			});
		});
		return true;
	}

	private void printHelp(CommandSender p_cs){
		CsWrapper cs = new CsWrapper(p_cs);
		BaseComponent[] txt = CText.hoverText("§c/replog [target]", "§cView " + RepType.NaturalRep.name() + "(s) given to a player.");
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/replog "));
		cs.sendMessage(txt);
		txt = CText.hoverText("§c/sreplog [target]", "§cView " + RepType.StaffRep.name() + "(s) given to a player.");
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/seplog "));
		cs.sendMessage(txt);
		txt = CText.hoverText("§c/xreplog [target]", "§cView " + RepType.XRep.name() + "(s) given to a player.");
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/xeplog "));
		cs.sendMessage(txt);
		txt = CText.hoverText("§c/notes [target]", "§cView " + RepType.Note.name() + "(s) given to a player.");
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/notes "));
		cs.sendMessage(txt);
	}

}
