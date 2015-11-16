package com.lumengaming.skillsaw.service;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.model.MutedPlayer;
import java.util.TreeSet;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public final class MuteService {
	
	TreeSet<MutedPlayer> mutedChatPlayers = new TreeSet<>();	
	
	private final Main plugin;
	
	public MuteService(Main sc){
		this.plugin = sc;
	}
	
	/** Removes any original matching mp then adds a new one. **/
	public synchronized void addMutedPlayer(MutedPlayer mp){
		this.mutedChatPlayers.remove(mp);
		this.mutedChatPlayers.add(mp);
	}
	
	/** Removes any original matching mp. Returns the original or null. **/
	public synchronized  MutedPlayer removeMutedPlayer(MutedPlayer mp){
		MutedPlayer mpR = null;
		if (mutedChatPlayers.contains(mp)){
			for(MutedPlayer mpF : mutedChatPlayers){
				if (mp.equals(mpF)){ mpR = mpF; break; }
			}
		}
		if (mpR != null){
			this.mutedChatPlayers.remove(mpR);
		}
		return mpR;
	}
	
	/** Removes any expired mutes. **/
	public synchronized  void removeExpiredMutedPlayers(){
		TreeSet<MutedPlayer> toRemove = new TreeSet<>();
		for(MutedPlayer mp : this.mutedChatPlayers){
			if (mp.isExpired()){
				toRemove.add(mp);
			}
		}
		this.mutedChatPlayers.removeAll(toRemove);
	}
	
	
	/** Checks non case sensitive username **/
	public synchronized boolean isMuted(String username){
		return this.mutedChatPlayers.contains(new MutedPlayer(username));
	}
	
	/** returns null if not found. **/
	public synchronized MutedPlayer getMutedPlayer(String username){
		MutedPlayer mp = null;
		MutedPlayer needle = new MutedPlayer(username);
		for(MutedPlayer tmp : mutedChatPlayers){
			if (tmp.equals(needle)){
				mp = tmp; break;
			}
		}
		return mp;
	}
	
	/** Removes all mutes. **/
	public synchronized  void clearMutedPlayer(){
		this.mutedChatPlayers.clear();
	}
	
}
