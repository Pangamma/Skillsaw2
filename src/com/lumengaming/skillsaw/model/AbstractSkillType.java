package com.lumengaming.skillsaw.model;

import java.util.Objects;

/**
 *
 * @author Taylor
 */
public abstract class AbstractSkillType {
    protected int minLevel = 0;
    protected int maxLevel = 10;
	protected int defLevel = 0;	// default level
    protected final String key;
	protected int minInstructLevel;
    
    /**
     * @param p_typeKey Will be used for data storage. Converted to lowercase.
	 * @param p_defaultLevel Default skill level for new players.
     * @param p_minLevel Minimum allowable skill level.
     * @param p_maxLevel Maximum allowable skill level.
     */
    public AbstractSkillType(String p_typeKey, int p_defaultLevel, int p_minLevel, int p_maxLevel){
		if (!p_typeKey.matches("([a-zA-Z_])+")){
			throw new IllegalArgumentException("SkillType key is only allowed to contain letters and underscores.");
		}
		
        this.key = p_typeKey.toLowerCase();
		this.defLevel = p_defaultLevel;
        this.minLevel = p_minLevel;
        this.maxLevel = p_maxLevel;
    }
	
	
	/** The title shown on hovering over the title in chat + the title in chat. **/
    public abstract Title getTitle(int p_skillLevel);
    
    public boolean isValidLevel(int p_skillLevel){
        return minLevel <= p_skillLevel && p_skillLevel <= maxLevel;
    }
	
	/** Use this key for database entries and config files. **/
	public String getKey(){
		return this.key;
	}

	/** Compares the key **/
	public boolean equals(Object o){
		if (o != null){
			if (o instanceof AbstractSkillType){
				AbstractSkillType a = (AbstractSkillType) o;
				if (o.hashCode() == this.hashCode()){
					if (this.key.equals(a.key)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 43 * hash + Objects.hashCode(this.key);
		return hash;
	}

	/** Minimum level this can be. **/
	public int getMinLevel(){
		return minLevel;
	}

	/** Set the minimum skill level for this type. **/
	public void setMinLevel(int minLevel){
		this.minLevel = minLevel;
	}

	/** Get max skill lvl for this type. **/
	public int getMaxLevel(){
		return maxLevel;
	}

	/** Set max level for this type. **/
	public void setMaxLevel(int maxLevel){
		this.maxLevel = maxLevel;
	}

	/** Get default level for this type. **/
	public int getDefLevel(){
		return defLevel;
	}

	/** Set the default level for this type. **/
	public void setDefLevel(int defLevel){
		this.defLevel = defLevel;
	}

	/** get minimum level required to instruct in this level. 
	 A user must have the instruct permission node + this thing to be able to 
	 instruct in a given category. Otherwise, they can just get an override 
	 permission node. **/
	public int getMinInstructLevel(){
		return minInstructLevel;
	}

	public void setMinInstructLevel(int minInstructLevel){
		this.minInstructLevel = minInstructLevel;
	}
	
}
