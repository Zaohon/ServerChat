package cn.blockmc.Zao_hon.ServerChat.Utils;

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

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class BungeeUtil {
	private static ServerChat plugin;

	public static void init(ServerChat plugin) {
		BungeeUtil.plugin = plugin;
	}

	public static void sendServerChat(ServerChat plugin, CommandSender sender, String msg) {
		String senderName = sender instanceof Player ? sender.getName() : "Server";
		String serverName = ChatColor.translateAlternateColorCodes('&', Config.THIS_SERVER_NAME);

		if (Config.CHAT_ENABLE) {
			String message = Config.CHAT_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			message = shieldReplace(message);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);
			sendServerMsg(message, MessageType.CHAT);
		}

		if (Config.ACTION_BAR_ENABLE) {
			String message = Config.ACTION_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);
			message = shieldReplace(message);
			sendServerMsg(message, MessageType.ACTION_BAR);
		}

		if (Config.BOSS_BAR_ENABLE) {
			String message = Config.BOSS_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);
			message = shieldReplace(message);
			sendServerMsg(message, MessageType.BOSS_BAR);
		}

		plugin.PR("<" + serverName + "> " + senderName + " : " + msg);

//			plugin.sendServerChat(servername, name, msg);

//			ByteArrayDataOutput out = ByteStreams.newDataOutput();
//			out.writeUTF("Forward");
//			out.writeUTF("ALL");
//			out.writeUTF("ServerChat");
//			out.writeUTF("");
//			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//			DataOutputStream msgout = new DataOutputStream(msgbytes);
//			//
//			msgout.writeLong(System.currentTimeMillis());
//			//
//			msgout.writeUTF(servername);
//			msgout.writeUTF(name);
//			msgout.writeUTF(msg);
//			out.writeShort(msgbytes.toByteArray().length);
//			out.write(msgbytes.toByteArray());
//			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
//			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}

	public static void sendServerMsg(String msg, MessageType type) {

		try {

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ServerChat");
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeLong(System.currentTimeMillis());
			msgout.writeUTF(type.name());
			msgout.writeUTF(msg);
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			plugin.sendMsg(msg, type);
		} catch (IOException e) {
			plugin.PR("Bungee通信出错，请上报mcbbs , bungee message error , report it to spigotmc.");
			e.printStackTrace();
		}
	}

	private static String shieldReplace(String msg) {
		String r = Config.SHILED_REPLACES;
		for (String s : Config.SHIELD_MESSAGES) {
			if (msg.contains(s)) {
				msg = msg.replaceAll(s, copy(r, s.length()));
			}
		}
		return msg;
	}

	private static String copy(String s, int n) {
		String str = "";
		for (int i = 0; i < n; i++) {
			str = str + s;
		}
		return str;
	}

}
