package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {

    private final Main plugin;

    public IgnoreCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){

        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.IGNORE)){
            return true;
        }
        
        if (cs instanceof Player){
            Player p = (Player) cs;
            try{
                DataService system = plugin.getDataService();
                User cp = system.getUser(p.getUniqueId());
                if (cp == null){
                    cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
                    return true;
                }
                
                if ("+".equals(args[0])){
                    int size = cp.getIgnored().size();
                    if (size >= 10){
                        cs.sendMessage("§cMax of 10 ignored players at a time unless you have the '"+STATIC.PERMISSION.IGNORE_INF+"' permission node. Consider removing some names from your ignore list.");
                        return true;
                    }else{
                        Player target = STATIC.getPlayer(args[1]);
                        if (target != null){
                            cp.getIgnored().add(target.getName().toLowerCase());
                            cs.sendMessage("§aAdded "+target.getName().toLowerCase()+" to your ignore list.");
                            plugin.getDataService().saveUser(cp);
                        }else{
                            cp.getIgnored().add(args[1].toLowerCase());
                            cs.sendMessage("§aAdded "+args[1].toLowerCase()+" to your ignore list.");                            
                            plugin.getDataService().saveUser(cp);
                        }
                    }
                }
                else 
                    if ("-".equals(args[0])){
                    Player target = STATIC.getPlayer(args[1]);
                    if (target != null){
                        cp.getIgnored().remove(target.getName().toLowerCase());
                        cs.sendMessage("§aRemoved "+target.getName().toLowerCase()+" from your ignore list.");
                        plugin.getDataService().saveUser(cp);
                    }else{
                        cp.getIgnored().remove(args[1]);
                        cs.sendMessage("§aRemoved "+args[1].toLowerCase()+" from your ignore list.");
                        plugin.getDataService().saveUser(cp);
                    }
                }
                else if ("*".equals(args[0])){
                    cp.getIgnored().add("*");
                    cs.sendMessage("§aIgnoring all players now.");
                    plugin.getDataService().saveUser(cp);
                }
                else if ("!*".equals(args[0])){
                    cp.getIgnored().clear();
                    cs.sendMessage("§aNo longer ignoring anyone.");
                    plugin.getDataService().saveUser(cp);
                }
                else if ("?".equals(args[0])){
                    Player.Spigot sp = p.spigot();
                    p.sendMessage(STATIC.C_DIV_LINE);
                    p.sendMessage(STATIC.C_DIV_TITLE_PREFIX+" Ignore List");
                    p.sendMessage(STATIC.C_DIV_LINE);
                    for(String name : cp.getIgnored()){
                        BaseComponent[] txt = CText.legacy(STATIC.C_MENU_CONTENT +name);
                        CText.applyEvent(txt, new HoverEvent(HoverEvent.Action.SHOW_TEXT, CText.legacy("Click to remove.")));
                        CText.applyEvent(txt, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ignore - "+name));
                        sp.sendMessage(txt);
                    }
                    p.sendMessage(STATIC.C_DIV_LINE);
                }
                else{
                    printHelp(p);
                }

            }
            catch (ArrayIndexOutOfBoundsException ex){
                printHelp(p);
            }
        }
        else{
            cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
        }
        return true;
    }

    private void printHelp(Player p){
        Player.Spigot sp = p.spigot();
        sp.sendMessage(CText.hoverText("§c/ignore + <player>", "Add player name to\nyour ignore list."));
        sp.sendMessage(CText.hoverText("§c/ignore - <player>", "Remove player name from\nyour ignore list."));
        sp.sendMessage(CText.hoverText("§c/ignore *", "Ignore everyone."));
        sp.sendMessage(CText.hoverText("§c/ignore !*", "Clear your ignore list.\n(Ignore no one)"));
        sp.sendMessage(CText.hoverText("§c/ignore ?", "Show the people in\nyour ignore list."));
    }

}
