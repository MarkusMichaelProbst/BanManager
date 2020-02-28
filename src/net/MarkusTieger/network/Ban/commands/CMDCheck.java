package net.MarkusTieger.network.Ban.commands;

import net.MarkusTieger.network.Ban.BanManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CMDCheck extends Command {

	public CMDCheck() {
		super("check");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("/check [Player]");
			sender.sendMessage("Permissions: []");
		}
		if(args.length == 1) {
			BanManager.check(args[0], sender);
		}
		
	}

}
