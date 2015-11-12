package com.lumengaming.skillsaw;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** This class makes it so you can send enhanced messages to the cmd sender 
 * without worrying about whether the command sender is a Player or not.
 * This class automatically sends a correct message depending on what
 * the command sender is.
 * @author Taylor
 */
public class CsWrapper {
	private final Player p;
	private final CommandSender cs;
	public CsWrapper(CommandSender cs){
		this.cs = cs;
		if (cs instanceof Player){
			this.p = (Player) cs;
		}else{
			this.p = null;
		}
	}
	public void sendMessage(String s){
		this.cs.sendMessage(s);
	}
	public void sendMessage(BaseComponent[] msg){
		if (p != null){
			p.spigot().sendMessage(msg);
		}else{
			String s = "";
			for(BaseComponent bc : msg){
				s  += bc.toLegacyText();
			}
			cs.sendMessage(s);
		}
	}

	public CommandSender getCs(){
		return this.cs;
	}
}
