package cn.blockmc.Zao_hon.ServerChat;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.Utils.CorrespondMessage;
import cn.blockmc.Zao_hon.ServerChat.Utils.MessageType;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class EventListener implements Listener {
	private ServerChat plugin;
	private HashMap<UUID, Long> horncooltime = new HashMap<UUID, Long>();
	private HashMap<UUID, Long> chatcooltime = new HashMap<UUID, Long>();
	private HashMap<UUID, Boolean> usinghorn = new HashMap<UUID, Boolean>();
	private HashMap<UUID, BukkitRunnable> playerrunnable = new HashMap<UUID, BukkitRunnable>();

	public EventListener(ServerChat plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		String message = e.getMessage();
		if (usinghorn.getOrDefault(p.getUniqueId(), false)) {
			e.setCancelled(true);
			if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
				Message.playerSendMessage(p, Message.getString("chat_error_length"));
				return;
			}
			colorMessage(p, message);
			BungeeUtil.sendServerChat(plugin, p, message);
			usinghorn.put(p.getUniqueId(), false);
			playerrunnable.get(p.getUniqueId()).cancel();
			return;
		}
		if (Config.CHAT_PREFIX_ENABLE) {
			String prefix = Config.CHAT_PREFIX;
			boolean emptyPrefix = prefix.equals("");
			if (emptyPrefix || message.startsWith(prefix)) {
				e.setCancelled(true);
				message = emptyPrefix ? message : message.substring(1);
				if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
					Message.playerSendMessage(p, Message.getString("chat_error_length"));
					return;
				}
				if (message.length() == 0) {
					Message.playerSendMessage(p, Message.getString("chat_error_empty"));
					return;
				}
				colorMessage(p, message);

				if (Config.FREE_CHAT) {
					int cooltime = remainChatCoolTime(p.getUniqueId());
					if (cooltime > 0) {
						Message.playerSendMessage(p, Message.getString("chat_error_incool", "%cooltime%", cooltime));
						return;
					}

					BungeeUtil.sendServerChat(plugin, p, message);
					updateChatCoolTime(p.getUniqueId());
					return;
				}

				if (Config.AUTO_USE) {
					int horncooltime = remainHornCoolTime(p.getUniqueId());
					if (horncooltime > 0) {
						Message.playerSendMessage(p,
								Message.getString("horn_error_incool", "%cooltime%", horncooltime));
						return;
					}
					if (this.consumeItemStack(p.getInventory(), HornItem.getHornItem())) {
						BungeeUtil.sendServerChat(plugin, p, message);
						updateHornCoolTime(p.getUniqueId());
						Message.playerSendMessage(p, Message.getString("chat_auto_use_success"));
						return;
					} else {
						if (!Config.AUTO_COST) {
							Message.playerSendMessage(p, Message.getString("chat_auto_use_failed"));
							return;
						}
					}
				}

			}

		}
		if(Config.SPY_ENABLE) {
			Pattern pattern = Pattern.compile(">>(.+)\\s");
			Matcher matcher = pattern.matcher(message);
			if(matcher.find()) {
				String s = matcher.group();
				String target = s.substring(2);
				String msg = message.substring(s.length());
				CorrespondMessage sm = new CorrespondMessage(p.getName(), "", msg, MessageType.SPY_MSG,
						System.currentTimeMillis(), null);
				BungeeUtil.sendServerMsg(sm);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractInv(InventoryInteractEvent event) {
		if (!usinghorn.containsKey(event.getWhoClicked().getUniqueId()))
			return;

	}

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

			int cooltime = remainHornCoolTime(p.getUniqueId());
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
			this.updateHornCoolTime(p.getUniqueId());
		}

	}

	private int remainChatCoolTime(UUID uuid) {
		int cooltime = 0;
		if (chatcooltime.containsKey(uuid)) {
			Long t = chatcooltime.get(uuid);
			if (t + 1000 * Config.CHAT_COOL_TIME > System.currentTimeMillis()) {
				cooltime = (int) ((t + 1000 * Config.CHAT_COOL_TIME - System.currentTimeMillis()) / 1000);
			}
		}
		return cooltime;
	}

	private void updateChatCoolTime(UUID uuid) {
		chatcooltime.put(uuid, System.currentTimeMillis());
	}

	private int remainHornCoolTime(UUID uuid) {
		int cooltime = 0;
		if (horncooltime.containsKey(uuid)) {
			Long t = horncooltime.get(uuid);
			if (t + 1000 * Config.HORNSET_COOL_TIME > System.currentTimeMillis()) {
				cooltime = (int) ((t + 1000 * Config.HORNSET_COOL_TIME - System.currentTimeMillis()) / 1000);
			}
		}
		return cooltime;
	}

	private void updateHornCoolTime(UUID uuid) {
		horncooltime.put(uuid, System.currentTimeMillis());
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

	private String colorMessage(Player player, String message) {
		return player.hasPermission("ServerChat.Color") ? message.replace("&", "¡ì") : message;
	}
}
