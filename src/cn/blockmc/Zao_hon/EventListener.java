package cn.blockmc.Zao_hon;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.lenis0012.bukkit.loginsecurity.LoginSecurity;

import configuration.Config;
import configuration.Lang;

public class EventListener implements Listener {
	private ServerChat plugin;
	private HashMap<UUID, Long> horncooltime = new HashMap<UUID, Long>();
	private HashMap<UUID, Long> chatcooltime = new HashMap<UUID, Long>();
	private HashMap<UUID, Boolean> usingtrumple = new HashMap<UUID, Boolean>();
	private HashMap<UUID, BukkitRunnable> playerrunnable = new HashMap<UUID, BukkitRunnable>();

	public EventListener() {
		this.plugin = ServerChat.getInstance();
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		if (!isAuthenticated(p)) {
			p.sendMessage(Lang.WITHOUT_AUTHENTICATED.replace("&", "§"));
			return;
		}
		String message = e.getMessage();
		if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
			e.setCancelled(true);
			if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
				p.sendMessage(Lang.CHAT_LENTH_ERROR);
				return;
			}
			usingtrumple.put(p.getUniqueId(), false);
			playerrunnable.get(p.getUniqueId()).cancel();
			BungeeUtil.sendServerChat(plugin, p, message);
			return;
		}
		if (Config.CHAT_PREFIX_ENABLE) {
			String prefix = Config.CHAT_PREFIX;
			if (message.startsWith(prefix)) {
				e.setCancelled(true);
				// if (chattime.containsKey(p.getUniqueId())) {
				// if (chattime.get(p.getUniqueId()) + 1000 *
				// plugin.getConfig().getInt("CoolTime") > System
				// .currentTimeMillis()) {
				// int cooltime = (int) ((chattime.get(p.getUniqueId())
				// + 1000 * plugin.getConfig().getInt("ChatCoolTime") -
				// System.currentTimeMillis())
				// / 1000);
				// p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				// plugin.Message.PrefixChatInCoolTime.replace("%cooltime%",
				// cooltime + "")));
				// return;
				// }
				// }
				int cooltime = remainChatCoolTime(p.getUniqueId());
				if (cooltime > 0) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							Lang.CHAT_IN_COOL_TIME.replace("%cooltime%", cooltime + "")));
					return;
				}

				message = message.substring(1);
				if (message.length() == 0) {
					p.sendMessage("§c消息不能为空！");
					return;
				}

				if (plugin.getConfig().getBoolean("FreeChat")) {
					BungeeUtil.sendServerChat(plugin, p, message);
					updateChatCoolTime(p.getUniqueId());
					return;
				}

				if (plugin.getConfig().getBoolean("AutoUse")) {
					if (p.getInventory().contains(plugin.getHorn())) {
						ItemStack item = p.getInventory().getItem(p.getInventory().first(plugin.getHorn()));
						item.setAmount(item.getAmount() - 1);
						BungeeUtil.sendServerChat(plugin, p, message);
						updateChatCoolTime(p.getUniqueId());
						this.sendPlayerMsg(p, Lang.AUTO_USE_SUCCESS);
						return;
					} else if (plugin.getConfig().getBoolean("AutoCost")) {
						int mc = plugin.getConfig().getInt("Cost.Money");
						// int ppc =
						// plugin.getConfig().getInt("Cost.PlayerPoint");
						if (mc != 0 && plugin.getEconomy().getBalance(p) >= mc) {
							plugin.getEconomy().depositPlayer(p, mc);
							BungeeUtil.sendServerChat(plugin, p, message);
							updateChatCoolTime(p.getUniqueId());
							this.sendPlayerMsg(p, Lang.AUTO_COST_MONEY);
						} else {
							this.sendPlayerMsg(p, Lang.AUTO_COST_FAILED);
							return;
						}
						// TODO playerpoint
					} else {
						this.sendPlayerMsg(p, Lang.AUTO_USE_FAILED);
						return;
					}
				}
				BungeeUtil.sendServerChat(plugin, p, message);
				chatcooltime.put(p.getUniqueId(), System.currentTimeMillis());
				return;
			}
		}

	}

	@EventHandler
	public void useTrumpet(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!isAuthenticated(p)) {
			p.sendMessage(Lang.WITHOUT_AUTHENTICATED.replace("&", "§"));
			return;
		}
		ItemStack hand = e.getItem();
		// ItemStack hand = p.getItemInHand();

		if (hand != null && hand.isSimilar(plugin.getHorn())) {
			e.setCancelled(true);
			if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.Message.AlreadyUsingHorn));
				return;
			}

			int cooltime = remainHornCoolTime(p.getUniqueId());
			if (cooltime > 0) {
				p.sendMessage(Lang.HORN_IN_COOL_TIME.replace("&", "§").replace("%cooltime%", cooltime + ""));
				return;
			}

			usingtrumple.put(p.getUniqueId(), true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.Message.HintWhenUsingHorn));
			int amount = hand.getAmount();
			if (amount <= 1) {
				p.getInventory().remove(hand);
			} else {
				hand.setAmount(amount - 1);
			}
			BukkitRunnable runable = new BukkitRunnable() {
				@Override
				public void run() {
					usingtrumple.put(p.getUniqueId(), false);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.Message.HintOvertimeUseHorn));
					p.getInventory().addItem(plugin.getHorn());
				}
			};
			runable.runTaskLater(plugin, plugin.getConfig().getInt("ItemResponTime") * 20);
			playerrunnable.put(p.getUniqueId(), runable);
		}

	}

	private boolean isAuthenticated(Player player) {
		if (plugin.getAuthMeApi() != null && !plugin.getAuthMeApi().isAuthenticated(player)) {
			return false;
		}
		if (plugin.getLoginSecurity() != null
				&& !LoginSecurity.getSessionManager().getPlayerSession(player).isAuthorized()) {
			return false;
		}
		return true;
	}

	private int remainChatCoolTime(UUID uuid) {
		int cooltime = 0;
		if (chatcooltime.containsKey(uuid)) {
			Long t = chatcooltime.get(uuid);
			if (t + 1000 * Config.CHAT_COOL_TIME > System.currentTimeMillis()) {
				cooltime = (int) (t + 1000 * Config.CHAT_COOL_TIME - System.currentTimeMillis() / 1000);
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
				cooltime = (int) (t + 1000 * Config.HORNSET_COOL_TIME - System.currentTimeMillis() / 1000);
			}
		}
		return cooltime;
	}

	private void updateHornCoolTime(UUID uuid) {
		horncooltime.put(uuid, System.currentTimeMillis());
	}

	private void sendPlayerMsg(Player p, String msg) {
		if (!msg.equals(""))
			p.sendMessage(msg);
	}
}
