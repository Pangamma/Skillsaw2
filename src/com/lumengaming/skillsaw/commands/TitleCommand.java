package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.Title;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Taylor Love (Pangamma)
 */
public class TitleCommand implements CommandExecutor {

	private final Main plugin;
	private final DataService ds;

	public TitleCommand(Main aThis){
		this.plugin = aThis;
		this.ds = plugin.getDataService();
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args){
		if (!(cs instanceof Player)){
			cs.sendMessage("Yeah, I am not going to deal with this. Log in and do this command from the game.");
		}

		try{
            boolean canSetSelf = STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_SELF,false);
            boolean canSetOthers = STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_OTHERS,false);
			
			//<editor-fold defaultstate="collapsed" desc="Init some stuff">
			final boolean isTargetSelf;
			final String targetName;
			final String action;
			final Player p;
			final Player.Spigot sp;

			if (cs instanceof Player){
				p = (Player) cs;
				sp = p.spigot();
			}
			else{
				p = null;
				sp = null;
			}
			//</editor-fold>
			
			//<editor-fold defaultstate="collapsed" desc="Input args">
			if (isValidAction(args[0])){
				isTargetSelf = true;
				targetName = cs.getName();
				action = args[0].toLowerCase();
				if (!(cs instanceof Player)){
					cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
					return false;
				}
			}
			// user title
			else if (args.length == 1){
				isTargetSelf = true;
				targetName = cs.getName();
				action = args[0].toLowerCase();
			}
			else
			{
				isTargetSelf = false;
				targetName = STATIC.getFullNameIfOnlinePlayer(args[0]);
				action = args[1].toLowerCase();
			}
			//</editor-fold>

			plugin.getDataService().getOfflineUser(targetName, true, (User usr) -> {

				if (usr == null){
					cs.sendMessage("§cCould not find the user requested: '" + targetName + "'");
					return;
				}

				if (action.equals("l") || action.equals("list")){
					if (sp != null){
						cs.sendMessage(STATIC.C_DIV_LINE);
						cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX + "Titles for "+usr.getName());
						cs.sendMessage(STATIC.C_DIV_LINE);
						for (Title title : usr.getAllTitles()){
							BaseComponent[] txt = CText.legacy(STATIC.C_MENU_CONTENT);
							BaseComponent[] sTitle = CText.legacy(title.getLongTitle());
							CText.applyEvent(sTitle, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(title.getShortTitle())));
							CText.applyEvent(sTitle, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/title " + title.getLongTitle().replace("§", "&")));
							txt = CText.merge(txt, sTitle);
							sp.sendMessage(txt);
						}
						cs.sendMessage(STATIC.C_DIV_LINE);
					}
					else{
						cs.sendMessage(STATIC.C_DIV_LINE_NC);
						cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX_NC + "Titles");
						cs.sendMessage(STATIC.C_DIV_LINE_NC);
						for (Title title : usr.getAllTitles()){
							cs.sendMessage(STATIC.C_MENU_CONTENT_NC + title.getLongTitle());
						}
						cs.sendMessage(STATIC.C_DIV_LINE_NC);
					}
				}
				else if (action.equals("add")){
                    if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_EDIT_ANY)){
                        return;
                    }
                    
					if (isTargetSelf){
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_SELF)){
                            return;
                        }
						String sTitle = args[1].replace('&','§');
						String lTitle = args[2].replace('&','§');
						Title nTitle = new Title(sTitle,lTitle);
						if (!usr.hasExactTitle(nTitle)){
							if (ChatColor.stripColor(sTitle).length() > 6){
								cs.sendMessage("§cYour short title must not excede 6 visible characters.");
								return;
							}
							usr.addTitle(new Title(sTitle,lTitle));
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully added the '"+lTitle+"'§a title.");
						}else{
							cs.sendMessage("§cYou already have the '"+lTitle+"'§c title.");
						}
					}else{
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_OTHERS)){
                            return;
                        }
						String sTitle = args[2];
						String lTitle = args[3];
						Title nTitle = new Title(sTitle,lTitle);
						if (!usr.hasExactTitle(nTitle)){
							usr.addTitle(new Title(sTitle,lTitle));
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully added the '"+lTitle+"'§a title.");
						}else{
							cs.sendMessage("§cThat player already have the '"+lTitle+"'§c title.");
						}
					}
				}
				else if (action.equals("remove")){
                    if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_EDIT_ANY)){
                        return;
                    }
					if (isTargetSelf){
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_SELF)){
                            return;
                        }
						String needle = args[1].replace("&", "§");
						Title title = Title.getMatchedTitle(needle, usr.getAllTitles());
						if (title != null){
							usr.removeTitle(title);
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully removed the '"+title.getLongTitle()+"'§a title.");
						}else{
							cs.sendMessage("§cYou do not have the '"+needle+"' title.");
						}
					}else{
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_OTHERS)){
                            return;
                        }
						String needle = args[2].replace("&", "§");
						Title title = Title.getMatchedTitle(needle, usr.getAllTitles());
						if (title != null){
							usr.removeTitle(title);
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully removed the '"+title.getLongTitle()+"'§a title.");
						}else{
							cs.sendMessage("§cThat person does not have the '"+needle+"' title.");
						}
					}
				} else {
					if (isTargetSelf){
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_SELF)){
                            return;
                        }
						ArrayList<Title> allTitles = usr.getAllTitles();
						String needle = args[0].replace("&", "§");
						Title title = Title.getMatchedTitle(needle, usr.getAllTitles());
						if (title != null){
							usr.setTitle(title);
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully set your title to " + title.getLongTitle() + "§a.");
						}
						else{
							cs.sendMessage("§cCould not find title : " + needle + "§c.");
						}
					}else{
                        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.TITLE_SET_SELF)){
                            return;
                        }
						ArrayList<Title> allTitles = usr.getAllTitles();
						String needle = args[1].replace("&", "§");
						Title title = Title.getMatchedTitle(needle, usr.getAllTitles());
						if (title != null){
							usr.setTitle(title);
                            ds.saveUser(usr);
							cs.sendMessage("§aSuccessfully set that person's title to " + title.getLongTitle() + "§a.");
						}
						else{
							cs.sendMessage("§cCould not find title : " + needle + "§c.");
						}
					}
				}
			});
		}
		catch (ArrayIndexOutOfBoundsException ex){
			printHelp(cs);
		}
		return true;
	}

	private boolean isValidAction(String action){
		switch (action.toLowerCase()){
			case "list":
			case "l":
			case "remove":
			case "add":
				return true;
			default:
				return false;
		}
	}

	public void printHelp(CommandSender cs){
//		if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CUSTOM_TITLES.node)){
			cs.sendMessage("§c/title add <short title> <long title>");
			cs.sendMessage("§c/title remove <short/long title>");
			cs.sendMessage("§c/title username list");
			cs.sendMessage("§c/title username <short/long title>");
			cs.sendMessage("§c/title username add <short title> <long title>");
			cs.sendMessage("§c/title username remove <short/long title>");
//		}
		cs.sendMessage("§c/title list");
		cs.sendMessage("§c/title <title>");
	}

}
