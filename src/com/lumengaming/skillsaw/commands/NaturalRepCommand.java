package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.RepLogEntry;
import com.lumengaming.skillsaw.model.RepType;
import com.lumengaming.skillsaw.model.User;
import com.lumengaming.skillsaw.service.DataService;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NaturalRepCommand extends IRepCommand{

    public NaturalRepCommand(Main plugin){
        super(plugin);
    }

    @Override
    protected void printHelp(CommandSender cs){
        cs.sendMessage("§c/rep <target>");
        cs.sendMessage("§c/rep <target> <amount> <reason>");
    }

    @Override
    protected boolean doRep(CommandSender cs, final User target, double amount, final String reason){
        DataService ds = plugin.getDataService();
        
        if (!(cs instanceof Player)){
            cs.sendMessage(STATIC.ERROR_PLAYERS_ONLY);
            return false;
        }
        
        final User issuer = ds.getUser(cs.getName());
        if (issuer == null){
            cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
            return true;
        }
        
        // Permissions?
        if (STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_NATURAL,false) || issuer.getRepLevel() >= 2){
            if (issuer.getName().equalsIgnoreCase(target.getName()) && issuer.getName().equalsIgnoreCase("Pangamma")){
                cs.sendMessage("§cYou cannot rep yourself.");
                return false;
            }
            
            cs.sendMessage("§7Processing...");
            
            long timePeriodCutoff = System.currentTimeMillis() - (plugin.getConfigHandler().getHoursPerRepTimePeriod()*3600000);
            int maxReps = plugin.getConfigHandler().getMaxNaturalRepsPerTimePeriod();
            
            ds.getLogEntriesByIssuer(RepType.NaturalRep,issuer.getUuid(),2000,timePeriodCutoff,
            (ArrayList<RepLogEntry> logEntries) -> {
                
                RepLogEntry oldest = null;
                RepLogEntry newest = null;
                RepLogEntry targeted = null;
                
                for(int i = 0; i < logEntries.size();i++){
                    RepLogEntry e = logEntries.get(i);
                    if (e.getTargetName().equalsIgnoreCase(target.getName())){
                        targeted = e;
                    }
                    
                    if (oldest == null){
                        oldest = e;
                    }else if (e.getTime().before(oldest.getTime())){
                        oldest = e;
                    }
                    
                    if (newest == null){
                        newest = e;
                    }else if (e.getTime().after(newest.getTime())){
                        newest = e;
                    }
                }
                
                if (targeted != null && !STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_NATURAL_INF,false)){
                    long timeToWait = targeted.getTime().getTime() - timePeriodCutoff;
                    String timeStr = STATIC.getTimePartsString(timeToWait);
                    cs.sendMessage("§cYou must wait §4"+timeStr+"§c before you can rep this person again. Can only rep someone once within a §4"+plugin.getConfigHandler().getHoursPerRepTimePeriod()+"§c hour time period.");
                }else if (logEntries.size() >= maxReps && !STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.REP_NATURAL_INF,false)){
                    long timeToWait = oldest.getTime().getTime() - timePeriodCutoff;
                    String timeStr = STATIC.getTimePartsString(timeToWait);
                    cs.sendMessage("§cYou must wait §4"+timeStr+"§c before you can rep again. Limit of §4"+maxReps+"§c per §4"+plugin.getConfigHandler().getHoursPerRepTimePeriod()+"§c hour time period.");
                }else{
                    final double fAmount = target.addNaturalRep(amount, issuer);
                    double reward = getRewardForRepping(fAmount);
                    issuer.addNaturalRep(reward,issuer);
                    
                    plugin.getDataService().saveUser(target);
                    plugin.getDataService().saveUser(issuer);
                    
                    if (target.p() != null){
                        String format = plugin.getConfigHandler().getNRepFormatToTarget();
                        format = plugin.getConfigHandler().compileMessageFormat(format,cs.getName(),  target.getName(), fAmount, reason);
                        target.p().sendMessage(format);
                    }

                    String format = plugin.getConfigHandler().getNRepFormatToIssuer();
                    format = plugin.getConfigHandler().compileMessageFormat(format, cs.getName(),  target.getName(), fAmount, reason);
                    cs.sendMessage(format);
                    cs.sendMessage("§aYou've also been awarded "+reward + " rep points for your contribution. :)");

                    dh.logRep(issuer,target,fAmount, RepType.NaturalRep, reason);
                }
            });
        }else{
            cs.sendMessage(STATIC.TELL_USER_PERMISSION_THEY_LACK(STATIC.PERMISSION.REP_NATURAL));
            return false;
        }
        return true;
    
    
    }
    
    //<editor-fold defaultstate="collapsed" desc="reward for repping">
    /** Rounded to a decimal place. **/
    public double getRewardForRepping(double initialAmount){
        double n = initialAmount / 10;
        if (n < 0){
            n *= 2;
            if (n < -10){
                n = -10;
            }
        }
        else if (n > 0 && n < 0.1){
            n = 0.1;
        }
        else if (n > 5){
            n = 5;
        }
        return User.round(n);
    }
    //</editor-fold>

}
