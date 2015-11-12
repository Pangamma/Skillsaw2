package com.lumengaming.skillsaw.model;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class SkillType extends AbstractSkillType {

	/**
	 * Replace this string with the level of the skill passed into the getTitle
	 * method. 
	 * %lvl%
	 */
	public static final String LEVEL_VAR_STR = "%lvl%";
	private final String shortTitleFormat;
	private final String longTitleFormat;
	private final int minInstructLevel;
	private final String listName;

	public SkillType(String p_typeKey, String p_listName, int p_defaultLevel, int p_minLevel, int p_maxLevel,int p_minInstructLevel, String p_shortTitleFormat, String p_longTitleFormat){
		super(p_typeKey, p_defaultLevel, p_minLevel, p_maxLevel);
		this.shortTitleFormat = p_shortTitleFormat;
		this.longTitleFormat = p_longTitleFormat;
		this.minInstructLevel = p_minInstructLevel;
		this.listName = p_listName;
	}

	/**
	 * Returns a title with § characters, and with %level% replaced by the input
	 * level.
	 *
	 * @param p_skillLevel
	 * @return
	 */
	@Override
	public Title getTitle(int p_skillLevel){
		Title t = new Title(
			this.shortTitleFormat.replace(SkillType.LEVEL_VAR_STR, "" + p_skillLevel),
			this.longTitleFormat.replace(SkillType.LEVEL_VAR_STR, "" + p_skillLevel)
		);
		return t;
	}

	/** Minimum level required to allow someone to instruct in this category.
	 * Also requires the instructor permission. Can be overidden with an
	 * override permission node.
	 * @return 
	 */
	public int getMinInstructLevel(){
		return this.minInstructLevel;
	}

	/** The name to use for command menus and stuff. **/
	public String getListName(){
		return this.listName;
	}
	
}
