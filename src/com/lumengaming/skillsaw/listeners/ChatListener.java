package com.lumengaming.skillsaw.listeners;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class ChatListener implements Listener {

	private final Main plugin;

	public ChatListener(Main plug){
		this.plugin = plug;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMee(final PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage();
		Player p = e.getPlayer();
		if (cmd.toLowerCase().startsWith("/me ")){
			String msg = cmd.replaceFirst("me ", "mee ");
			e.setMessage(msg);
		}
		else if (cmd.toLowerCase().startsWith("/ch:")){
			String msg = cmd.replaceFirst("/ch:", "");
			String[] args = msg.split(" ");
			if (args.length >= 2){
				String ch = args[0].toLowerCase();
				msg = msg.replaceFirst(ch, "").trim();
				this.sendMessageToChannelAndFormatIt(p, msg, ch);
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAnyCommand(final PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage();
		Player p = e.getPlayer();
		if (e.getMessage().startsWith("/g ")){
			plugin.getDataService().logChat(p.getUniqueId(), p.getDisplayName(),"#global",cmd);
		}else{
			plugin.getDataService().logCommand(p.getUniqueId(), cmd);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)	// Let other plugins go first.
	public void onChat(final AsyncPlayerChatEvent e){
		if (e.isCancelled()){
			return;
		}
		
		//<editor-fold defaultstate="collapsed" desc="start">
		if (e.getMessage().startsWith("/")){
			return;
		}
		sendMessageToChannelAndFormatIt(e.getPlayer(), e.getMessage());
		e.setCancelled(true);
		//</editor-fold>
		
	}
	
	private void sendMessageToChannelAndFormatIt(Player p, String rawMessage){
		User u = plugin.getDataService().getUser(p.getUniqueId());
		if (u == null){
			p.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_CHAT);
			return;
		}
		sendMessageToChannelAndFormatIt(p, rawMessage,u.getSpeakingChannel());
	}
	
	private void sendMessageToChannelAndFormatIt(final Player p, final String rawMessage,final String p_channel){
		final String fChannel = p_channel.toLowerCase();
		Bukkit.getScheduler().runTask(plugin, 
				() -> {
					ChatListener.this.doNamePingIfNamed(fChannel, p.getName(), rawMessage);
				}
		)
				;
		User u = plugin.getDataService().getUser(p.getUniqueId());
		if (u == null){
			p.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_CHAT);
			return;
		}
		//</editor-fold>
		
		plugin.getDataService().logChat(u.getUuid(), u.getDisplayName(), u.getSpeakingChannel(), rawMessage);
		//<editor-fold defaultstate="collapsed" desc="format">
		BaseComponent[] channel = new CText("[").color(ChatColor.WHITE)
				.append(fChannel).color(ChatColor.AQUA)
				.append("]").color(ChatColor.WHITE)
				.create();
		
		CText.applyEvent(channel,new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ch:"+fChannel+" "));
		CText.applyEvent(channel,new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("§bChat Channel")));
		
		BaseComponent[] rep = new CText("[").color(ChatColor.WHITE).create();
		rep = CText.merge(rep, CText.legacy("§c"+u.getRepLevel()));
		rep = CText.merge(rep, CText.legacy("§f]"));
		CText.applyEvent(rep,new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("§cReputation level")));
		CText.applyEvent(rep,new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rep "+u.getName()+" "));
		
		BaseComponent[] title = new CText("[").color(ChatColor.WHITE).create();
		title = CText.merge(title, CText.legacy(u.getTitle().getShortTitle()));
		title = CText.merge(title, CText.legacy("§f] "));
		CText.applyEvent(title,new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(u.getTitle().getLongTitle())));
		CText.applyEvent(title,new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/title "+u.getName()+" list"));

		BaseComponent[] name = CText.merge(u.getNameForChat(),CText.legacy("§f : "));
		
		String chatColor = u.getChatColor();
		if (chatColor == null || chatColor.isEmpty()){
			chatColor = ChatColor.GRAY.toString();
		}else{
			chatColor = chatColor.replace('&', '§');
		}
		boolean isColorOkay = STATIC.USER_HAS_PERMISSION(p, STATIC.PERMISSION.CHAT_COLOR_BASIC, false);
		boolean isFormatOkay = STATIC.USER_HAS_PERMISSION(p, STATIC.PERMISSION.CHAT_COLOR_FORMATTNG, false);
		boolean isBlackOkay = STATIC.USER_HAS_PERMISSION(p, STATIC.PERMISSION.CHAT_COLOR_BLACK, false);
        String msg = rawMessage.replace('&', '§');
        
        for(org.bukkit.ChatColor cc : org.bukkit.ChatColor.values()){
			if (cc.isColor() && !isColorOkay){
				msg = msg.replace(cc.toString(), cc.toString().toUpperCase().replace('§','&'));
				msg = msg.replace(cc.toString(), cc.toString().toLowerCase().replace('§','&'));
			}else if (cc.isFormat() && !isFormatOkay){
				msg = msg.replace(cc.toString(), cc.toString().toUpperCase().replace('§','&'));
				msg = msg.replace(cc.toString(), cc.toString().toLowerCase().replace('§','&'));
			}
		}
        if (!isBlackOkay){
            msg = msg.replace("§0", "");
        }
        
		BaseComponent[] message = CText.legacy(chatColor+msg);
		BaseComponent[] output = CText.merge(channel, rep);
		output = CText.merge(output,title);
		output = CText.merge(output,name);
		output = CText.merge(output,message);
		if (u.isStaff() || u.isInstructor()){
			String strGroups = "§2Special §2Group(s):";
			if (u.isStaff()){strGroups += "\n"+"§aStaff";}
			if (u.isInstructor()){ strGroups += "\n"+"§aInstructor";}
			BaseComponent[] specialGroups = null;
            if (u.isStaff() && u.isInstructor()){
                specialGroups = CText.hoverText("✪", strGroups);
            }else if (u.isStaff()){
                specialGroups = CText.hoverText("§a✪", strGroups);
            }else if (u.isInstructor()){
                specialGroups = CText.hoverText("§2✪", strGroups);
            }
			output = CText.merge(specialGroups, output);
		}
		
		plugin.getDataService().sendMessageToChannel(p.getName(), p_channel, output);
	}
	
	private void doNamePingIfNamed(String ch,String senderName, String rawMessage){
		rawMessage = ChatColor.stripColor(rawMessage.replace('&','§'));
		ArrayList<User> onlineUsersReadOnly = plugin.getDataService().getOnlineUsersReadOnly();
		for(User u : onlineUsersReadOnly){
			if (u.isListeningOnChannel(ch)){
				if (!u.isIgnoringPlayer(senderName)){
					if (u.p() != null && u.p().isValid()){
						if (rawMessage.contains(u.getName()) || rawMessage.contains(ChatColor.stripColor(u.getDisplayName()))){
							u.p().playSound(u.p().getLocation(), Sound.NOTE_PLING,1f, 1f);
						}
					}
				}
			}
		}
	}
}
