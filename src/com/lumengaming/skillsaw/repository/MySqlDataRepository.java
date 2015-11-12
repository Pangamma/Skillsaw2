package com.lumengaming.skillsaw.repository;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.Title;
import com.lumengaming.skillsaw.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class MySqlDataRepository implements IDataRepository {

	private final String username;
	private final String password;
	private final String database;
	private final int port;
	private final String host;
	private Connection connection;
	private final Main plugin;

	public MySqlDataRepository(Main p_plugin, String p_Host, int p_Port, String p_Username, String p_Password, String p_Database){
		this.plugin = p_plugin;
		this.host = p_Host;
		this.port = p_Port;
		this.username = p_Username;
		this.password = p_Password;
		this.database = p_Database;
	}

	@Override
	public boolean onEnable(){
		return initTables();	// connect happens within init tables.
	}

	/**
	 * Call to ensure a connection can be formed and is ready. *
	 */
	private boolean connect(){
		try{
			if (this.connection == null){
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + this.port + "/" + this.database, this.username, this.password);
			}
			else if (this.connection != null && !this.connection.isValid(3)){
				try{
					this.connection.close();
				}
				catch (Exception ex){
					System.err.println(ex);
				}
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + this.port + "/" + this.database, this.username, this.password);
			}
			return true;
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	@Override
	public boolean onDisable(){
		return this.disconnect();
	}

	private boolean initTables(){
		if (connect()){
			try{
				String q = "SHOW COLUMNS FROM `skillsaw_users` WHERE Field LIKE 's\\_%'";
				PreparedStatement ps = connection.prepareStatement(q);
				ResultSet rs = ps.executeQuery();
				ArrayList<SkillType> typesFromDb = new ArrayList<>();
				while (rs.next()){
					typesFromDb.add(
							new SkillType(
									rs.getString("Field").replaceFirst("s_", ""),
									"dummy",
									rs.getInt("Default"), -1, -1, -1, "dummy", "dummy")
					);
				}
				ArrayList<SkillType> sts = plugin.getConfigHandler().getSkillTypes();

				for (SkillType st : sts){
					if (st.getKey().matches("([a-zA-Z0-9_]*)")){
						SkillType dbSt = null;
						for (SkillType dbT : typesFromDb){
							if (dbT.getKey().equalsIgnoreCase(st.getKey())){
								dbSt = dbT;
								break;
							}
						}
						if (dbSt != null){
							if (dbSt.getDefLevel() != st.getDefLevel()){
								System.out.println("SkillSaw: Changing default level for " + st.getKey() + " skill type.");
								ps = connection.prepareStatement("ALTER TABLE `skillsaw_users`	CHANGE COLUMN `s_" + dbSt.getKey() + "` `s_" + dbSt.getKey() + "` INT(11) NOT NULL DEFAULT '" + st.getDefLevel() + "';");
								ps.execute();
							}
							else{
								// Everything matches. No need to change anything.
							}
						}
						else{
							System.out.println("SkillSaw: Adding " + st.getKey() + " skill type column to MySQL db.");
							ps = connection.prepareStatement("ALTER TABLE `skillsaw_users` ADD COLUMN `s_" + st.getKey() + "` INT(11) NOT NULL DEFAULT '" + st.getDefLevel() + "'");
							ps.execute();
						}
					}
					else{
						throw new IllegalArgumentException("Key for skill types must match this regex: ([a-zA-Z0-9_]*)");
					}
				}
				return true;
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return false;
	}

	private boolean disconnect(){
		if (this.connection != null){
			try{
				this.connection.close();
				this.connection = null;
				return true;
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true;
	}

	@Override
	public User getUser(UUID uniqueId){
		User output = null;
		String q = "SELECT * FROM `skillsaw_users` WHERE `uuid` = ? limit 1";
		try{
			if (connect()){
				PreparedStatement ps = this.connection.prepareStatement(q);
				ps.setString(1, uniqueId.toString());
				ResultSet rs = ps.executeQuery();
				if (rs.next()){
					return readUser(rs);
				}
			}
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return output;
	}

	@Override
	public void createUser(User user){
		if (connect()){
			try{
				String q = "INSERT INTO `skillsaw_users` "
						+ "(`uuid`, `username`,`display_name`, `ipv4`, `current_title`, `custom_titles`,"
						+ "	`chat_color`,`rep_level`,`natural_rep`,`staff_rep`,`last_played`,"
						+ "	`first_played`,`speaking_channel`,`sticky_channels`,`ignored_players`";
				for (SkillType st : plugin.getConfigHandler().getSkillTypes()){
					q += ",`s_" + st.getKey() + "`";
				}
				q += ") VALUES (?,?,?,?,?,  ?,?,?,?,?    ,?,?,?,?,?";
				for (SkillType st : plugin.getConfigHandler().getSkillTypes()){
					q += ",?";
				}
				q += ")";
				PreparedStatement ps = connection.prepareStatement(q);
				int i = 1;
				ps.setString(i++, user.getUuid().toString());
				ps.setString(i++, user.getName());
				ps.setString(i++, user.getDisplayName().replace('§', '&'));
				ps.setString(i++, user.getIpv4());
				ps.setString(i++, user.getCurrentTitle().toString());
				ArrayList<Title> customTitlesReadOnly = user.getCustomTitlesReadOnly();
				String cTitlesStr = "";
				for (Title t : customTitlesReadOnly){
					cTitlesStr += t.toString() + "\n";
				}
				ps.setString(i++, cTitlesStr);
				ps.setString(i++, user.getChatColor().replace('§', '&'));
				ps.setInt(i++, user.getRepLevel());
				ps.setDouble(i++, user.getNaturalRep());
				ps.setDouble(i++, user.getStaffRep());
				ps.setLong(i++, user.getLastPlayed());
				ps.setLong(i++, user.getFirstPlayed());
				ps.setString(i++, user.getSpeakingChannel());
				ps.setString(i++, String.join("\n", user.getStickyChannels()));
				ps.setString(i++, String.join("\n", user.getIgnored()));

				for (SkillType st : plugin.getConfigHandler().getSkillTypes()){
					ps.setInt(i++, user.getSkill(st));
				}
				ps.execute();
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public boolean saveUser(User u){

		String q = "UPDATE `skillsaw_users` SET "
				+ "`username`=?, `display_name`=?, `ipv4`=?, `current_title`=?,"
				+ "`custom_titles` = ?,`chat_color` = ?, `rep_level` = ?,"
				+ "`natural_rep` = ?, `staff_rep`= ?,`last_played` = ?,"
				+ "`first_played` = ?,`speaking_channel` = ?, `sticky_channels` = ?,"
				+ "`ignored_players` = ?, is_staff = ?, is_instructor = ?";

		for (SkillType st : plugin.getConfigHandler().getSkillTypes()){
			q += ",`s_" + st.getKey() + "` = " + u.getSkill(st);
		}
		q += " WHERE `uuid` = ?";
		try{
			if (connect()){
				PreparedStatement ps = connection.prepareStatement(q);
				int i = 1;
				ps.setString(i++, u.getName());
				ps.setString(i++, u.getDisplayName().replace('§', '&'));
				ps.setString(i++, u.getIpv4().replace('§', '&'));
				ps.setString(i++, u.getCurrentTitle().toString());

				ArrayList<Title> customTitlesReadOnly = u.getCustomTitlesReadOnly();
				String cTitlesStr = "";
				for (Title t : customTitlesReadOnly){
					cTitlesStr += t.toString() + "\n";
				}
				ps.setString(i++, cTitlesStr);
				ps.setString(i++, u.getChatColor().replace('§', '&'));
				ps.setInt(i++, u.getRepLevel());
				ps.setDouble(i++, u.getNaturalRep());
				ps.setDouble(i++, u.getStaffRep());
				ps.setLong(i++, u.getLastPlayed());
				ps.setLong(i++, u.getFirstPlayed());
				ps.setString(i++, u.getSpeakingChannel());
				ps.setString(i++, String.join("\n", u.getStickyChannels()));
				ps.setString(i++, String.join("\n", u.getIgnored()));
				ps.setInt(i++, u.isStaff() ? 1 : 0);
				ps.setInt(i++, u.isInstructor() ? 1 : 0);

				ps.setString(i++, u.getUuid().toString());
				return ps.executeUpdate() > 0;
			}
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Returns max of 50 rows where is_staff = 1.
	 * @return 
	 */
	@Override
	public ArrayList<User> getStaff(){
		ArrayList<User> output = new ArrayList<>();
		String q = "SELECT * FROM `skillsaw_users` WHERE `is_staff` = 1 ORDER BY last_played DESC limit 50";
		try{
			if (connect()){
				PreparedStatement ps = this.connection.prepareStatement(q);
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					User u = readUser(rs);
					if (u != null){
						output.add(u);
					}
				}
			}
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return output;
	}
		
	/**
	 * Returns max of 50 rows where is_staff = 1.
	 * @return 
	 */
	@Override
	public ArrayList<User> getInstructors(){
		ArrayList<User> output = new ArrayList<>();
		String q = "SELECT * FROM `skillsaw_users` WHERE `is_instructor` = 1 ORDER BY last_played DESC limit 50";
		try{
			if (connect()){
				PreparedStatement ps = this.connection.prepareStatement(q);
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					User u = readUser(rs);
					if (u != null){
						output.add(u);
					}
				}
			}
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return output;
	}
	
	/**
	 * Maximum of 1000 results. If more than that many are returned, something
	 * probably went wrong with the query.
	 *
	 * @param username
	 * @return
	 */
	@Override
	public ArrayList<User> getUsers(String username){
		ArrayList<User> output = new ArrayList<>();
		String q = "SELECT * FROM `skillsaw_users` WHERE `username` LIKE ? limit 1000";
		try{
			if (connect()){
				PreparedStatement ps = this.connection.prepareStatement(q);
				ps.setString(1, "%" + username + "%");
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					User u = readUser(rs);
					if (u != null){
						output.add(u);
					}
				}
			}
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return output;
	}

	/**
	 * Expects that RS has been primed before calling. "rs.next()". Returns null
	 * on failure, also throws exception into console.
	 */
	private User readUser(ResultSet rs){
		User u = null;
		try{
			ArrayList<SkillType> skillTypes = plugin.getConfigHandler().getSkillTypes();
			HashMap<SkillType, Integer> skills = new HashMap<>();
			for (SkillType st : skillTypes){
				skills.put(st, rs.getInt("s_" + st.getKey()));
			}
			String customTitlesStr = rs.getString("custom_titles");
			String[] titleStrings = customTitlesStr.replace('&', '§').split("\n");
			ArrayList<Title> customTitles = new ArrayList<>();

			for (String titleStr : titleStrings){
				Title tmp = Title.fromString(titleStr);
				if (tmp != null){
					customTitles.add(tmp);
				}
			}

			boolean rsIsStaff = rs.getInt("is_staff") == 1;
			boolean rsIsInstructor = rs.getInt("is_instructor") == 1;
			UUID rsUuid = UUID.fromString(rs.getString("uuid"));
			String rsUsername = rs.getString("username");
			String rsDispName = rs.getString("display_name").replace('&', '§');
			long rsLPlayed = rs.getLong("last_played");
			long rsPlayed = rs.getLong("first_played");
			double rsNRep = rs.getDouble("natural_rep");
			double rsSRep = rs.getDouble("staff_rep");
			Title rsCurTitle = Title.fromString(rs.getString("current_title").replace('&', '§'));
			String rsChatcolor = rs.getString("chat_color").replace('&', '§');
			String rsIpv4 = rs.getString("ipv4");
			String rsSpeakingChannel = rs.getString("speaking_channel");
			CopyOnWriteArraySet<String> rsStickie = new CopyOnWriteArraySet<>(readListFromString(rs.getString("sticky_channels")));
			CopyOnWriteArraySet<String> rsIgnored = new CopyOnWriteArraySet<>(readListFromString(rs.getString("ignored_players")));
			u = new User(rsUuid, rsUsername, rsDispName, rsLPlayed, rsPlayed, rsNRep, rsSRep,
					skills, customTitles, rsCurTitle, rsChatcolor, rsIpv4, rsSpeakingChannel,
					rsStickie, rsIgnored,rsIsStaff,rsIsInstructor);
		}
		catch (SQLException ex){
			Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return u;
	}

	@Override
	public void logRep(User issuer, User target, double amount, RepType repType, String reason){
		String q = "INSERT INTO `replog` (`rep_type`,  `issuer_id`,  `target_id`,  `issuer_name`,  `target_name`,  `amount`,  `reason`) "
				+ "VALUES (?,(SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),(SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),?,?,?,?);";
		if (connect()){
			try{
				PreparedStatement ps = connection.prepareStatement(q);
				int i = 1;
				ps.setInt(i++, repType.toInt());
				ps.setString(i++, issuer.getUuid().toString());
				ps.setString(i++, target.getUuid().toString());
				ps.setString(i++, issuer.getName());
				ps.setString(i++, target.getName());
				ps.setDouble(i++, User.round(amount));
				ps.setString(i++, reason);
				ps.executeUpdate();
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else{
			System.out.println("Failed to connect to the DB. Could not log rep.");
		}
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntries(RepType type, int maxResultsReturned){
		ArrayList<RepLogEntry> output = new ArrayList<>();
		String q
				= "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
				+ "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
				+ "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
				+ "WHERE `rep_type` = " + type.toInt()
				+ " ORDER BY r.`id` DESC "
				+ " limit " + maxResultsReturned;
		if (connect()){
			try{
				PreparedStatement ps = connection.prepareStatement(q);
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					RepLogEntry e = readRepLogEntry(rs);
					if (e != null){
						output.add(e);
					}
				}
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return output;
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByTarget(RepType type, UUID targetUuid, int maxResultsReturned, long minLogDate){
		ArrayList<RepLogEntry> output = new ArrayList<>();
		String q
				= "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
				+ "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
				+ "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
				+ "WHERE `rep_type` = " + type.toInt() + " AND t.uuid = ? AND r.time >= ? "
				+ " ORDER BY r.`id` DESC "
				+ " limit " + maxResultsReturned;
		if (connect()){
			try{
				PreparedStatement ps = connection.prepareStatement(q);
				ps.setString(1, targetUuid.toString());
				ps.setTimestamp(2, new Timestamp(minLogDate));
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					RepLogEntry e = readRepLogEntry(rs);
					if (e != null){
						output.add(e);
					}
				}
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return output;
	}

	@Override
	public ArrayList<RepLogEntry> getRepLogEntriesByIssuer(RepType type, UUID issuerUuid, int maxResultsReturned, long minLogDate){

		ArrayList<RepLogEntry> output = new ArrayList<>();
		String q
				= "SELECT r.*,t.uuid as `target_uuid`,t.uuid as `issuer_uuid` FROM replog r\n "
				+ "INNER JOIN skillsaw_users i ON i.user_id = r.issuer_id\n "
				+ "INNER JOIN skillsaw_users t ON t.user_id = r.target_id\n "
				+ "WHERE `rep_type` = " + type.toInt() + " AND i.uuid = ? AND r.time >= ? "
				+ " ORDER BY r.`id` DESC "
				+ " limit " + maxResultsReturned;
		if (connect()){
			try{
				PreparedStatement ps = connection.prepareStatement(q);
				ps.setString(1, issuerUuid.toString());
				ps.setTimestamp(2, new Timestamp(minLogDate));
				ResultSet rs = ps.executeQuery();
				while (rs.next()){
					RepLogEntry e = readRepLogEntry(rs);
					if (e != null){
						output.add(e);
					}
				}
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return output;
	}

	/**
	 * Expects rs is pre-primed. *
	 */
	private RepLogEntry readRepLogEntry(ResultSet rs) throws SQLException{
		int id = rs.getInt("id");
		Timestamp time = rs.getTimestamp("time");
		String reason = rs.getString("reason");
		double amount = rs.getDouble("amount");
		RepType type = RepType.fromInt(rs.getInt("rep_type"));
		String iName = rs.getString("issuer_name");
		String tName = rs.getString("target_name");
		UUID iUUID = UUID.fromString(rs.getString("issuer_uuid"));
		UUID tUUID = UUID.fromString(rs.getString("target_uuid"));
		RepLogEntry e = new RepLogEntry(id, type, iName, iUUID, tName, tUUID, time, amount, reason);
		return e;
	}

	@Override
	public void logPromotion(User issuer, User target, SkillType st, int oLevel, int nLevel, Location l){
		String q = "INSERT INTO `promo_log` (`skill_type`,  `issuer_id`,  `target_id`,  `issuer_name`,  `target_name`,  `olevel`,`nlevel`,  `location`) "
				+ "VALUES (?,IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),IFNULL((SELECT `user_id` FROM `skillsaw_users` WHERE `uuid` = ?),-1),?,?,?,?,?);";
		if (connect()){
			try{
				PreparedStatement ps = connection.prepareStatement(q);
				int i = 1;
				ps.setString(i++, st.getKey());
				ps.setString(i++, issuer.getUuid().toString());
				ps.setString(i++, target.getUuid().toString());
				ps.setString(i++, issuer.getName());
				ps.setString(i++, target.getName());
				ps.setInt(i++, oLevel);
				ps.setInt(i++, nLevel);
				ps.setString(i++, l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getWorld().getName());
				ps.executeUpdate();
			}
			catch (SQLException ex){
				Logger.getLogger(MySqlDataRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else{
			System.out.println("Failed to connect to the DB. Could not log rep.");
		}
	}

	/**
	 * Removes \r, splits by \n. *
	 */
	private ArrayList<String> readListFromString(String s){
		ArrayList<String> output = new ArrayList<String>();
		if (s != null){
			String[] split = s.replace("\r", "").split("\n");
			for (String line : split){
				output.add(line.trim());
			}
		}
		return output;
	}

	/**
	 * Removes \r, splits by \n. *
	 */
	private String toStringFromList(ArrayList<String> list){
		String output = "";
		if (list != null){
			for (String s : list){
				output += s + "\n";
			}
		}
		return output;
	}


}
