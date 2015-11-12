package com.lumengaming.skillsaw;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

/**
 * @author Taylor Love (Pangamma)
 */
public class CText extends ComponentBuilder{

	public CText(String text){
		super(fixLegacy(text));
	}
    
    public CText(ComponentBuilder cb){
        super(cb);
    }

	/** This method is horrible, but it does the job. Maybe one day I can fix
	 * it. For now it just works. It fixes a glitch in the core game where 
	 * colored chat stops being colored after it spans more than one line.
	 * @param orig
	 * @return 
	 */
	private static String fixLegacy(String orig){
		String spacesBefore = ""; String spacesAfter = "";
		for(int i = 0; i < orig.length(); i++){
			char c = orig.charAt(i);
			if (c == ' ')
				spacesBefore += " ";
			else
				break;
		}
		
		for(int i = orig.length()-1; i >= 0; i--){
			char c = orig.charAt(i);
			if (c == ' ')
				spacesAfter += " ";
			else
				break;
			if (i == 0){
				spacesAfter = ""; // avoid duplicates. If the whole thing is spaces.
			}
		}
		
		String s = "";
		String[] args = orig.split(" ");
		String lastColors = "";
		for(int i = 0; i < args.length; i++){
			if (i != 0 && !ChatColor.stripColor(args[i]).isEmpty()){
				s += " ";
			}
			
			String nColor = ChatColor.getLastColors(args[i]);
			if (nColor != null && !nColor.isEmpty()){
				lastColors = nColor;
			}
			s += lastColors + args[i];
		}
		return spacesBefore + s + spacesAfter;
	}
	
    public static BaseComponent[] legacy(String orig){
        return TextComponent.fromLegacyText(fixLegacy(orig));
    }
	
	/** Merges two arrays of base components. **/
	public static BaseComponent[] merge(BaseComponent[]  o1,BaseComponent[] o2){
		BaseComponent[] n = new BaseComponent[o1.length+o2.length];
		int i = 0;
		
		for(BaseComponent bc : o1)
			n[i++] = bc;
		for(BaseComponent bc : o2)
			n[i++] = bc;
		
		return n;
	}
	
	/** Iterates through all the base components and sets an event on them. **/
	public static void applyEvent(BaseComponent[] bcs,HoverEvent e){
		for (BaseComponent bc : bcs){
			bc.setHoverEvent(e);
		}
	}
	/** Iterates through all the base components and sets an event on them. **/
	public static void applyEvent(BaseComponent[] bcs,ClickEvent e){
		for (BaseComponent bc : bcs){
			bc.setClickEvent(e);
		}
	}
	
	/**
	 * Creates hover text easily.
	 * @return 
	 */
	public static BaseComponent[] hoverText(String displayText,String hoverText){
		BaseComponent[] txt = CText.legacy(displayText);
		CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
		return txt;
	}
	
	/**
	 * Creates hover text easily. Click event suggests the command.
	 * @return 
	 */
	public static BaseComponent[] hoverTextSuggest(String displayText,String hoverText,String commandText){
		BaseComponent[] txt = CText.legacy(displayText);
		CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandText));
		return txt;
	}	
	
	/**
	 * Creates hover text easily. Click event runs the command.
	 * @return 
	 */
	public static BaseComponent[] hoverTextForce(String displayText,String hoverText,String commandText){
		BaseComponent[] txt = CText.legacy(displayText);
		CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy(hoverText)));
		CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandText));
		return txt;
	}
}
