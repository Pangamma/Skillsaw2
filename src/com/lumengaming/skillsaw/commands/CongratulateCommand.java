/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Taylor
 */
public class CongratulateCommand implements CommandExecutor{
	private final Main plugin;

	public CongratulateCommand(Main plug){
		this.plugin = plug;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
