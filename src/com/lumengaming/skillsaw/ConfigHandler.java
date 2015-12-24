package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.repository.MySqlDataRepository;
import com.lumengaming.skillsaw.repository.TestDataRepository;
import com.lumengaming.skillsaw.service.DataService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 * @author Taylor Love (Pangamma)
 */
public class ConfigHandler {
    
    private final Main plugin;
    private File file;
    private YamlConfiguration config;
    private double minutesBetweenReposts;
    private int maxEntriesInPublicReviewList;
    private String nRepMessageToTarget;
    private String sRepMessageToTarget;
    private String xRepMessageToTarget;
    private String nRepMessageToIssuer;
    private String sRepMessageToIssuer;
    private String xRepMessageToIssuer;
    private DataService dataService;
    private boolean enableMysql;
    private boolean enableSqlite;
    private String dataStorageFormat;
    private ArrayList<SkillType> skillTypes;
    private int maxNaturalRepsPerTimePeriod;
    private int hoursPerRepTimePeriod;
    
    public ConfigHandler(Main aThis) {
        this.plugin = aThis;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Load">
    /** loads the config and returns an instance of itself if successful.
     * @return  **/
    public ConfigHandler load(){
        //<editor-fold defaultstate="collapsed" desc="set up config">
        String filename = "config.yml";
        file = new File(Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).getDataFolder(), filename);
        
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                if (!Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).getDataFolder().exists()){
                    Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).getDataFolder().mkdir();
                }
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
            } catch (Exception e) {
                System.out.println("an unspecified error happened during the load method. of Woolcitychat.");
            }
        }
        //</editor-fold>
        
    
        this.hoursPerRepTimePeriod = getOrSet(config,"rates.hours-per-time-period",6);
        this.maxNaturalRepsPerTimePeriod = getOrSet(config,"rates.max-natural-reps-per-time-period",6);
        this.minutesBetweenReposts = getOrSet(config,"minutes-required-between-reposts", 60);
        this.maxEntriesInPublicReviewList = getOrSet(config,"max-entries-in-public-review-list", 15);
        
        this.nRepMessageToTarget = getOrSet(config,"messages.n-repped.target","&a%issuer% just gave you %amount% rep. Reason: %reason%");
        this.sRepMessageToTarget = getOrSet(config,"messages.s-repped.target","&a%issuer% just gave you %amount% staff rep. Reason: %reason%");
        this.xRepMessageToTarget = getOrSet(config,"messages.x-repped.target","&a%issuer% just fixed your rep. Reason: %reason%");
        
        this.nRepMessageToIssuer = getOrSet(config,"messages.n-repped.issuer","&aGave %amount% rep to %target% for :&2 %reason%");
        this.sRepMessageToIssuer = getOrSet(config,"messages.s-repped.issuer","&aGave %amount% s-rep to %target% for :&2 %reason%");
        this.xRepMessageToIssuer = getOrSet(config,"messages.x-repped.issuer","&aGave %amount% x-rep to %target% for :&2 %reason%");
        
        //<editor-fold defaultstate="collapsed" desc="Skill Types">
        this.skillTypes = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("skill-types");
        if (section == null){
            this.skillTypes.add(new SkillType(
            getOrSet(config, "skill-types.PixelArt.key", "pixelart"),
            getOrSet(config, "skill-types.PixelArt.list-name", "PixelArt"),
            getOrSet(config, "skill-types.PixelArt.default-level", 0),
            getOrSet(config, "skill-types.PixelArt.min-level", 0),
            getOrSet(config, "skill-types.PixelArt.max-level", 10),
            getOrSet(config, "skill-types.PixelArt.min-instruct-level", 6),
            getOrSet(config, "skill-types.PixelArt.title-format.short", "&aP"+SkillType.LEVEL_VAR_STR),
            getOrSet(config, "skill-types.PixelArt.title-format.long", "&aPixelArtistT"+SkillType.LEVEL_VAR_STR)
            ));
        }else{
            Set<String> keys = section.getKeys(false);
            for(String key : keys){
            this.skillTypes.add(new SkillType(
                getOrSet(config, "skill-types."+key+".key", "foo"),
                getOrSet(config, "skill-types."+key+".list-name", "Foo"),
                getOrSet(config, "skill-types."+key+".default-level", 0),
                getOrSet(config, "skill-types."+key+".min-level", 0),
                getOrSet(config, "skill-types."+key+".max-level", 10),
                getOrSet(config, "skill-types."+key+".min-instruct-level", 6),
                getOrSet(config, "skill-types."+key+".title-format.short", "&aF"+SkillType.LEVEL_VAR_STR),
                getOrSet(config, "skill-types."+key+".title-format.long", "&aFooT"+SkillType.LEVEL_VAR_STR)
                ));
            }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Data Formats">
        this.dataStorageFormat = getOrSet(config,"data-format","TEST");
        
        switch(this.dataStorageFormat.toUpperCase()){
            
//            case "SQLITE":
//                File fSqlite = new File(Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).getDataFolder(), "data.sqlite");
//                this.dataService = new DataService(plugin, new SqliteDataRepository(plugin,fSqlite));
//                break;
            case "TEST":
                this.dataService = new DataService(plugin, new TestDataRepository());
                break;
            case "MYSQL":
                String host = getOrSet(config, "mysql.host", "127.0.0.1");
                int port = getOrSet(config,"mysql.port",3306);
                String user = getOrSet(config,"mysql.user","skillsaw_user");
                String pass = getOrSet(config,"mysql.pass","password");
                String dbName = getOrSet(config, "mysql.database", "skillsaw");
                boolean isReadOnly = getOrSet(config,"mysql.is-read-only",false);
                this.dataService = new DataService(plugin, new MySqlDataRepository(plugin,host,port,user,pass,dbName,isReadOnly));
                break;
//                
//            case "FLAT_FILE":
//                File fYaml = new File(Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).getDataFolder(), "data.yml");
//                this.dataService = new DataService(plugin, new FlatFileDataRepository(fYaml));
//                break;
                
            default:
                throw new IllegalArgumentException("data-format must be one of these: [SQLITE, MYSQL, FLAT_FILE, TEST]"); 
        }
        //</editor-fold>
        
        try {
            config.save(file);
        } catch (IOException ex) {
            Logger.getLogger(ConfigHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get/Set">    
    private boolean getOrSet(YamlConfiguration config, String path,boolean defValue){
        if (!config.contains(path)){
            config.set(path, defValue);
        }            
        return config.getBoolean(path);
    }
    private String getOrSet(YamlConfiguration config, String path,String defValue){
        if (!config.contains(path)){
            config.set(path, defValue);
        }            
        return config.getString(path);
    }
    private int getOrSet(YamlConfiguration config, String path,int defValue){
        if (!config.contains(path)){
            config.set(path, defValue);
        }            
        return config.getInt(path);
    }
    private double getOrSet(YamlConfiguration config, String path,double defValue){
        if (!config.contains(path)){
            config.set(path, defValue);
        }            
        return config.getDouble(path);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Accessors">
    public DataService getDataService(){
        return this.dataService;
    }

    public double getMinutesBetweenReposts() {
        return this.minutesBetweenReposts;
    }

    public int getMaxEntriesToKeepInPublicReviewList() {
        return this.maxEntriesInPublicReviewList;
    }
    public String getNRepFormatToTarget() {
        return this.nRepMessageToTarget.replace('&','§');
    }
    public String getSRepFormatToTarget() {
        return this.sRepMessageToTarget.replace('&','§');
    }
    public String getXRepFormatToTarget() {
        return this.xRepMessageToTarget.replace('&','§');
    }
    public String getNRepFormatToIssuer() {
        return this.nRepMessageToIssuer.replace('&','§');
    }
    public String getSRepFormatToIssuer() {
        return this.sRepMessageToIssuer.replace('&','§');
    }
    public String getXRepFormatToIssuer() {
        return this.xRepMessageToIssuer.replace('&','§');
    }
    //</editor-fold>

    public ArrayList<SkillType> getSkillTypes(){
        return skillTypes;
    }

    /** Replaces isuer, target, amount, and reason variables with the inputs. **/
    public String compileMessageFormat(String format, String issuer, String target, double amount, String reason){
        return format
                .replace("%issuer%", issuer)
                .replace("%target%", target)
                .replace("%amount%", ""+ User.round(amount))
                .replace("%reason%", reason);
    }

    public int getMaxEntriesInPublicReviewList(){
        return maxEntriesInPublicReviewList;
    }

    public int getMaxNaturalRepsPerTimePeriod(){
        return maxNaturalRepsPerTimePeriod;
    }

    public int getHoursPerRepTimePeriod(){
        return hoursPerRepTimePeriod;
    }
    
}
