package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.CsWrapper;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.Title;
import com.lumengaming.skillsaw.model.User;
import java.util.ArrayList;
import java.util.Comparator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffCommand implements CommandExecutor {

	private final Main plugin;

	public StaffCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender csndr, Command cmnd, String cmdAlias, String[] args){
		CsWrapper cs = new CsWrapper(csndr);
		try{
			if (args[0].equals("+") || args[0].equalsIgnoreCase("add")){
				if (!STATIC.USER_HAS_PERMISSION(csndr, STATIC.PERMISSION.STAFF_MODIFY)){
					return true;
				}
				this.plugin.getDataService().getOfflineUser(STATIC.getFullNameIfOnlinePlayer(args[1]), true, (User u) -> {
					if (u != null){
						if (!u.isStaff()){
							u.setIsStaff(true);
							this.plugin.getDataService().saveUser(u);
							cs.sendMessage("§a" + u.getName() + " has been added to the staff list.");
							if (u.p() != null && u.p().isValid()){
								u.p().sendMessage("§aYou've been added to the staff list.");
							}
						}
						else{
							cs.sendMessage("§c" + u.getName() + " is already a staff member.");
						}
					}
					else{
						cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
					}
				});
			}
			else if (args[0].equals("-") || args[0].equalsIgnoreCase("del")){
				if (!STATIC.USER_HAS_PERMISSION(csndr, STATIC.PERMISSION.STAFF_MODIFY)){
					return true;
				}
				this.plugin.getDataService().getOfflineUser(STATIC.getFullNameIfOnlinePlayer(args[1]), true, (User u) -> {
					if (u != null){
						if (u.isStaff()){
							u.setIsStaff(false);
							this.plugin.getDataService().saveUser(u);
							cs.sendMessage("§a" + u.getName() + " has been removed from the staff list.");
							if (u.p() != null && u.p().isValid()){
								u.p().sendMessage("§cYou've been removed from the staff list.");
							}
						}
						else{
							cs.sendMessage("§c" + u.getName() + " is not a staff member.");
						}
					}
					else{
						cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
					}
				});
			}
			else if (args[0].equalsIgnoreCase("l") || args[0].equalsIgnoreCase("list")){
				if (!STATIC.USER_HAS_PERMISSION(csndr, STATIC.PERMISSION.STAFF_LIST)){
					return true;
				}
				this.plugin.getDataService().getOfflineStaff((ArrayList<User> us) -> {
					us.sort(new Comparator<User>() {
						@Override
						public int compare(User o1, User o2){
							return (int) (o1.getLastPlayed() - o2.getLastPlayed());
						}
					});
					cs.sendMessage(STATIC.C_DIV_LINE);
					cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX + "Staff List");
					cs.sendMessage(STATIC.C_DIV_LINE);
					boolean altColor = false;
					for (User u : us){
						String hoverText = "";
						ArrayList<Title> ts = u.getAllTitles();
						for (int i = 0; i < ts.size(); i++){
							Title t = ts.get(i);
							if (i > 0){
								hoverText += "\n";
							}
							hoverText += t.getLongTitle();
						}

						altColor = !altColor;
						if (altColor){
							cs.sendMessage(CText.hoverText(STATIC.C_MENU_CONTENT
									+ STATIC.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
									+ " - " + u.getName(), hoverText));
						}
						else{
							cs.sendMessage(CText.hoverText(STATIC.C_MENU_CONTENT2
									+ STATIC.getTimePartsString(System.currentTimeMillis() - u.getLastPlayed())
									+ " - " + u.getName(), hoverText));
						}
					}
					cs.sendMessage(STATIC.C_DIV_LINE);
				});
			}
			else{
				printHelp(cs);
			}
		}
		catch (ArrayIndexOutOfBoundsException ex){
			printHelp(cs);
		}
		return true;
	}

	private void printHelp(CsWrapper cs){
		cs.sendMessage("§c/staff +/add <username>");
		cs.sendMessage("§c/staff -/del <username>");
		cs.sendMessage("§c/staff list");
	}

}
