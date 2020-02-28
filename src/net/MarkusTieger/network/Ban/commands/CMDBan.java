package net.MarkusTieger.network.Ban.commands;

import java.sql.SQLException;

import net.MarkusTieger.network.Ban.BanManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDBan extends Command {

	public CMDBan() {
		super("ban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("/ban [Player] [Reason] (Show all Reasons use /ban list)");
			sender.sendMessage("/ban [Player] [Days] [Reason...] (Custom Reason)");
			sender.sendMessage("Permissions: server.ban");
		}
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				System.out.println("List: ");
				for(String str : BanManager.banlist) {
					sender.sendMessage(str + ": Length: " + BanManager.config.getInt(str + ".length") + " Reason: " + BanManager.config.getString(str + ".reason"));
				}
			}
		}
		if(args.length == 2) {
			if(sender.hasPermission("server.ban")) {
			
			String target = args[0];
			String name = args[1];
			try {
				if(BanManager.banPlayer(ProxyServer.getInstance().getPlayer(target), BanManager.config.getString(name + ".reason"), BanManager.config.getInt(name + ".length"))) {
					sender.sendMessage("Successfully Banned Player");
					System.out.println(sender.getName() + " has Banned " + target);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sender.sendMessage("MySQL Error Can't Ban Player");
			}
			} else {
				sender.sendMessage("You have not the Permission to use the Command");
				return;
			}
		}
		if(args.length >= 3) {
			if(sender.hasPermission("server.ban")) {
				ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args[0]);
				int length = Integer.parseInt(args[1]);
				
				String reason = "";
				for(int i = 2; i < args.length; i++) {
					if(reason == "") {
						reason = args[i];
					} else {
						reason += " " + args[i];
					}
				}
				try {
					if(BanManager.banPlayer(pp, reason, length)) {
						sender.sendMessage("Successfully Banned Player");
						System.out.println(sender.getName() + " has Banned " + pp.getName());
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sender.sendMessage("MySQL Error Can't Ban Player");
				}
				
			} else {
				sender.sendMessage("You have not the Permission to use the Command");
			}
		}
		
	}

}
