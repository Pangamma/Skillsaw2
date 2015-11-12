package com.lumengaming.skillsaw.model;

import java.util.HashMap;


public class MasterSkillTitle extends Title{
	private final HashMap<SkillType, Integer> map;
	
	/** Pass in the skill map from the User. This is passed by reference, so
	 * any changes to the skill map will immediately show up in the master skill
	 * title map. 
	 * @param refSkillMap 
	 */
	public MasterSkillTitle(HashMap<SkillType,Integer> refSkillMap){
		super("§e*"+SkillType.LEVEL_VAR_STR, "§eCreatorT"+SkillType.LEVEL_VAR_STR);
		this.map = refSkillMap;
	}
	
	/** A title to be used on mouse hover events on the chat format. Automatically **/
	public String getLongTitle(){
		int combinedLevel = 0;
		for(int lvl : map.values()){
			combinedLevel += lvl;
		}
		return longTitle.replace(SkillType.LEVEL_VAR_STR, ""+combinedLevel);
	}

	/** The title to use for chat. **/
	public String getShortTitle(){
		int combinedLevel = 0;
		for(int lvl : map.values()){
			combinedLevel += lvl;
		}
		return shortTitle.replace(SkillType.LEVEL_VAR_STR, ""+combinedLevel);
	}
	
}
