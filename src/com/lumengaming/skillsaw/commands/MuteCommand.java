package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.CText;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.MutedPlayer;
import com.lumengaming.skillsaw.service.MuteService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private final Main plugin;

    public MuteCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
        if (!STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.MUTE)){
            return false;
        }

        cmdAlias = cmdAlias.toLowerCase();
        MuteService system = plugin.getMuteService();
        
        try{
            String name = args[0];
            Player p = STATIC.getPlayer(name);
            if (p != null){
                name = p.getName();
            }

            if ("smute".equals(cmdAlias) || "softmute".equals(cmdAlias)){
                long seconds = args.length == 2 ? Long.parseLong(args[1]) : 300;
                MutedPlayer mp = new MutedPlayer(name, true, seconds);
                system.addMutedPlayer(mp);
                if (p != null){
                    p.sendMessage("§7Shhhhh...");
                }
                cs.sendMessage("§a" + name + " was muted softly for " + mp.getTotalMuteTimeStr() + ". They will not be notified of being muted and they will still see their own messages as if nothing has happened.");
            }
            else if ("mute".equals(cmdAlias)){
                long seconds = args.length == 2 ? Long.parseLong(args[1]) : 300;
                MutedPlayer mp = new MutedPlayer(name, false, seconds);
                system.addMutedPlayer(mp);
                cs.sendMessage("§a" + name + " was muted for " + mp.getTotalMuteTimeStr() + ".");

                if (p != null){
                    if (seconds == -1){
                        p.sendMessage("§cMuted.");
                    }
                    else{
                        p.sendMessage("§cMuted for " + mp.getTotalMuteTimeStr() + ".");
                    }
                }
            }
			else {
                MutedPlayer mp = system.removeMutedPlayer(new MutedPlayer(name));
				if (mp != null){
					cs.sendMessage("§aUnmuted '" + name + "'.");
					if (p != null){
						if (!mp.isSoftMute()){
							p.sendMessage("§aUnmuted.");
						}else{
							p.sendMessage("§7You can talk again. :)");
						}
					}
				}else{
					cs.sendMessage("§cThat player isn't muted.");
				}
            }

        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException ex){
            printHelp(cs);
        }
        return true;
    }

    private void printHelp(CommandSender cs){
        if (cs instanceof Player){
            Player.Spigot ps = ((Player) cs).spigot();
            ps.sendMessage(CText.hoverText("§c/unmute <player>", "Unmute the player."));
            ps.sendMessage(CText.hoverText("§c/mute <player>", "Mute for 5 minutes."));
            ps.sendMessage(CText.hoverText("§c/mute <player> -1", "Mute until next server restart.\nSomething something\nsomething"));
            ps.sendMessage(CText.hoverText("§c/softmute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages."));
            ps.sendMessage(CText.hoverText("§c/smute <player> [# seconds]", "Soft mute.\nThe player will be the only\none seeing their messages."));
        }
        else{
            cs.sendMessage("/smute <player> [seconds]");
            cs.sendMessage("/mute <player> [seconds]");
            cs.sendMessage("/unmute <player>");
        }
    }

}
