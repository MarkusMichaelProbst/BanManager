package net.MarkusTieger.network.Ban;

import java.sql.SQLException;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {
	
	@EventHandler
	public void onJoin(LoginEvent e) {
		
		try {
			if(BanManager.isBanned(e.getConnection().getUniqueId())) {
				e.getConnection().disconnect(BanManager.encode(BanManager.getReason(e.getConnection().getUniqueId()), BanManager.getLength(e.getConnection().getUniqueId())));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
