package com.lumengaming.skillsaw.model;

import java.util.ArrayList;
import java.util.Objects;
import org.bukkit.ChatColor;

public class Title {
	protected final String longTitle;
	protected final String shortTitle;
	
	public Title(String p_shortTitle){
		this(p_shortTitle, p_shortTitle);
	}
	
	public Title(String p_shortTitle,String p_longTitle){
		this.shortTitle= p_shortTitle.replace('&', '§');
		this.longTitle = p_longTitle.replace('&', '§');
	}

	/** A title to be used on mouse hover events on the chat format. Automatically
	 * replaces & to color chars even though it isn't needed.
	 * @return  **/
	public String getLongTitle(){
		return longTitle.replace('&', '§');
	}

	/** The title to use for chat.Automatically replaces & to color chars even
	 * though it isn't needed.
	 * @return  **/
	public String getShortTitle(){
		return shortTitle.replace('&', '§');
	}
	
	@Override
	/** Replaces § with &. Returns shortTitle\tlongTitle. Use this for serializing. **/
	public String toString(){
		return this.shortTitle.replace('§', '&')+"\t"+this.longTitle.replace('§', '&');
	}	
	
	/** Accepts in the format of "shorttitle\tlongtitle", and I guess also "shorttitle".
	 * Returns null if a shitty format was passed in. 
	 * @param p_titleStr
	 * @return 
	 */
	public static Title fromString(String p_titleStr){
		String[] parts = p_titleStr.split("\t");
		if (parts.length == 2){
			return new Title(parts[0].replace('&', '§'),parts[1].replace('&', '§'));
		}else if (parts.length == 1){
			return new Title(parts[0].replace('&', '§'),parts[0].replace('&', '§'));
		}else{
			return null;
		}
	}
	
	public boolean equals(Object o){
		if( o != null && o instanceof Title){
			Title other = (Title) o;
			return other.toString().equals(this.toString());
		}
		return false;
	}

	@Override
	public int hashCode(){
		int hash = 5;
		hash = 61 * hash + Objects.hashCode(this.longTitle);
		hash = 61 * hash + Objects.hashCode(this.shortTitle);
		return hash;
	}
	
	//<editor-fold defaultstate="collapsed" desc="Utility methods">
	/**
	 * Returns null if no matches are found. Within needle, replaces & to be §. *
	 */
	public static Title getMatchedTitle(String needle,ArrayList<Title> haystack){
		String convertedNeedle = needle.replace('&', '§');
		Title output = null;
		int bestMatchLevel = 0;
		for (Title t : haystack){
			int lvl = t.getTitleMatchLevel(convertedNeedle);
			if (lvl > bestMatchLevel){
				bestMatchLevel = lvl;
				output = t;
			}
		}

		return output;
	}	
	
	/** Returns exact match if found, otherwise searches for matches using the 
	 * longTitle string within the needle. Best match returned. If nothing found,
	 * returns null. 
	 * @param haystack
	 * @param tNeedle
	 * @return 
	 */
	public static Title getMatchedTitle(Title tNeedle,ArrayList<Title> haystack){
		String needle = tNeedle.longTitle;
		String convertedNeedle = needle.replace('&', '§');
		Title output = null;
		int bestMatchLevel = 0;
		for (Title t : haystack){
			if (tNeedle.toString().equals(t.toString())){
				int lvl = t.toString().length();
				if (lvl > bestMatchLevel){
					bestMatchLevel = lvl;
					output = t;
					return output;
				}
			}else{
				int lvl = t.getTitleMatchLevel(convertedNeedle);
				if (lvl > bestMatchLevel){
					bestMatchLevel = lvl;
					output = t;
				}
			}
		}

		return output;
	}
	
	/**
	 * The higher the number, the better the match. Expects the needle to use §
	 * instead of &. Strips colors off all inputs. If neither the short title
	 * nor the long title start with the needle, 0 is returned. If short title
	 * matches (with color codes), then the length of the needle + color codes
	 * will be returned. Same with the long title. In the event that color codes
	 * do not match, the length of the stripped needle will be returned.
	 * @param needle
	 */
	public int getTitleMatchLevel(String needle){
		Title hayStrand = this;
		String sNeedle = ChatColor.stripColor(needle).toLowerCase();
		String sShort = ChatColor.stripColor(hayStrand.getShortTitle()).toLowerCase();
		String sLong = ChatColor.stripColor(hayStrand.getLongTitle()).toLowerCase();
		if (sShort.startsWith(sNeedle) || sLong.startsWith(sNeedle)){
			if (hayStrand.getShortTitle().startsWith(needle)){
				return needle.length();
			}
			else if (hayStrand.getLongTitle().startsWith(needle)){
				return needle.length();
			}
			else{
				return sNeedle.length();
			}
		}
		else{
			return 0;
		}
	}
	
	//</editor-fold>
}
