package cn.blockmc.Zao_hon.ServerChat.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.chat.CorrespondMessage;
import cn.blockmc.Zao_hon.ServerChat.chat.MessageType;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class BungeeUtil {
	private static ServerChat plugin;

	public static void init(ServerChat plugin) {
		BungeeUtil.plugin = plugin;
	}

	public static void bungeeMsg(ServerChat plugin, CommandSender sender, String msg) {
		String senderName = sender instanceof Player ? sender.getName() : "Server";
		String serverName = ChatColor.translateAlternateColorCodes('&', Config.THIS_SERVER_NAME);

		
		List<String> hoverTexts = sender instanceof Player
				? replaceListHolder((Player) sender, Config.CHAT_HOVER_MESSAGES)
				: new ArrayList<String>();

		if (Config.CHAT_ENABLE) {
			String message = Config.CHAT_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			message = shieldReplace(message);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);

			CorrespondMessage sm = new CorrespondMessage(senderName, serverName, message, MessageType.CHAT,
					System.currentTimeMillis(), hoverTexts);
			sendServerMsg(sm);
		}

		if (Config.ACTION_BAR_ENABLE) {
			String message = Config.ACTION_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);
			message = shieldReplace(message);
			CorrespondMessage sm = new CorrespondMessage(senderName, serverName, message, MessageType.ACTION_BAR,
					System.currentTimeMillis(), hoverTexts);
			sendServerMsg(sm);
		}

		if (Config.BOSS_BAR_ENABLE) {
			String message = Config.BOSS_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", serverName)
					.replaceAll("%player%", senderName);
			if (sender instanceof Player)
				message = Message.replacePlayceHolders((Player) sender, message);
			message = shieldReplace(message);
			CorrespondMessage sm = new CorrespondMessage(senderName, serverName, message, MessageType.BOSS_BAR,
					System.currentTimeMillis(), hoverTexts);
			sendServerMsg(sm);
		}

		plugin.PR("<" + serverName + "> " + senderName + " : " + msg);
	}

	public static void sendServerMsg(CorrespondMessage message) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("ServerChat");
			byte[] bytes = CorrespondMessage.toBytes(message);
			out.writeShort(bytes.length);
			out.write(bytes);
			Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			plugin.sendMsg(message);
		} catch (IOException e) {
			plugin.PR("Bungee出错,请看mcbbs , bungee message error , report it to spigotmc.");
			e.printStackTrace();
		}
	}

	private static List<String> replaceListHolder(Player player, List<String> list) {
		List<String> newList = new ArrayList<String>(list.size());
		for (String s : list) {
			newList.add(Message.replacePlayceHolders(player, s));
		}
		return newList;

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

	public static String copy(String s, int n) {
		String str = "";
		for (int i = 0; i < n; i++) {
			str = str + s;
		}
		return str;
	}

}
