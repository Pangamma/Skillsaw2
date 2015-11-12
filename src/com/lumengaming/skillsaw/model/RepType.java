package com.lumengaming.skillsaw.model;

/**
 * To be used within the database.
 * @author Taylor Love (Pangamma)
 */
public enum RepType
{
	Invalid(0),
	NaturalRep(1),
	StaffRep(2),
	XRep(3),
	Note(4)
	;
	
	private int integerValue;
	
	RepType(int p_typeVal){
		this.integerValue = p_typeVal;
	}
	
	/**
	 * The int value that should be used for the database.
	 * @return 
	 */
	public int toInt(){
		return this.integerValue;
	}
	/** Null if not found. **/
	public static RepType fromInt(int i){
		for(RepType rt : RepType.values()){
			if (rt.toInt() == i){
				return rt;
			}
		}
		return null;
	}
}
