package com.lumengaming.skillsaw.model;

import java.util.UUID;
import org.bukkit.Location;


public class ScavengerHuntLogEntry {
	private final String username;
	private final UUID uuid;
	private final String groupKey;
	private final String itemKey;
	private final String commandsenderName;
	private final String world;
	private final int x;
	private final int y;
	private final int z;

	public ScavengerHuntLogEntry(String username, UUID uuid, String cmdSenderName, String groupKey, String itemKey,Location csLoc){
		this.commandsenderName = cmdSenderName;
		this.username = username;
		this.uuid = uuid;
		this.groupKey = groupKey.toUpperCase();
		this.itemKey = itemKey.toUpperCase();
		this.world = csLoc.getWorld().getName();
		this.x = csLoc.getBlockX();
		this.y = csLoc.getBlockY();
		this.z = csLoc.getBlockZ();
	}

	public String getUsername(){
		return username;
	}

	public UUID getUuid(){
		return uuid;
	}

	public String getGroupKey(){
		return groupKey;
	}

	public String getItemKey(){
		return itemKey;
	}

	public String getCommandsenderName(){
		return commandsenderName;
	}

	public String getWorld(){
		return world;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getZ(){
		return z;
	}
	
}
