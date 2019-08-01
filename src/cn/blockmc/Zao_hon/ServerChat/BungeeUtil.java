package cn.blockmc.Zao_hon.ServerChat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cn.blockmc.Zao_hon.ServerChat.configuration.Config;

public class BungeeUtil {
	public static void sendServerChat(ServerChat plugin, CommandSender sender, String msg) {
		try {
			String servername = ChatColor.translateAlternateColorCodes('&', Config.THIS_SERVER_NAME);
			String name = sender instanceof Player ? sender.getName() : "Server";
			plugin.sendServerChat(servername, name, msg);
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ServerChat");
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			//
			msgout.writeLong(System.currentTimeMillis());
			//
			msgout.writeUTF(servername);
			msgout.writeUTF(name);
			msgout.writeUTF(msg);
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
