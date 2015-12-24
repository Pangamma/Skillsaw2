package com.lumengaming.skillsaw.repository;

import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.ScavengerHuntLogEntry;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;

public class TestDataRepository implements IDataRepository {

	ArrayList<RepLogEntry> logEntryCache = new ArrayList<>();
	ArrayList<User> usersCache = new ArrayList<>();

	@Override
	public boolean onEnable(){
		return true;
	}

	@Override
	public boolean onDisable(){
		return true;
	}

	@Override
	public User getUser(UUID uniqueId){
		Optional<User> findFirst = this.usersCache.stream().filter(e -> e.getUuid().equals(uniqueId)).findFirst();
		return findFirst.isPresent() ? findFirst.get() : null;
	}

	@Override
	public boolean saveUser(User user){
		Optional<User> findFirst = this.usersCache.stream().filter(e -> e.getUuid().equals(user.getUuid())).findFirst();
		if (findFirst.isPresent()){
			this.usersCache.remove(user);
		}
		this.usersCache.add(user);
		return true;
	}

	@Override
	public void createUser(User user){
		this.usersCache.add(user);
	}
	
	@Override
	public ArrayList<User> getUsers(String username){
		ArrayList<User> arrayList = new ArrayList<>();
		this.usersCache.stream().filter(e -> e.getName().equalsIgnoreCase(username)).forEach(
				arrayList::add
		);
		return arrayList;
	}

	@Override
	public void logRep(User issuer, User target, double amount, RepType repType, String reason){
		logEntryCache.add(new RepLogEntry(logEntryCache.size() + 1, repType, issuer, target, new Timestamp(System.currentTimeMillis()), amount, reason));
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned){
		ArrayList<RepLogEntry> arrayList = new ArrayList<>();
		this.logEntryCache.stream().filter(e -> e.getType().equals(type)).forEach(
				arrayList::add
		);
		return arrayList;
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate){
		ArrayList<RepLogEntry> arrayList = new ArrayList<>();
		this.logEntryCache.stream().filter(e -> e.getType().equals(type)
				&& e.getTargetUUID().equals(targetUuid)
				&& e.getTime().after(new Timestamp(minLogDate))
		).forEach(
				arrayList::add
		);
		return arrayList;
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate){
		ArrayList<RepLogEntry> arrayList = new ArrayList<>();
		this.logEntryCache.stream().filter(e -> e.getType().equals(type)
				&& e.getIssuerUUID().equals(issuerUuid)
				&& e.getTime().after(new Timestamp(minLogDate))
		).forEach(
				arrayList::add
		);
		return arrayList;
	}

	@Override
	public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, Location location){
	}

	@Override
	public ArrayList<User> getStaff(){
		return new ArrayList<User>();
	}

	@Override
	public ArrayList<User> getInstructors(){
		return new ArrayList<User>();
	}

	@Override
	public void logScavengerHuntEntry(ScavengerHuntLogEntry e){
	}

    @Override
    public ArrayList<RepLogEntry> getRepLogEntriesByTarget(UUID targetUuid, int maxResultsReturned, long minLogDate) {
        return logEntryCache;
    }
}
