package com.lumengaming.skillsaw.service;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.MutedPlayer;
import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.repository.IDataRepository;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Taylor
 */
public class DataService {

	//<editor-fold defaultstate="collapsed" desc="General">
	private final IDataRepository repo;
	private final Main plugin;
	private final ConcurrentHashMap<UUID, User> onlineUsers = new ConcurrentHashMap<>();

	public DataService(Main p_plugin, IDataRepository p_repository){
		this.repo = p_repository;
		this.plugin = p_plugin;
	}

	public boolean onEnable(){
		if (this.repo.onEnable()){
			for (Player p : Bukkit.getOnlinePlayers()){
				this.loginUser(p);
			}
			return true;
		}
		return false;
	}

	/**
	 * Non asynchronous. *
	 */
	public boolean onDisable(){
		this.saveUsers(false);
		this.onlineUsers.clear(); // ^make sure the above is synchronous first.
		return this.repo.onDisable();
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Saving Users">
	/**
	 * Should probably run this asynchronously. *
	 */
	private void saveUsers(boolean isAsync){
		if (isAsync){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run(){
					for (User u : onlineUsers.values()){
						DataService.this.repo.saveUser(u);
					}
				}
			});
		}
		else{
			for (User u : onlineUsers.values()){
				this.repo.saveUser(u);
			}
		}
	}

	/**
	 * Save is asynchronous. If user is not writable, an illegal state exception
	 * is thrown. So basically, make sure it is not being called before this is
	 * initialized.
	 *
	 * @param user
	 */
	public void saveUser(User user){
		this.saveUser(user, true);
	}

	/**
	 * Should probably run this asynchronously. *
	 */
	public void saveUser(User user, boolean isAsync){
		if (isAsync){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run(){
					repo.saveUser(user);
				}
			});
		}
		else{
			this.repo.saveUser(user);
		}
	}

	/**
	 * Should probably run this asynchronously. Use this only when making users
	 * for the first time. *
	 */
	private void createUser(User user, boolean isAsync){
		if (isAsync){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run(){
					repo.createUser(user);
				}
			});
		}
		else{
			this.repo.createUser(user);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Chat">
	/**
	 * Sends message to all ChatPlayers listening to the channel. 
	*
	 */
	@Deprecated
	public synchronized void sendMessageToChannel(String channelName, BaseComponent message){
		sendMessageToChannel("$", channelName, message);
	}

	/**
	 * Sends message to all ChatPlayers listening to the channel. Respects the
	 * ignored sender lists of each chat player. *
	 */
	public synchronized void sendMessageToChannel(String p_senderName, String channelName, BaseComponent message){
		MutedPlayer mp = this.plugin.getMuteService().getMutedPlayer(p_senderName);
		if (mp != null){
			if (!mp.isExpired()){
				if (mp.isSoftMute()){
					User user = this.getUser(p_senderName);
					if (user != null){
						user.sendMessage(message);
					}
				}
				return;
			}
			else{
				this.plugin.getMuteService().removeExpiredMutedPlayers();
			}
		}
//		this.dataService.
		for (User cp : this.onlineUsers.values()){
			if (cp.isListeningOnChannel(channelName) && !cp.isIgnoringPlayer(p_senderName)){
				cp.sendMessage(message);
			}
		}
	}

	/**
	 * The list is read only. The users can be edited... in theory. Try to avoid
	 * editing any users within this list if you can. Concurrency is a tricky
	 * thing that creates hard to find bugs if you aren't careful. To be safe,
	 * avoid writing at all to the users from this method.
	 *
	 * @return
	 */
	public synchronized ArrayList<User> getOnlineUsersReadOnly(){
		ArrayList<User> users = new ArrayList<>(this.onlineUsers.values());
		return users;
	}

	/**
	 * Sends message to all ChatPlayers listening to the channel. *
	 */
	@Deprecated
	public synchronized void sendMessageToChannel(String channelName, BaseComponent[] message){
		sendMessageToChannel("$", channelName, message);
	}

	/**
	 * Sends message to all ChatPlayers listening to the channel. Respects the
	 * ignored sender lists of each chat player. *
	 */
	public synchronized void sendMessageToChannel(String p_senderName, String channelName, BaseComponent[] message){
		MutedPlayer mp = this.plugin.getMuteService().getMutedPlayer(p_senderName);
		if (mp != null){
			if (!mp.isExpired()){
				if (mp.isSoftMute()){
					for (User cp : this.onlineUsers.values()){
						if (cp.getName().equalsIgnoreCase(p_senderName)){
							cp.sendMessage(message);
						}
					}
				}
				return;
			}
			else{
				this.plugin.getMuteService().removeExpiredMutedPlayers();
			}
		}
		for (User cp : this.onlineUsers.values()){
			if (cp.isListeningOnChannel(channelName)){
				if (!cp.isIgnoringPlayer(p_senderName)){
					cp.sendMessage(message);
				}
			}
		}
	}

	/**
	 * asynchronously log a chat message *
	 */
	public void logChat(UUID p_uuid, String p_displayName, String p_channel, String p_message){

	}

	/**
	 * Asynchronously log a command *
	 */
	public void logCommand(UUID p_uuid, String p_command){

	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Login">
	/**
	 * Must be called from a synchronous context. Creates user if not exist.
	 * Otherwise updates username, ipv4, and lastPlayed time.
	 *
	 * @param p *
	 */
	public void loginUser(final Player p){
		if (!this.onlineUsers.containsKey(p.getUniqueId())){
			final UUID uuid = p.getUniqueId();
			final ArrayList<SkillType> sts = plugin.getConfigHandler().getSkillTypes();
			this.getOfflineUser(uuid, false, (User u) -> {
				User nU = null;
				if (u != null){
					nU = u;
					nU.setName(p.getName());
					nU.setIpv4(p.getAddress().getAddress().getHostAddress());
					nU.setLastPlayed(System.currentTimeMillis());
					// Insert all the possible skill types if not already present.
					for (int i = 0; i < sts.size(); i++){
						SkillType st = sts.get(i);
						nU.getSkill(st);
					}
					onlineUsers.put(uuid, nU);
					DataService.this.saveUser(nU, true);	// Update the user into the DB.
				}
				else{
					nU = new User(p);
					// Insert all the possible skill types if not already present.
					for (int i = 0; i < sts.size(); i++){
						SkillType st = sts.get(i);
						nU.getSkill(st);
					}
					onlineUsers.put(uuid, nU);
					DataService.this.createUser(nU, true);	// Update the user into the DB.
				}
			});
		}
		else{
			throw new IllegalStateException("Logging in a user that was already logged in? Examine the code, or this could turn into a memory leak.");
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Logout">
	/**
	 * Saves the user and removes it from the online user list. Save operation
	 * happens asynchronously. Don't use this method for closing the plugin. Use
	 * save all users instead.
	 *
	 * @param p
	 */
	public void logoutUser(final Player p){
		if (this.onlineUsers.containsKey(p.getUniqueId())){
			User user = this.onlineUsers.get(p.getUniqueId());
			this.onlineUsers.remove(p.getUniqueId());
			this.saveUser(user, true);
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Get">
	/**
	 * Only checks through currently online users for the data. Null if not
	 * found. *
	 */
	public User getUser(UUID uuid){
		if (onlineUsers.containsKey(uuid)){
			return onlineUsers.get(uuid);
		}
		return null;
	}

	/**
	 * Attempts to resolve the partial name into a full name of an online
	 * player. If found, the User is returned. Otherwise null is returned. Only
	 * checks results from online users. Will make no calls to file system for
	 * the data.
	 *
	 * @param partialUsername
	 * @return 
	 *
	 */
	public User getUser(String partialUsername){
		Player p = STATIC.getPlayer(partialUsername);
		if (p != null){
			return getUser(p.getUniqueId());
		}
		return null;
	}

	/**
	 * All users from the database where is_staff = 1 will be returned. Ordered
	 * by most recent login. 
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return  *
	 */
	public void getOfflineStaff(AsyncManyCallback<User> callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<User> u = this.repo.getStaff();
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(u);
			});
		});
	}	
	/**
	 * All users from the database where is_instructor = 1 will be returned. Ordered
	 * by most recent login. 
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return  *
	 */
	public void getOfflineInstructors(AsyncManyCallback<User> callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<User> u = this.repo.getInstructors();
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(u);
			});
		});
	}
	
	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param uuid
	 * @param useCache
	 * @param callback
	 * @return  *
	 */
	public void getOfflineUser(UUID uuid, boolean useCache, AsyncCallback<User> callback){
		if (useCache){
			if (this.onlineUsers.containsKey(uuid)){
				User u = this.onlineUsers.get(uuid);
				callback.doCallback(u);
				return;
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			User u = this.repo.getUser(uuid);
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(u);
			});
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return  *
	 */
	public void getOfflineUser(String username, boolean useCache, AsyncCallback<User> callback){
		if (useCache){
			synchronized (this.onlineUsers){
				for (User u : this.onlineUsers.values()){
					if (u.getName().equalsIgnoreCase(username)){
						callback.doCallback(u);
						return;
					}
				}
			}
		}
		this.getOfflineUsers(username, useCache, (ArrayList<User> users) -> {
			int shortestLen = Integer.MAX_VALUE;
			User tmp = null;
			for (int i = 0; i < users.size(); i++){
				User u = users.get(i);
				if (u.getName().length() < shortestLen){
					shortestLen = u.getName().length();
					tmp = u;
				}
			}
			callback.doCallback(tmp);
		});
	}

	/**
	 * IO heavy operation. Loads the user from the database. Run this
	 * asynchronously. if useCache is true, users can be loaded from the online
	 * users hashmap before being taken from the database.
	 *
	 * @param username
	 * @param callback
	 * @param useCache
	 * @return  *
	 */
	public void getOfflineUsers(String username, boolean useCache, AsyncManyCallback<User> callback){
		if (useCache){
			synchronized (this.onlineUsers){
				for (User u : this.onlineUsers.values()){
					if (u.getName().equalsIgnoreCase(username)){
						ArrayList<User> us = new ArrayList<User>();
						us.add(u);
						callback.doCallback(us);
						return;
					}
				}
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<User> us = this.repo.getUsers(username);
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(us);
			});
		});
	}

	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Logging">
	/**
	 * asynchronously logs the rep to the DB or save file. *
	 */
	public void logRep(User issuer, User target, double amount, RepType repType, String reason){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			this.repo.logRep(issuer, target, amount, repType, reason);
		});
	}

	public void getLogEntries(RepType type, int maxResultsReturned, AsyncManyCallback<RepLogEntry> callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntries(type, maxResultsReturned);
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate, AsyncManyCallback<RepLogEntry> callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntriesByTarget(type, targetUuid, maxResultsReturned, minLogDate);
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByTarget(RepType type, UUID targetUuid, AsyncManyCallback<RepLogEntry> callback){
		getLogEntriesByTarget(type, targetUuid, 1000, (System.currentTimeMillis() - 86400000), callback);
	}

	public void getLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate, AsyncManyCallback<RepLogEntry> callback){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			ArrayList<RepLogEntry> result = DataService.this.repo.getRepLogEntriesByIssuer(type, issuerUuid, maxResultsReturned, minLogDate);
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.doCallback(result);
			});
		});
	}

	public void getLogEntriesByIssuer(RepType type, UUID issuerUuid, AsyncManyCallback<RepLogEntry> callback){
		getLogEntriesByIssuer(type, issuerUuid, 1000, (System.currentTimeMillis() - 86400000), callback);
	}

	/**
	 * Log a skilltype level change. *
	 */
	public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, Location location){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			DataService.this.repo.logPromotion(issuer, target, st, oLevel, nLevel, location);
		});
	}
	//</editor-fold>

}
