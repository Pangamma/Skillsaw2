package com.lumengaming.skillsaw.repository;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.ScavengerHuntLogEntry;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class SqliteDataRepository implements IDataRepository{
	private final File f;
	private final Main plugin;
	public SqliteDataRepository(Main p_plugin, File f){
		this.f = f;
		this.plugin = p_plugin;
	}

	@Override
	public boolean onEnable(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean onDisable(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public User getUser(UUID uniqueId){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean saveUser(User user){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<User> getUsers(String username){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void logRep(User issuer, User target, double amount, RepType repType, String reason){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void logPromotion(User suer, User target, SkillType st, int oLevel, int nLevel, Location location){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void createUser(User user){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<User> getStaff(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ArrayList<User> getInstructors(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void logScavengerHuntEntry(ScavengerHuntLogEntry e){
		throw new UnsupportedOperationException("Not supported yet.");
	}

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
