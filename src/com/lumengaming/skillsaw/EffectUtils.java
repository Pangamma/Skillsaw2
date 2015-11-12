package com.lumengaming.skillsaw;

import java.util.ArrayList;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;


public class EffectUtils {
	
	/** If player is null, no action will be performed. **/
	public static void playLevelUpEffect(Player p,String subTitle, String sendInChat){
		if (p == null){ return;}
		EffectUtils.playLevelUpEffect(p, subTitle);
		p.sendMessage(sendInChat);
	}
	
	/** If player is null, no action will be performed. **/
	public static void playLevelUpEffect(Player p,String subTitle){
		if (p == null){ return;}
		p.sendTitle("§2Level-Up!", "§7"+subTitle);
		p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
		p.playSound(p.getLocation(), "levelup",1F,0F);
		
		FireworkEffect fwe = org.bukkit.FireworkEffect.builder().withColor(Color.SILVER).withColor(Color.RED).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withColor(Color.YELLOW).build();
		ArrayList<Firework> fwList = new ArrayList<>();
		fwList.add(p.getWorld().spawn(p.getLocation(), Firework.class));
		fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, -1), Firework.class));
		fwList.add(p.getWorld().spawn(p.getLocation().subtract(1, 0, 1), Firework.class));
		fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, 1), Firework.class));
		fwList.add(p.getWorld().spawn(p.getLocation().subtract(-1, 0, -1), Firework.class));
		fwList.add(p.getWorld().spawn(p.getLocation().subtract(0, 1, 0), Firework.class));
		for (Firework fw : fwList){
			FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
			data.addEffects(fwe);
			data.setPower(0);
			fw.setFireworkMeta(data);
		}
	}

	public static void playVillagerSound(Player p){
		p.playSound(p.getLocation(), Sound.VILLAGER_IDLE,1F,1F);
	}

	public static void playLevelDownEffect(Player p, String subTitle){
		if (p == null){ return;}
		p.sendTitle("§4Level-Down", "§7"+subTitle);
		p.playSound(p.getLocation(), "leveldown",1F,1F);
		p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
	}

	public static void playLevelDownEffect(Player p, String subTitle, String sendInChat){
		if (p == null){ return;}
		playLevelDownEffect(p, subTitle);
		p.sendMessage(sendInChat);
	}
}
