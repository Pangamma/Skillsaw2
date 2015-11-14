package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MeeCommand implements CommandExecutor{

    private final Main plugin;

    public MeeCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args){
        
        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.MEE)){
            return true;
        }
        
        if (cs instanceof Player){
            Player p = (Player) cs;
            String msg = String.join(" ",args);
            User u = plugin.getDataService().getUser(p.getUniqueId());
            if (u == null){
                cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
                return true;
            }
            if (u.getSpeakingChannel().equalsIgnoreCase("1")){
                cs.sendMessage("§cDon't use /me on the main channel. Thanks!");
                return true;
            }
            BaseComponent[] txt = CText.merge(CText.legacy("§f* "),u.getNameForChat());
            txt = CText.merge(txt,CText.legacy(u.getChatColor()+" "+msg));
            
            User cp = plugin.getDataService().getUser(p.getUniqueId());
            plugin.getDataService().sendMessageToChannel(p.getName(),cp.getSpeakingChannel(),txt);
            
        }else{
            cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
        }
        return true;
    }

}
