package cn.blockmc.Zao_hon.ServerChat.chat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cn.blockmc.Zao_hon.ServerChat.HornItem;
import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class ServerChatHandler implements PrefixChatHandler {
	private ServerChat plugin;
	private String prefix;

	public ServerChatHandler(ServerChat plugin) {
		this.plugin = plugin;
		this.prefix = Config.CHAT_PREFIX;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getChatType() {
		return "ServerChat";
	}

	@Override
	public boolean handle(Player player, String message) {
		if (Config.FREE_CHAT) {
			int cooltime = CoolTimeManager.remainChatCoolTime(player.getUniqueId());
			if (cooltime > 0) {
				Message.playerSendMessage(player, Message.getString("chat_error_incool", "%cooltime%", cooltime));
				return false;
			}
			BungeeUtil.bungeeMsg(plugin, player, message);
			CoolTimeManager.updateChatCoolTime(player.getUniqueId());
			return true;
		}
		
		if (Config.AUTO_USE) {
			int horncooltime = CoolTimeManager.remainHornCoolTime(player.getUniqueId());
			if (horncooltime > 0) {
				Message.playerSendMessage(player, Message.getString("horn_error_incool", "%cooltime%", horncooltime));
				return true;
			}
			if (this.consumeItemStack(player.getInventory(), HornItem.getHornItem())) {
				BungeeUtil.bungeeMsg(plugin, player, message);
				CoolTimeManager.updateHornCoolTime(player.getUniqueId());
				Message.playerSendMessage(player, Message.getString("chat_auto_use_success"));
				return true;
			} else {
				if (!Config.AUTO_COST) {
					Message.playerSendMessage(player, Message.getString("chat_auto_use_failed"));
					return true;
				}
			}
		}
		return true;
	}

	private boolean consumeItemStack(Inventory inv, ItemStack item) {
		for (ItemStack cont : inv.getStorageContents()) {
			if (cont != null && cont.isSimilar(item)) {
				cont.setAmount(cont.getAmount() - 1);
				return true;
			}
		}
		return false;
	}

}
