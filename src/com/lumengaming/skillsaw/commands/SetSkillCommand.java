package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.EffectUtils;
import com.lumengaming.skillsaw.Main;
import com.lumengaming.skillsaw.STATIC;
import com.lumengaming.skillsaw.model.SkillType;
import com.lumengaming.skillsaw.model.User;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * @author Taylor
 */
public class SetSkillCommand implements CommandExecutor{
	private final Main plugin;
	public SetSkillCommand(Main plug){
		this.plugin = plug;
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
		if (args.length==1){
			//<editor-fold defaultstate="collapsed" desc="show data about...">
			cs.sendMessage(STATIC.MSG_PROCESSING);
            Player onlineP = STATIC.getPlayer(args[0]);
            String fName = (onlineP != null) ? onlineP.getName() : args[0];
			plugin.getDataService().getOfflineUser(fName,true,(User u) -> {
				if (u != null){
					u.showStatisticsTo(cs);
				}else{
					cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
				}
			});
			return true;
			//</editor-fold>
		}else{
			if (!STATIC.USER_HAS_PERMISSION(cs,STATIC.PERMISSION.INSTRUCT)){
				return false;
			}
			
			if (!(cs instanceof Player)){
				cs.sendMessage("Only players can set another player's skill level.");
				return true;
			}
			Player sender = (Player) cs;
			
			try{
				ArrayList<SkillType> sts = plugin.getConfigHandler().getSkillTypes();
				SkillType st = null;
				for(SkillType st2 : sts){
					if (st2.getListName().toLowerCase().startsWith(args[1].toLowerCase())){
						st = st2; break;
					}
				}
				if (st == null){
					cs.sendMessage("§cNo skilltype found that matched your request. Try something else.");
					return false;
				}
				final SkillType finalSt = st;
				final int nLevel = Integer.parseInt(args[2]);
				if (nLevel < st.getMinLevel() || nLevel > st.getMaxLevel()){
					cs.sendMessage("§cYou must specify a level between "+st.getMinLevel()+" and "+st.getMaxLevel()+".");
					return false;
				}
				
				User issuer = plugin.getDataService().getUser(cs.getName());			
				if (issuer == null){
					cs.sendMessage(STATIC.ERROR_TRY_AGAIN_LATER_COMMAND);
					return false;
				}
				if (nLevel > 3 && issuer.getSkill(st) < st.getMinInstructLevel() && !STATIC.USER_HAS_PERMISSION(cs, STATIC.PERMISSION.INSTRUCT_OVERRIDE)){
					cs.sendMessage(STATIC.C_ERROR + "You cannot promote people in §4" + st.getListName() + STATIC.C_ERROR + " until your skill level in that "
							+ "category is §4"+st.getMinInstructLevel()+"§c or higher.");return true;
				}
				
				//<editor-fold defaultstate="collapsed" desc="user">
				cs.sendMessage(STATIC.MSG_PROCESSING);
                
                Player onlineP = STATIC.getPlayer(args[0]);
                String fName = (onlineP != null) ? onlineP.getName() : args[0];
				plugin.getDataService().getOfflineUser(fName,true,(User target) -> {
					
					if (target == null) { 
						cs.sendMessage(STATIC.ERROR_P_NOT_FOUND);
						return; 
					}
					
					if (target.getName().equalsIgnoreCase(cs.getName())){
						cs.sendMessage(STATIC.C_ERROR+"You cannot change your own level.");
						return;
					}
					int oLevel = target.getSkill(finalSt);
					
					if (oLevel != nLevel){
						Player targetP = Bukkit.getPlayer(target.getUuid());
						playEffectsIfLevelChange(targetP, oLevel,nLevel, finalSt.getListName());
						target.setSkill(finalSt, nLevel);	
						plugin.getDataService().logPromotion(issuer,target,finalSt,oLevel,nLevel,sender.getLocation());
						plugin.getDataService().saveUser(target);
					 }
					cs.sendMessage(STATIC.C_SUCCESS+"Set "+target.getName()+"'s §2"+finalSt.getListName()+"§a skill to level "+ nLevel+".");
				
				});
				//</editor-fold>
				
			}catch(ArrayIndexOutOfBoundsException ex){
				plugin.printHelp(cs);
			}
			return true;
		}
	}
	
	/** player if valid, original skill level, and new level. **/
	private void playEffectsIfLevelChange(Player p, int oSkill, int level,String skillName) {
		if (oSkill < level){
			if (p!=null && p.isValid()){
				EffectUtils.playLevelUpEffect(p, "§7"+skillName+" tier increased", "§aYour §2"+skillName+"§a skill level has increased!");
			}
		}else if (oSkill > level){
			if (p!=null && p.isValid()){
				EffectUtils.playLevelDownEffect(p, "§7"+skillName+" tier decreased","§cYour §4"+skillName+"§c skill level has decreased.");
			}
		}
	}
}
