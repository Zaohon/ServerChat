package cn.blockmc.Zao_hon.ServerChat.chat;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import cn.blockmc.Zao_hon.ServerChat.HornItem;
import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class ChatListener implements Listener {
	private ServerChat plugin;
	private Set<PrefixChatHandler> handlers = new LinkedHashSet<PrefixChatHandler>();

	public ChatListener(ServerChat plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void addPrefixHandler(PrefixChatHandler handler) {
		handlers.add(handler);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		String msg = event.getMessage();
		msg = player.hasPermission("ServerChat.Color") ? msg.replace("&", "¡ì") : msg;
		for (PrefixChatHandler handler : handlers) {
			String prefix = handler.getPrefix();
			int lenth = prefix.length();
			if (msg.startsWith(prefix)) {
				String message = msg;
				if(lenth!=0)message = message.substring(lenth - 1);
				if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
					Message.playerSendMessage(player, Message.getString("chat_error_length"));
					continue;
				}
				if (message.length() == 0) {
					Message.playerSendMessage(player, Message.getString("chat_error_empty"));
					continue;
				}
				if (handler.handle(player, message)) {
					event.setCancelled(true);
					return;
				}
					

			}
		}

	}
}
