package com.lumengaming.skillsaw.model;

import com.lumengaming.skillsaw.STATIC;
import java.util.Objects;


public class MutedPlayer implements Comparable<MutedPlayer>{
	private final String name;
	private final boolean isSoftMute;
	private final long expireAt;
	private final long secondsToLive;
	
	/** Hard mute that lasts 5 minutes **/
	public MutedPlayer(String name){
		this(name,false,300);
	}
	
	/** expires in 5 minutes **/
	public MutedPlayer(String name,boolean isSoftMute){
		this(name,isSoftMute, 300);
	}
	public MutedPlayer(String name,boolean isSoftMute, long  secondsToLive){
		this.name = name.toLowerCase();
		this.isSoftMute = isSoftMute;
		this.expireAt = secondsToLive == -1 ? -1 : secondsToLive*1000 + System.currentTimeMillis();
		this.secondsToLive = secondsToLive;
	}
	
	/** D:H:M:S **/
	public String getTotalMuteTimeStr(){
		if (this.secondsToLive == -1){ return "infnite";}
		String s = "";
		long[] timeParts = STATIC.getTimeParts(secondsToLive*1000);
		s += timeParts[0]+"d ";
		s += timeParts[1]+"h ";
		s += timeParts[2]+"m ";
		s += timeParts[3]+"s ";
		return s;
	}
	
	/** D:H:M:S **/
	public String getTimeRemainingStr(){
		if (this.secondsToLive == -1){ return "infnite";}
		String s = "";
		long ms = expireAt - System.currentTimeMillis();
		if (ms < 0){ ms = 0;}
		long[] timeParts = STATIC.getTimeParts(ms);
		s += timeParts[0]+"d ";
		s += timeParts[1]+"h ";
		s += timeParts[2]+"m ";
		s += timeParts[3]+"s ";
		return s;
	}
	
	public boolean isExpired(){
		return expireAt != -1 && expireAt < System.currentTimeMillis();
	}
	
	public boolean isSoftMute(){
		return this.isSoftMute;
	}
	
	/** lowercase version **/
	public String getMutedPlayerName(){
		return this.name;
	}
	
	@Override
	public boolean equals(Object o){
		if (o != null){
			if (o instanceof MutedPlayer){
				MutedPlayer mp = (MutedPlayer) o;
				if (this.name != null){
					return this.name.equalsIgnoreCase(mp.name);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode(){
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public int compareTo(MutedPlayer o){
		return this.name.compareTo(o.name);
	}
}
