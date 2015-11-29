package com.lumengaming.skillsaw.commands;

import com.lumengaming.skillsaw.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class TestCommand implements CommandExecutor{

	private final Main plugin;
	public TestCommand(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String cmdAlias, String[] args){
		
		return true;
	}

}