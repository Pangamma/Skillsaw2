package com.lumengaming.skillsaw;

import com.lumengaming.skillsaw.STATIC.PERMISSION;
import com.lumengaming.skillsaw.commands.*;
import com.lumengaming.skillsaw.listeners.*;
import com.lumengaming.skillsaw.service.*;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.MuteService;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class Main extends JavaPlugin {

    private ConfigHandler config;
    private DataService dataService;
    private MuteService muteService;
	private MuteCommand muteCommand;

    @Override
    public void onEnable(){
        this.config = new ConfigHandler(this).load();
        this.muteService = new MuteService(this);
        this.dataService = this.config.getDataService();
        this.dataService.onEnable();

        getCommand("testlock").setExecutor(new TestCommand(this));
        getCommand("scavengerhunt").setExecutor(new ScavengerHuntCommand(this));
        getCommand("staff").setExecutor(new StaffCommand(this));
        getCommand("instructor").setExecutor(new InstructorCommand(this));
        getCommand("title").setExecutor(new TitleCommand(this));
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("chatcolor").setExecutor(new ChatColorCommand(this));
//        getCommand("congratulate").setExecutor(new CongratulateCommand(this));
        getCommand("global").setExecutor(new GlobalCommand(this));
        getCommand("review").setExecutor(new ReviewCommand(this));
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("mee").setExecutor(new MeeCommand(this));
		this.muteCommand = new MuteCommand(this);
        getCommand("mute").setExecutor(muteCommand);
        getCommand("unmute").setExecutor(muteCommand);
        getCommand("rep-natural").setExecutor(new NaturalRepCommand(this));
        getCommand("nick").setExecutor(new NickCommand(this));
        getCommand("rep-note").setExecutor(new NoteCommand(this));
        getCommand("setskill").setExecutor(new SetSkillCommand(this));
        getCommand("skillsaw").setExecutor(new SkillSawCommand(this));
        getCommand("rep-staff").setExecutor(new StaffRepCommand(this));
        getCommand("rep-fix").setExecutor(new XRepCommand(this));
        getCommand("replog").setExecutor(new ViewLogCommand(this));

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable(){
		HandlerList.unregisterAll(this);
        this.dataService.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args2){
		int n = 23;
		while(n % 9 != 0){
			System.out.println(n+"...");
			n++;
		}
    }

    public ConfigHandler getConfigHandler(){
        return this.config;
    }

    public DataService getDataService(){
        return this.dataService;
    }

    public MuteService getMuteService(){
        return muteService;
    }
	
    private void printSyntax(boolean hideIfNoPermission, CsWrapper cs, PERMISSION permRequired, String cmdSyntax, String hoverText){
        if (STATIC.USER_HAS_PERMISSION(cs.getCs(), permRequired, false)){
            BaseComponent[] txt = CText.hoverText(STATIC.C_MENU_CONTENT + cmdSyntax, hoverText);
            cs.sendMessage(txt);
        }
        else if (!hideIfNoPermission){
            cs.sendMessage(STATIC.C_MENU_CONTENT + "§c" + cmdSyntax);
        }
    }

    private void printSyntax(CsWrapper cs, PERMISSION permRequired, String cmdSyntax, String hoverText){
        printSyntax(false, cs, permRequired, cmdSyntax, hoverText);
    }

    private void printSyntax(CsWrapper cs, String cmdSyntax, String hoverText){
        BaseComponent[] txt = CText.hoverText(STATIC.C_MENU_CONTENT + cmdSyntax, hoverText);
        cs.sendMessage(txt);
    }

    public void printHelp(CommandSender csTmp){
        CsWrapper cs = new CsWrapper(csTmp);

        cs.sendMessage(STATIC.C_DIV_LINE);
        cs.sendMessage(STATIC.C_DIV_TITLE_PREFIX + this.getName());
        cs.sendMessage(STATIC.C_DIV_LINE);
        printSyntax(cs, "/skillsaw perms", "List all Skillsaw permissions.");
        printSyntax(cs, PERMISSION.ALL, "/skillsaw reload", "Reloads skillsaw.\nDoes a save and a load.");
        printSyntax(cs, PERMISSION.STAFF_LIST, "/staff list", "List staff members\nAlso shows time since\nlast login.");
        printSyntax(cs, PERMISSION.STAFF_MODIFY, "/staff + <player>", "Add staff\nMark a player as staff.");
        printSyntax(cs, PERMISSION.STAFF_MODIFY, "/staff - <player>", "Add staff\nUnmark a player as staff.");
        printSyntax(cs, PERMISSION.INSTRUCTORS_LIST, "/instr list", "List instructors\nAlso shows time since\nlast login and\ntheir skill tiers.");
        printSyntax(cs, PERMISSION.INSTRUCTORS_MODIFY, "/instr + <player>", "Add instructor\nMark a player as instructor.");
        printSyntax(cs, PERMISSION.INSTRUCTORS_MODIFY, "/instr - <player>", "Add instructor\nUnmark a player as instructor.");

        printSyntax(cs, PERMISSION.IGNORE, "/ignore + <player>", "Add player name to\nyour ignore list.");
        printSyntax(cs, PERMISSION.IGNORE, "/ignore - <player>", "Remove player name from\nyour ignore list.");
        printSyntax(cs, PERMISSION.IGNORE, "/ignore *", "Ignore everyone.");
        printSyntax(cs, PERMISSION.IGNORE, "/ignore !*", "Clear your ignore list.\n(Ignore no one)");
        printSyntax(cs, PERMISSION.IGNORE, "/ignore ?", "Show the people in\nyour ignore list.");

//        cs.sendMessage(("§c/ch:<channel> <message>"));
        printSyntax(cs, "/ch <channel>", "Set your active chat\nchannel to speak on.");
        printSyntax(cs, "/ch = <channel>", "Set your active chat\nchannel to speak on.");
        printSyntax(cs, PERMISSION.CHANNEL_LIST, "/ch -L", "List active chat channels.");
        printSyntax(cs, PERMISSION.CHANNEL_INFO, "/ch -P [player]", "Show channel info for\nthe selected player,\nor for yourself if\nthe player name is not\ngiven.");
        printSyntax(cs, PERMISSION.CHANNEL_INFO, "/ch -I [channel]", "Show channel info for\nthe selected channel, or\nfor your current channel\nif the channel name is not\ngiven.");
        printSyntax(cs, PERMISSION.CHANNEL_STICKIES, "/ch + <channel>", "Add a sticky channel.\nSticky channels are\nchannels you listen \nto even if you are not\tspeaking on them.");
        printSyntax(cs, PERMISSION.CHANNEL_STICKIES, "/ch - <channel>", "Remove a sticky channel.\nSticky channels are\nchannels you listen \nto even if you are not\tspeaking on them.");

        printSyntax(cs, PERMISSION.CHAT_COLOR_BASIC, "/chatcolor &2", "Set your default chat\ncolor to §2dark green.");
        printSyntax(cs, PERMISSION.CHAT_COLOR_FORMATTNG, "/chatcolor &L", "Set your default chat\nformat to be §lbold.");
        printSyntax(cs, "/chatcolor &2&n", "Set your default chat\nformat to be formatted\nand colored.");

        //congratulate
        //scold
        printSyntax(cs, PERMISSION.CHANNEL_GLOBAL, "/g <message>", "Broadcast a global message.");

        printSyntax(cs, PERMISSION.MEE, "/me <action message>", "Don't use this on the\nmain channels.");
        printSyntax(cs, PERMISSION.MUTE, "/mute <player>", "Mute for 5 minutes.");
        printSyntax(cs, PERMISSION.MUTE, "/unmute <player>", "Unmute the player.");
        printSyntax(cs, PERMISSION.MUTE, "/mute <player> -1", "Mute until the server reboots.");
        printSyntax(cs, PERMISSION.MUTE, "/softmute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages.");
        printSyntax(cs, PERMISSION.MUTE, "/smute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages.");

        printSyntax(cs, PERMISSION.REP_NATURAL, "/rep <player>", "See skillsaw info about\nthe selected player.");
        printSyntax(cs, PERMISSION.REP_NATURAL, "/rep <player> <amount> <reason>",
                "Give someone some rep for\n"
                + "the good job they've done.\n"
                + "The amount you can give is\n"
                + "capped by your own repping\n"
                + "power and their rep level.");
        printSyntax(true, cs, PERMISSION.REP_FIX, "/xrep <player> <amount> <reason>", "Fix their rep. Has no limits\non the amount given or\ntaken. DO NOT ABUSE THIS.");
        printSyntax(true, cs, PERMISSION.REP_FIX, "/srep <player> <amount> <reason>", "Give staff rep. Staff rep\nis about staff-ish behavior\npeople may be showing.");
        printSyntax(true, cs, PERMISSION.REP_NOTE, "/note <player> <message>", "Add a note about a player.\nPlayers are not notified about\nreceiving new notes. This is\na private thing only staff\nwill see.");

        cs.sendMessage(STATIC.C_MENU_CONTENT + "/review this");
        cs.sendMessage(STATIC.C_MENU_CONTENT + "/review list");
        cs.sendMessage(STATIC.C_MENU_CONTENT + "/review tp [name]");
        cs.sendMessage(STATIC.C_DIV_LINE);
    }
}
