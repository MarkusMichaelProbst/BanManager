package net.MarkusTieger.network.Ban.commands;

import java.sql.SQLException;

import net.MarkusTieger.network.Ban.BanManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDUnban extends Command {

	public CMDUnban() {
		super("unban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("/unban [Player]");
			sender.sendMessage("Permissions: server.unban");
		}
		if(sender.hasPermission("server.unban")) {
			if(args.length == 1) {
				try {
					BanManager.unban(args[0]);
					sender.sendMessage("Successfuly Unbanned Player");
					System.out.println(sender.getName() + " Unbanned " + args[0]);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sender.sendMessage("MySQL Error Can't unban");
				}
			}
		} else {
			sender.sendMessage("You have not the Permission to use the Command");
		}
		
	}

}
