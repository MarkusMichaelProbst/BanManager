package net.MarkusTieger.network.Ban;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.MarkusTieger.network.Ban.commands.CMDBan;
import net.MarkusTieger.network.Ban.commands.CMDCheck;
import net.MarkusTieger.network.Ban.commands.CMDUnban;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BanManager extends Plugin {
	
	private static String msg;
	public static List<String> banlist;
	public static Configuration config;

	@Override
	public void onEnable() {
		
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinListener());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CMDBan());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CMDUnban());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CMDCheck());
		
		String user = null, pwd = null, database = null, host = null;
		int port = 3306;
		try {
			if(!getDataFolder().exists()) {
				getDataFolder().mkdir();
			}
			File file = new File(getDataFolder(), "config.yml");
			if(!file.exists()) {
				
				file.createNewFile();
			
			}
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
			if(!config.contains("MySQL.User")) {
				config.set("MySQL.User", "root");
			}
			
			if(!config.contains("MySQL.Password")) {
				config.set("MySQL.Password", "");
			}
			
			if(!config.contains("MySQL.Host")) {
				config.set("MySQL.Host", "localhost");
			}
			
			if(!config.contains("MySQL.Port")) {
				config.set("MySQL.Port", 3306);
			}
			
			if(!config.contains("MySQL.Database")) {
				config.set("MySQL.Database", "BanManager");
			}
			
			if(!config.contains("BanMessage")) {
				config.set("BanMessage", "You are Banned From the Server\nReason: %Message%\nLength: %length%");
			}
			
			if(!config.contains("BanList")) {
				banlist = new ArrayList<String>();
				banlist.add("Hacking");
				config.set("BanList", banlist);
			} else {
				banlist = config.getStringList("BanList");
			}
			
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
			
			
			
			msg = config.getString("BanMessage");
			port = config.getInt("MySQL.Port");
			database = config.getString("MySQL.Database");
			host = config.getString("MySQL.Host");
			pwd = config.getString("MySQL.Password");
			user = config.getString("MySQL.User");
			this.config = config;
			System.out.println("Config Loaded and Saved!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		System.out.println("[MySQL] Connecting");
		
		MySQL.update(host, port, database, user, pwd);
		MySQL.connect();
		
		try {
			if(MySQL.isConnected()) {
				System.out.println("[MySQL] Connected");
				PreparedStatement statement = MySQL.prepareStatement("CREATE TABLE IF NOT EXISTS playerbanned (UUID VARCHAR(100),Player VARCHAR(100),Reason VARCHAR(100),Length INT(100))");
				statement.executeUpdate();
				
				
			}
		} catch (SQLException e) {
			
		}
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static String encode(String reason, int length) {
		System.out.println("Encode: " + length);
		int day = getDays();
		System.out.println(day);
		int days = (length - day);
		return msg.replaceAll("%Message%", reason).replaceAll("%length%", (length == 0 ? "§4Permanent§f" : ("§6" + days + (days == 1 ? " Day" : " Days") + "§f"))).replace("&", "§");
	}
	
	private static int getDays() {
		Date date = new Date();
		return (date.getDay() + (date.getMonth()  * 30) + ((date.getYear() + 1900) * 365));
	}
	
	public static void unban(UUID uuid) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("DELETE FROM playerbanned WHERE UUID = ?");
		statement.setString(1, uuid.toString());
		statement.executeUpdate();
	}
	
	public static void unban(String player) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("DELETE FROM playerbanned WHERE Player = ?");
		statement.setString(1, player);
		statement.executeUpdate();
	}
	
	public static String getReason(ProxiedPlayer pp) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, pp.getUniqueId().toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			return result.getString("Reason");
		}
		return "Banned By Operator";
	}
	
	public static String getReason(UUID uuid) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, uuid.toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			return result.getString("Reason");
		}
		return "Banned By Operator";
	}
	
	public static void check(String target, CommandSender sender) {
		try {
			if(isBanned(target)) {
				PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE Player = ?");
				statement.setString(1, target);
				ResultSet result = statement.executeQuery();
				
				int length = 0;
				String reason = "";
				String uuid = "";
				
				while(result.next()) {
					
					length = result.getInt("Length");
					reason = result.getString("Reason");
					uuid = result.getString("UUID");
					break;
				}
				
				sender.sendMessage(ChatColor.GOLD + "Infos von " + target + "\nStatus: Banned\nLenght: " + (length == 0 ? "Permanent" : ("" + (length - getDays()))) + "\nUUID: " + uuid + "\nReason: " + reason.replace("&", "§"));
				
			} else {
				sender.sendMessage(ChatColor.GOLD + "Infos von " + target + "\nStatus: Unbanned");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isBanned(ProxiedPlayer pp) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, pp.getUniqueId().toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			int length = result.getInt("Length");
			System.out.println("Checking... " + length);
			if(length == 0) {
				return true;
			}
			if(getDays() >= length) {
				unban(pp.getUniqueId());
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBanned(String player) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE Player = ?");
		statement.setString(1, player);
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			int length = result.getInt("Length");
			if(length == 0) {
				return true;
			}
			if(getDays() >= length) {
				unban(player);
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBanned(UUID uuid) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, uuid.toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			int length = result.getInt("Length");
			if(length == 0) {
				return true;
			}
			if(getDays() >= length) {
				unban(uuid);
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public static boolean banPlayer(ProxiedPlayer pp, String reason, int days) throws SQLException {
		int day = getDays();
		System.out.println(day);
		if(pp == null) {
			System.out.println("Player Offline!");
			return false;
		}
		int length = (days == 0 ? 0 : (getDays() + days));
		
		System.out.println(length);
		PreparedStatement statement = MySQL.prepareStatement("INSERT INTO playerbanned (UUID,Player,Reason,Length) VALUES (?,?,?,?)");
		statement.setString(1, pp.getUniqueId().toString());
		statement.setString(2, pp.getName());
		statement.setString(3, reason);
		statement.setInt(4, length);
		statement.executeUpdate();
		
		pp.disconnect(encode(reason, days == 0 ? 0 : getDays() + days));
		return true;
	}

	public static int getLength(ProxiedPlayer pp) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, pp.getUniqueId().toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			return result.getInt("Length");
		}
		return 0;
	}
	
	public static int getLength(UUID uuid) throws SQLException {
		PreparedStatement statement = MySQL.prepareStatement("SELECT * FROM playerbanned WHERE UUID = ?");
		statement.setString(1, uuid.toString());
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			return result.getInt("Length");
		}
		return 0;
	}
	
}
