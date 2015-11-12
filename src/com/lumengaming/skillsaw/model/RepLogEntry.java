package com.lumengaming.skillsaw.model;

import java.sql.Timestamp;
import java.util.UUID;


public class RepLogEntry {
	private int id;
	private RepType type;
	private UUID issuerUUID;
	private UUID targetUUID;
	private String targetName;
	private String issuerName;
	private Timestamp time;
	private double amount;
	private String reason;

	public RepLogEntry(int id, RepType type, User issuer, User target, Timestamp time, double amount, String reason){
		this.id = id;
		this.type = type;
		this.issuerName = issuer.getName();
		this.issuerUUID = issuer.getUuid();
		this.targetName = target.getName();
		this.targetUUID = target.getUuid();
		this.time = time;
		this.amount = amount;
		this.reason = reason;
	}
	
	public RepLogEntry(int id, RepType type,String issuerName, UUID issuerUUID, String targetName,UUID targetUUID, Timestamp time, double amount, String reason){
		this.id = id;
		this.type = type;
		this.issuerName = issuerName;
		this.issuerUUID = issuerUUID;
		this.targetName = targetName;
		this.targetUUID = targetUUID;
		this.time = time;
		this.amount = amount;
		this.reason = reason;
	}

	public int getId(){
		return id;
	}

	public RepType getType(){
		return type;
	}

	public UUID getIssuerUUID(){
		return issuerUUID;
	}

	public UUID getTargetUUID(){
		return targetUUID;
	}

	public String getTargetName(){
		return targetName;
	}

	public String getIssuerName(){
		return issuerName;
	}

	public Timestamp getTime(){
		return time;
	}

	public double getAmount(){
		return amount;
	}

	public String getReason(){
		return reason;
	}
	
}
