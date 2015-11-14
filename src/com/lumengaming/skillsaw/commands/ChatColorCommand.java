package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand implements CommandExecutor {

    private final Main plugin;

    public ChatColorCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
        boolean hasColorBasic = STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHAT_COLOR_BASIC, false);
        boolean hasColorFormatting = STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHAT_COLOR_FORMATTNG, false);
        boolean hasColorBlack = STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHAT_COLOR_BLACK, false);

        if (!(hasColorBasic || hasColorFormatting)){
            cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.CHAT_COLOR_BASIC.node + "' or '" + STATIC.PERMISSION.CHAT_COLOR_FORMATTNG.node));
            return false;
        }

        if (!(cs instanceof Player)){
            cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
            return false;
        }

        Player p = (Player) cs;
        User user = plugin.getDataService().getUser(cs.getName());
        if (user == null){
            cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
            return true;
        }

        try{
            String prefix = args[0].replace('&', '§');
            if (!ChatColor.stripColor(prefix).replace("§", "").isEmpty()){
                cs.sendMessage("§cColor codes only. Thanks.");
            }
            prefix = STATIC.removeColorCodes(prefix, hasColorFormatting, hasColorBasic, hasColorBlack);

            user.setChatColor(prefix);
            cs.sendMessage("§aChanged your chat color.");
            plugin.getDataService().saveUser(user);

        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException ex){
            printHelp(cs);
        }
        return true;
    }

    private void printHelp(CommandSender cs){
        cs.sendMessage("§c/chatcolor &2");
        if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.CHAT_COLOR_FORMATTNG)){
            cs.sendMessage("§c/chatcolor &2&n");
            cs.sendMessage("§c/chatcolor &2&L");
            cs.sendMessage("§c/chatcolor &L");
        }
    }

}
