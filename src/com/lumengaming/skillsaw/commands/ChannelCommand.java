package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class ChannelCommand implements CommandExecutor {

	private final Main plugin;

	public ChannelCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
		String alias = cmnd.getName();
		try{

			// Otherwise see if they used the in-command thing with arg[0]
			switch (args[0].toLowerCase()){
				case "+":
					runChannelPlus(cs, stripArg(args));
					return true;
				case "-":
					runChannelMinus(cs, stripArg(args));
					return true;
				case "-i":
					runChannelInfo(cs, stripArg(args));
					return true;
				case "-p":
					runChannelPlayer(cs, stripArg(args));
					return true;
				case "list":
				case "l":
				case "-l":
					runChannelList(cs, stripArg(args));
					return true;
				case "=":
					runChannelSet(cs, stripArg(args));
					return true;
				default:
					runChannelSet(cs, args);
					return true;
			}
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException ex){
			printHelp(cs);
		}

		return true;
	}

	/**
	 * Just as it sounds. Returns array with one fewer argument. Missing the
	 * first arg. *
	 */
	private String[] stripArg(String[] origArray){
		String[] nArray = new String[origArray.length - 1];
		if (origArray.length > 0){
			for (int i = 1; i < origArray.length; i++){
				nArray[i - 1] = origArray[i];
			}
		}
		return nArray;
	}

	private void printHelp(CommandSender cs){
		cs.sendMessage(("§c/ch"));
		cs.sendMessage(("§c/ch:<channel> <message>"));
		cs.sendMessage(("§c/ch + <channel>"));
		cs.sendMessage(("§c/ch - <channel>"));
		cs.sendMessage(("§c/ch = <channel>"));
		cs.sendMessage(("§c/ch -i [channel]"));
		cs.sendMessage(("§c/ch -p [player]"));
		cs.sendMessage(("§c/ch -L"));
		cs.sendMessage(("§c/ch <channel>"));
	}

	private void runChannelInfo(CommandSender cs, String[] args){

		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_INFO)){
			return;
		}

		DataService system = plugin.getDataService();
		String ch = args.length > 0 ? args[0] : null;
		if (cs instanceof Player){
			Player p = (Player) cs;
			if (ch == null){
				User cp = system.getUser(p.getUniqueId());
				if (cp != null){
					ch = cp.getSpeakingChannel();
				}
			}
			if (ch == null){
				cs.sendMessage(STATIC.ERROR_REPORT_THIS_TO_PANGAMMA(1));
				return;
			}
			p.sendMessage(STATIC.C_DIV_LINE);
			p.sendMessage(STATIC.C_DIV_TITLE_PREFIX + "Channl Info");
			p.sendMessage(STATIC.C_DIV_LINE);
			ArrayList<User> cps = system.getOnlineUsersReadOnly();
			int numListening = 0;
			int numSpeaking = 0;
			Player.Spigot sp = p.spigot();

			for (User cp : cps){
				if (cp.isSpeakingOnChannel(ch)){
					numSpeaking++;
					BaseComponent[] txt = CText.legacy(STATIC.C_MENU_CONTENT + cp.getName());
					sp.sendMessage(txt);
				}
				if (cp.isListeningOnChannel(ch)){
					numListening++;
				}
			}

			p.sendMessage(STATIC.C_DIV_LINE);
			p.sendMessage(STATIC.C_MENU_CONTENT + "# Listening: " + numListening);
			p.sendMessage(STATIC.C_MENU_CONTENT + "# Speaking: " + numSpeaking);
			p.sendMessage(STATIC.C_DIV_LINE);

		}
		else{

			if (ch == null){
				cs.sendMessage("Could not determine the channel you want info for.");
				return;
			}

			cs.sendMessage(STATIC.C_DIV_LINE_NC);
			cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX_NC + "Channl Info");
			cs.sendMessage(STATIC.C_DIV_LINE_NC);
			Collection<User> cps = system.getOnlineUsersReadOnly();
			int numListening = 0;
			int numSpeaking = 0;

			for (User cp : cps){
				if (cp.isSpeakingOnChannel(ch)){
					numSpeaking++;
					cs.sendMessage(STATIC.C_MENU_CONTENT_NC + cp.getName());
				}
				if (cp.isListeningOnChannel(ch)){
					numListening++;
				}
			}
			cs.sendMessage(STATIC.C_DIV_LINE_NC);
			cs.sendMessage(STATIC.C_MENU_CONTENT_NC + "# Listening: " + numListening);
			cs.sendMessage(STATIC.C_MENU_CONTENT_NC + "# Speaking: " + numSpeaking);
			cs.sendMessage(STATIC.C_DIV_LINE_NC);
		}
	}

	private void runChannelPlayer(CommandSender cs, String[] args){

		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_INFO)){
			return;
		}

		DataService system = plugin.getDataService();

		String pName = args.length > 0 ? args[0] : cs.getName();

		// print info for that one player
		Player p = STATIC.getPlayer(pName);

		if (p != null){
			User cp = system.getUser(p.getUniqueId());
			if (cp == null){
				cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
				return;
			}
			cs.sendMessage(STATIC.C_DIV_LINE);
			cs.sendMessage(STATIC.C_MENU_CONTENT + "Chat info for: §a" + p.getName() + ".");
			for (String ch : cp.getStickyChannels()){
				cs.sendMessage(STATIC.C_MENU_CONTENT + "(L): " + ch);
			}
			cs.sendMessage(STATIC.C_MENU_CONTENT + "§a(S): " + cp.getSpeakingChannel());
			cs.sendMessage(STATIC.C_DIV_LINE);
		}
		else{
			cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
		}

	}

	private void runChannelList(CommandSender cs, String[] args){
		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_LIST)){
			return;
		}

		ArrayList<User> users = plugin.getDataService().getOnlineUsersReadOnly();
		HashMap<String, Integer> speakers = new HashMap<>();
		for (User cp : users){
			String ch = cp.getSpeakingChannel();
			if (speakers.containsKey(ch)){
				Integer get = speakers.get(ch);
				speakers.put(ch, get + 1);
			}
			else{
				speakers.put(ch, 1);
			}
		}

		cs.sendMessage(STATIC.C_DIV_LINE);
		for (String ch : speakers.keySet()){
			if (!ch.startsWith("_")){
				cs.sendMessage("§2=§7 " + speakers.get(ch) + " people on channel : " + ch);
			}
			else if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_LIST_PRIVATE, false)){
				cs.sendMessage("§2=§c " + speakers.get(ch) + " people on channel : " + ch);
			}
		}
		cs.sendMessage(STATIC.C_DIV_LINE);

	}

	private void runChannelPlus(CommandSender cs, String[] args){

		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_STICKIES)){
			return;
		}

		if (cs instanceof Player){
			Player p = (Player) cs;
			User cp = plugin.getDataService().getUser(p.getUniqueId());
			if (cp != null){
				if (cp.getStickyChannels().size() < 10 || STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_STICKIES_INFINITE)){
					String ch = args[0].toLowerCase();
					if (!isValidChannelName(ch)){
						cs.sendMessage(("§cInvalid symbols in the channel name. Pick a different channel."));
						return;
					}
					cp.addStickyChannel(ch);
					cs.sendMessage("§aAdded new \"sticky\" channel §2" + ChatColor.stripColor(ch));
					plugin.getDataService().saveUser(cp);
				}
				else{
					p.sendMessage("§cMaximum number of sticky channels has been reached. You cannot add any more!");
				}
			}
			else{
				p.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
			}
		}
		else{
			cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
		}

	}

	private void runChannelMinus(CommandSender cs, String[] args){
		if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHANNEL_STICKIES)){
			return;
		}
		if (cs instanceof Player){
			Player p = (Player) cs;
			String ch = args[0].toLowerCase();
			User cp = plugin.getDataService().getUser(p.getUniqueId());
			if (cp != null){
				String toRemove = args[0].toLowerCase();
				if (cp.getStickyChannels().remove(toRemove)){
					cs.sendMessage("§aRemoved \"sticky\" channel §2" + ChatColor.stripColor(toRemove));
					plugin.getDataService().saveUser(cp);
				}
				else{
					cs.sendMessage("§cYou do not have the \"sticky\" channel §4" + ChatColor.stripColor(toRemove));
				}
			}
			else{
				p.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
			}
		}
		else{
			cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
		}
	}

	private void runChannelSet(CommandSender cs, String[] args){
		if (cs instanceof Player){
			Player p = (Player) cs;
			String ch = args[0].toLowerCase();

			if (!isValidChannelName(ch)){
				cs.sendMessage(("§cInvalid symbols in the channel name. Pick a different channel."));
				return;
			}

			User cp = plugin.getDataService().getUser(p.getUniqueId());

			if (cp != null){
				cp.setSpeakingChannel(ch);
				cp.sendMessage(("§aSet the chat channel to §2" + ch));
				plugin.getDataService().saveUser(cp);
			}
			else{
				cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
			}
		}
		else{
			cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
		}
	}

	/**
	 * Channel name must only contain chars between decimal value 32 and 127.
	 * (non inclusive). Channel name must also have at least one char.
	 *
	 * @param chName
	 * @return
	 */
	private boolean isValidChannelName(String chName){
		if (chName == null || chName.isEmpty()){
			return false;
		}

		for (int c : chName.toCharArray()){
			if (c < 33 || c > 126){
				return false;
			}
		}
		return true;
	}

}
