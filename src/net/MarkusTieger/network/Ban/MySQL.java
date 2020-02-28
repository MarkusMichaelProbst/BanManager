package net.MarkusTieger.network.Ban;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {
	
	private static Connection con;
	private static String pwd;
	private static String host;
	private static int port;
	private static String user;
	private static String database;
	
	public static PreparedStatement prepareStatement(String sql) {
		try {
			return con.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void update(String host, int port, String database, String user, String pwd) {
		MySQL.host = host;
		MySQL.pwd = pwd;
		MySQL.user = user;
		MySQL.port = port;
		MySQL.database = database;
	}
	
	public static void connect() {
		if(!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pwd);
				System.out.println("[MySQL] Connected");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isConnected() {
		return con != null;
	}
	
	public static void close() {
		if(isConnected()) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
