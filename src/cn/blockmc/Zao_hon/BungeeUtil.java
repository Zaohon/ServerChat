package cn.blockmc.Zao_hon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeUtil {
	public static void sendServerChat(ServerChat plugin, Player p, String msg) {
		try {
			String servername = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("ThisServerName"));
			String name = p.getName();
			plugin.sendServerChat(servername, name, msg);
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ServerChat");
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF(servername);
			msgout.writeUTF(name);
			msgout.writeUTF(msg);
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
}
