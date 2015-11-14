package com.lumengaming.skillsaw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Taylor Love (Pangamma)
 */
public class STATIC {
    
    //<editor-fold defaultstate="collapsed" desc="colors & formatting">
    
    /**
     * the color used for the things being shown between the divider bars. §2=§7
     */
    public static final String C_MENU_CONTENT = "§2=§7 ";
    
    /**
     * Use this color for alternating lists. Color used for things between the
     * divider bars. bars.§2=§f *
     */
    public static final String C_MENU_CONTENT2 = "§2=§f ";
    
    /**
     * the color used for the things being shown between the divider bars. §2=§7
     */
    public static final String C_MENU_CONTENT_NC = "= ";

    /**
     * §2=§e§l SkillSaw - * Start with a div line above this and end with a div
     * line below it.
     */
    public static final String C_DIV_TITLE_PREFIX = "§2=§e§l SkillSaw - ";

    /**
     * = Skillsaw - *
     */
    public static final String C_DIV_TITLE_PREFIX_NC = "= SkillSaw - ";

    /**
     * has no color by default.*
     */
    public static final String C_DIV_LINE_NC = "=====================================================";
    
    /**
     * 53 things in length §2=§a=§2=§a=§2=§a=§2=....§2=§a=§2=§a=§2=§a=§2= *
     */
    public static final String C_DIV_LINE = "§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=§a=§2=";

    //</editor-fold>

    public static final String ERROR_NOT_YET_IMPLEMENTED = "§cThis feature isn't ready yet.";
    public static final String ERROR_FEATURE_REMOVED = "§cThis feature isn't available for your current version.";
    public static final String ERROR_P_NOT_FOUND = "§cThat player is not available.";
    public static final String ERROR_FUKKITRAGE = "§cThis might work normally, but because of an update to craftbukkit it is currently broken. You'll have to hunt down a new version of this plugin if you want to use this feature again. Sorry. :(";
    public static final String ERROR_DIRECTORY_NOT_FOUND = "Directory not found.";
    public static final String ERROR_PLAYERS_ONLY = "Only players may use this feature.";
    public static final String ERROR_IMPOSSIBLE = "§cNot sure what happened, but you shouldn't have reached this section of code. If you see this message, report it to Pangamma.";
    public static final String ERROR_TRY_AGAIN_LATER_COMMAND = "§cSorry, the system wasn't prepared for what you just did. Can you please try that again?";
    public static final String ERROR_TRY_AGAIN_LATER_CHAT = "§cHang on... try that chat message again.";
    public static final String MSG_PROCESSING = "§7Processing...";
    
    public static String ERROR_REPORT_THIS_TO_PANGAMMA(int errIdentifier){
        return "§cYou shouldn't be seeing this. Report this number to Pangamma: '" + errIdentifier + "'.";
    }

    public static String ERROR_NOT_INSTRUCTOR_FOR_CATEGORY(String letter){
        return "§cYou are not an instructor for this category! : " + letter;
    }

    /** Expects input to be pre-prepared to § color code format. 
     * Goes through the input string and removes all the color codes as needed. 
     Useful for doing stuff like checking if user has permissions to use certain
     color codes. Or maybe not for checking. But certainly for doing actions
     based on it. **/
    public static String removeColorCodes(String input,boolean canUseFormatCodes, boolean canUseBasicColors, boolean canUseBlack){
        String output = input;
        for(ChatColor cc : ChatColor.values()){
            if (cc.isColor() && !canUseBasicColors){
                output = output.replace(cc.toString().toUpperCase(), "");
                output = output.replace(cc.toString().toLowerCase(), "");
            }else if (cc.isFormat() && !canUseFormatCodes){
                output = output.replace(cc.toString().toUpperCase(), "");
                output = output.replace(cc.toString().toLowerCase(), "");
            }
        }
        if (!canUseBlack){
            output = output.replace("§0", "");
        }
        return output;
    }
    /** Makes sure color codes are replaced as needed.  **/
    public static String replaceColorCodes(String input,boolean canUseFormatCodes, boolean canUseBasicColors, boolean canUseBlack){
        String output = input;
        for(ChatColor cc : ChatColor.values()){
            if (cc.isColor() && !canUseBasicColors){
                output = output.replace(cc.toString().toUpperCase(), "");
                output = output.replace(cc.toString().toLowerCase(), "");
            }else if (cc.isFormat() && !canUseFormatCodes){
                output = output.replace(cc.toString().toUpperCase(), "");
                output = output.replace(cc.toString().toLowerCase(), "");
            }
        }
        if (!canUseBlack){
            output = output.replace("§0", "");
        }
        return output;
    }

    //<editor-fold defaultstate="collapsed" desc="permissions">
    public static enum PERMISSION {
        
        ALL("Skillsaw.*"),
        CHANNEL_ALL("Skillsaw.chat.*"),
        CHANNEL_GLOBAL("Skillsaw.chat.global"),
        CHANNEL_LIST("Skillsaw.chat.list"),
        CHANNEL_LIST_PRIVATE("Skillsaw.chat.list.private"),
        CHANNEL_STICKIES("Skillsaw.chat.stickies"),
        CHANNEL_STICKIES_INFINITE("Skillsaw.chat.stickies.infinite"),
        CHANNEL_INFO("Skillsaw.chat.info"),
        
        /** allows colors **/
        CHAT_COLOR_BASIC("Skillsaw.chat.color.basic"),
        CHAT_COLOR_BLACK("Skillsaw.chat.color.black"),
        /** Allows formatting codes. **/
        CHAT_COLOR_FORMATTNG("Skillsaw.chat.color.formatting"),
        
        /**
         * To send fireworks to people. *
         */
        CONGRATULATE("SkillSaw.congrats"),
        REP_NATURAL_INF("Skillsaw.nrep.override"),
        REP_NATURAL("Skillsaw.nrep"),
        REP_STAFF("Skillsaw.srep"),
        REP_FIX("Skillsaw.xrep"),
        REP_NOTE("Skillsaw.note"),
        
        REVIEW_LIST("Skillsaw.review.list"),
        REVIEW_THIS("Skillsaw.review.this"),
        REVIEW_REMOVE_SELF("Skillsaw.review.remove.self"),
        REVIEW_REMOVE_OTHERS("Skillsaw.review.remove.others"),
        
        /**
         * Allows someone to instruct in categories they have level 5 or higher
         * in.
         */
        INSTRUCT("SkillSaw.instruct"),
        
        /**
         * the node required to NOT require being level 5 or higher in a
         * category *
         */
        INSTRUCT_OVERRIDE("SkillSaw.instruct.override"),
        
        /**
         * /mee message *
         */
        MEE("Skillsaw.mee"),
        MUTE("Skillsaw.mute"),
        IGNORE("Skillsaw.ignore"),
        IGNORE_INF("Skillsaw.ignore.infinite"),
        
        /**
         * Basic nickname ability. Does not grant colors or anything special.
         */
        NICK_SELF("Skillsaw.nick.self"),
        
        /**
         * Allow player to change another person's nickname. It is assumed if
         * they have this power they will also have the other nick powers as
         * well because they are staff members.
         */
        NICK_OTHERS("Skillsaw.nick.others"),
        
        /**
         * Allows hearts and stars and stuff. 
         */
        NICK_STYLE_SPECIAL_CHARS("Skillsaw.nick.style.special_chars"),
        
        
        /**
         * All color codes for the nicknames. 
         */
        NICK_STYLE_COLORS("Skillsaw.nick.style.colors"),
        
        /**
         * All color codes for the nicknames. 
         */
        NICK_STYLE_COLOR_BLACK("Skillsaw.nick.style.colorblack"),
        
        /**
         * Formatting codes.
         */
        NICK_STYLE_FORMATTING("Skillsaw.nick.style.formatting"),
        
        STAFF_LIST("Skillsaw.staff.list"),
        
        /** For adding/removing from staff list */
        STAFF_MODIFY("Skillsaw.staff.modify"),        
        INSTRUCTORS_MODIFY("Skillsaw.instructors.modify"),
        INSTRUCTORS_LIST("Skillsaw.instructors.list"),

        TITLE_SET_SELF("Skillsaw.title.set.self"),
        TITLE_SET_OTHERS("Skillsaw.title.set.others"),
        TITLE_EDIT_ANY("SkillSaw.title.edit.*"),
        TITLE_EDIT_SPECIFIC("SkillSaw.title.edit"),
        
        VIEWLOGS_STAFF_REP("SkillSaw.viewlogs.staffrep"),
        VIEWLOGS_NATURAL_REP("SkillSaw.viewlogs.naturalrep"),
        VIEWLOGS_NOTE("SkillSaw.viewlogs.note"),
        VIEWLOGS_REP_FIX("SkillSaw.viewlogs.xrep"),
        CUSTOM_TITLES("Skillsaw.customtitles");

        //<editor-fold defaultstate="collapsed" desc="methods">
        
        public String node = "SkillSaw.*";

        /**
         * returns the node.*
         */
        @Override
        public String toString(){
            return node;
        }

        private PERMISSION(String node){
            this.node = node;
        }
        
        //</editor-fold>
    }
    
    public static String TELL_USER_LEVEL_THEY_LACK(int lvl){
        return ChatColor.RED + "Sorry, it seems that you need to have a total rep level of " + lvl + " or higher to be able to use this command.";
    }
            
    public static String TELL_USER_PERMISSION_THEY_LACK(PERMISSION node)
    {
        return TELL_USER_PERMISSION_THEY_LACK(node.node);
    }
    
    public static String TELL_USER_PERMISSION_THEY_LACK(String node){    
        return ChatColor.RED + "Oh teh noes! D: It appears you lack the '" + node + "' permission node that is required to perform this operation.";
    }
    
    /** Tells user if they lack permissions.
     * @param cs
     * @param node
     * @return  **/
    public static boolean USER_HAS_PERMISSION(CommandSender cs, PERMISSION node){
        return USER_HAS_PERMISSION(cs,node,true);
    }
    
    public static boolean USER_HAS_PERMISSION(CommandSender cs, PERMISSION node, boolean tellIfLacking){
        
        if (cs.isOp()){
            return true;
        }
        
        if (cs.hasPermission(node.node)){
            return true;
        }
        
        String[] args = node.node.split("\\.");
        if (args.length > 0){
            String perm = "";
            for (int i = 0; i < args.length - 1; i++){
                if (i > 0){
                    perm += "." + args[i];
                }
                else{
                    perm = args[i];
                }
                if (cs.hasPermission(perm + ".*")){
                    return true;
                }
            }
        }

        if (tellIfLacking){
            cs.sendMessage(ChatColor.RED + "Oh teh noes! D: It appears you lack the '" + node + "' permission node that is required to perform this operation.");
        }

        return false;
    }
    //</editor-fold>
    
    /**
     * returns null if display name or regular name are not found. within online
     players list.
     */
    public static Player getPlayer(String name){
        name = name.toLowerCase();
        Player p = Bukkit.getPlayer(name);
        if (p != null && p.isOnline()){
            return p;
        }
        for (Player n : Bukkit.getOnlinePlayers()){
            String nick = ChatColor.stripColor(n.getDisplayName()).toLowerCase();
            if (nick.contains(name)){
                return n;
            }
        }
        return null;
    }

    /**
     * [0]    days [1]    hours [2]    minutes [3]    seconds [4]    millis
     *
     * @param ms
     */
    public static long[] getTimeParts(long ms){
        long[] t = new long[5];
        t[0] = TimeUnit.MILLISECONDS.toDays(ms);
        t[1] = TimeUnit.MILLISECONDS.toHours(ms - TimeUnit.DAYS.toMillis(t[0]));
        t[2] = TimeUnit.MILLISECONDS.toMinutes(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]));
        t[3] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]));
        t[4] = TimeUnit.MILLISECONDS.toSeconds(ms - TimeUnit.DAYS.toMillis(t[0]) - TimeUnit.HOURS.toMillis(t[1]) - TimeUnit.MINUTES.toMillis(t[2]) - TimeUnit.SECONDS.toMillis(t[3]));
        return t;
    }
    
    /** 
     * '1d 5h 23m 22s'
     * @param ms
     * @return 
     */
    public static String getTimePartsString(long ms){
        long[] timeParts = getTimeParts(ms);
        String s = "";
        s += timeParts[0]+"d ";
        s += timeParts[1]+"h ";
        s += timeParts[2]+"m ";
        s += timeParts[3]+"s";
        return s;
    }

    @Deprecated
    /**
     * returns null if display name or regular name are not found. *
     */
    public static OfflinePlayer getOfflinePlayer(String name){
        name = name.toLowerCase();
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.hasPlayedBefore()){
            return op;
        }
        OfflinePlayer[] list = Bukkit.getOfflinePlayers();
        for (int i = 0; i < list.length; i++){
            op = list[i];
            if (op.getName().toLowerCase().startsWith(name)){
                return op;
            }
        }
        return null;
    }
    
    @Deprecated
    /**
     * Safely encodes any utf16 chars as \\u00FF
     *
     * @param s
     * @return
     */
    public static String makeSafe(String s){
        StringBuilder sb = new StringBuilder();
        if (s != null){
            for (char c : s.toCharArray()){
                if (c > '\u00FF'){
                    sb.append("\\u").append(String.format("%4x", (int) c));
                }
                else{
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
    private static final Pattern unicodePattern = Pattern.compile(".*(\\\\u[A-Fa-f0-9]{4}).*");
    
    @Deprecated
    public static String makeUnsafe(String s){

        Matcher matcher = unicodePattern.matcher(s);

        char utf16Char = '\0';    // null 
        String toReplace = "";
        while (matcher.matches()){
            toReplace = matcher.group(1);
            utf16Char = (char) Integer.parseInt(toReplace.substring(2), 16);
            s = s.replace(toReplace, "" + utf16Char);
            matcher = unicodePattern.matcher(s);
        }
        return s;
    }
    
    @Deprecated
    /**
     * @deprecated
     */
    public static boolean isSafeUTF8(String input){
        input = input
                .replace("]", "")
                .replace("[", "")
                .replace(")", "")
                .replace("(", "")
                .replace("}", "")
                .replace("{", "")
                .replace("?", "")
                .replace("\\", "")
                .replace("/", "")
                .replace("<", "")
                .replace(">", "")
                .replace(".", "")
                .replace(",", "")
                .replace(":", "");
        return input.matches("(?i)(\\d|\\w|\\s|[!@#$%^§&*_\\-+=])*");
    }
    
    @Deprecated
    /** 
     * use this method instead: getFullNameIfOnlinePlayer
     * returns null if no player is found with X name *
     */
    public static String getFullNameOfPlayer(String partialName){
        Player p = getPlayer(partialName);
        if (p != null){
            return p.getName();
        }
        OfflinePlayer op = getOfflinePlayer(partialName);
        if (op != null){
            return op.getName();
        }
        return null;
    }
    
    /**
     * Searches online players by display name, username. If not findable, 
     * defaults to using the partial name given.
     */
    public static String getFullNameIfOnlinePlayer(String partialName){
        Player p = getPlayer(partialName);
        if (p != null){
            return p.getName();
        }else{
            return partialName;
        }
    }

    public static UUID getUUID(String username){
        return getUUID(username, true);
    }

    /**
     * Returns null on failure *
     */
    public static UUID getUUID(String username, boolean fetchFromLocalIfAvailable){
        if (username == null){
            return null;
        }
        UUID uuid = null;
        if (fetchFromLocalIfAvailable){
            Player p = STATIC.getPlayer(username);
            if (p != null){
                uuid = p.getUniqueId();
            }
            if (uuid == null){
                OfflinePlayer op = STATIC.getOfflinePlayer(username);
                if (op != null){
                    uuid = op.getUniqueId();
                }
            }
        }
        try{
            if (uuid == null){
                String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "minecraft");

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = (JSONObject) (new JSONParser().parse(response.toString()));
                String id = json.get("id").toString();
                String uuidStr = (id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
                uuid = UUID.fromString(uuidStr);
            }
        }
        catch (ProtocolException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (MalformedURLException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParseException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.WARNING, username + " <-- Failed to parse JSON response from Mojang. They probably do not have a current record for the username requested.");
        }
        catch (Exception ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uuid;
    }

    /**
     * Returns null on failure *
     */
    public static String getUsername(UUID uuid){
        return getUsername(uuid, true);
    }

    /**
     * Returns null on failure *
     */
    public static String getUsername(UUID uuid, boolean fetchFromLocalIfAvailable){
        String username = null;
        if (fetchFromLocalIfAvailable){
            Player p = Bukkit.getPlayer(uuid);
            if (p != null){
                username = p.getName();
            }
            else{
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op != null){
                    username = op.getName();
                }
            }
        }
        if (username == null){
            ArrayList<String> names = getNameHistory(uuid);
            if (!names.isEmpty()){
                username = names.get(names.size() - 1);
            }
        }
        return username;
    }

    public static ArrayList<String> getNameHistory(UUID uuid){
        ArrayList<String> output = new ArrayList<String>();
        if (uuid == null){
            return output;
        }
        try{
            String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
            System.out.println(url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "minecraft");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            String jsonStr = response.toString();
            System.out.println(jsonStr);
            JSONArray json = (JSONArray) (new JSONParser().parse(jsonStr));
            for (int i = 0; i < json.size(); i++){
                JSONObject jsonObj = (JSONObject) json.get(i);
                output.add(jsonObj.get("name").toString());
            }

        }
        catch (MalformedURLException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ProtocolException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParseException ex){
            Logger.getLogger(STATIC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
