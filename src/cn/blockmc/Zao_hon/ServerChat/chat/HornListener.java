package cn.blockmc.Zao_hon.ServerChat.chat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cn.blockmc.Zao_hon.ServerChat.HornItem;
import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class HornListener implements Listener, PrefixChatHandler {
	private ServerChat plugin;

	public HornListener(ServerChat plugin) {
		this.plugin = plugin;
	}

	private HashMap<UUID, Boolean> usinghorn = new HashMap<UUID, Boolean>();
	private HashMap<UUID, BukkitRunnable> playerrunnable = new HashMap<UUID, BukkitRunnable>();

	@EventHandler
	public void useTrumpet(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack hand = e.getItem();
		if (hand != null && HornItem.isHornItem(hand)) {
			e.setCancelled(true);
			if (usinghorn.getOrDefault(p.getUniqueId(), false)) {
				Message.playerSendMessage(p, Message.getString("horn_error_already_use"));
				return;
			}

			int cooltime = CoolTimeManager.remainHornCoolTime(p.getUniqueId());
			if (cooltime > 0) {
				Message.playerSendMessage(p, Message.getString("horn_error_incool", "%cooltime%", cooltime));
				return;
			}

			usinghorn.put(p.getUniqueId(), true);
			Message.playerSendMessage(p, Message.getString("horn_tip_using"));
			int amount = hand.getAmount();
			hand.setAmount(amount - 1);

			BukkitRunnable runable = new BukkitRunnable() {
				@Override
				public void run() {
					usinghorn.put(p.getUniqueId(), false);
					Message.playerSendMessage(p, Message.getString("horn_tip_overtime"));
					p.getInventory().addItem(HornItem.getHornItem());
				}
			};
			runable.runTaskLater(plugin, Config.HORNSET_RESPON_TIME * 20);
			playerrunnable.put(p.getUniqueId(), runable);
			CoolTimeManager.updateHornCoolTime(p.getUniqueId());
		}

	}

	@Override
	public String getPrefix() {
		return "";
	}

	@Override
	public String getChatType() {
		return "HornChat";
	}

	@Override
	public boolean handle(Player player, String message) {
		if (usinghorn.getOrDefault(player.getUniqueId(), false)) {
			if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
				Message.playerSendMessage(player, Message.getString("chat_error_length"));
				return false;
			}
			BungeeUtil.sendServerChat(plugin, player, message);
			usinghorn.put(player.getUniqueId(), false);
			playerrunnable.get(player.getUniqueId()).cancel();
			return true;
		}
		return false;

	}

}
