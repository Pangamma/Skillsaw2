package com.lumengaming.skillsaw.repository;

import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;

/**
 *
 * @author Taylor
 */
public class FlatFileDataRepository implements IDataRepository{

	public FlatFileDataRepository(File fYaml){
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public boolean onEnable(){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean onDisable(){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public User getUser(UUID uniqueId){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean saveUser(User user){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<User> getUsers(String username){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void logRep(User issuer, User target, double amount, RepType repType, String reason){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void logPromotion(User suer, User target, SkillType st, int oLevel, int nLevel, Location location){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void createUser(User user){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<User> getStaff(){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList<User> getInstructors(){
		throw new UnsupportedOperationException("Not supported yet.");
	}
}