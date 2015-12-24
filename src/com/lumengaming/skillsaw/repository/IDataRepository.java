package com.lumengaming.skillsaw.repository;

import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.ScavengerHuntLogEntry;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public interface IDataRepository {
	
	//<editor-fold defaultstate="collapsed" desc="Instance">
	/** Opening connections, initializing databases or files, etc **/
    public boolean onEnable();
	
	/** Closing hanging connections, saving files, etc. **/
	public boolean onDisable();
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Users">
	
	public User getUser(UUID uniqueId);
	
	public boolean saveUser(User user);
	
	public ArrayList<User> getUsers(String username);
	
	public void createUser(User user);
	
	public ArrayList<User> getStaff();
	
	public ArrayList<User> getInstructors();
	
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Logs">
	
	public void logRep(User issuer, User target, double amount, RepType repType, String reason);
	
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned);
	
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type,  UUID targetUuid,int maxResultsReturned,long minLogDate);
	
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate);
	
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned,long minLogDate);
	
	public void logPromotion(User suer, User target, SkillType st, int oLevel, int nLevel, Location location);
	
	public void logScavengerHuntEntry(ScavengerHuntLogEntry e);
	//</editor-fold>

	

}
