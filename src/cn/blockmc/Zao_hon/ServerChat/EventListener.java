package cn.blockmc.Zao_hon.ServerChat;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

//import com.lenis0012.bukkit.loginsecurity.LoginSecurity;

import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;

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
	public void updateCheck(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		if (p.isOp()) {
			if (plugin.isOutdate()) {
				String latest = UpdateChecker.getLatestVersion();
				p.sendMessage("[ServerChat]发现新版本" + latest + ",快前往http://www.mcbbs.net/thread-704339-1-1.html下载!");
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		if (e.isCancelled())
			return;
		String message = e.getMessage();
		if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
			e.setCancelled(true);
			if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
				Lang.sendMsg(p, Lang.CHAT_LENTH_ERROR);
				return;
			}
			usingtrumple.put(p.getUniqueId(), false);
			playerrunnable.get(p.getUniqueId()).cancel();

			if (p.hasPermission("ServerChat.Color")) {
				message = message.replace("&", "§");
			}

			BungeeUtil.sendServerChat(plugin, p, message);
			return;
		}
		if (Config.CHAT_PREFIX_ENABLE) {
			String prefix = Config.CHAT_PREFIX;
			if (message.startsWith(prefix)) {
				e.setCancelled(true);
				message = message.substring(1);
				if (message.length() < Config.LENTH_LIMIT_MIN || message.length() > Config.LENTH_LIMIT_MAX) {
					Lang.sendMsg(p, Lang.CHAT_LENTH_ERROR);
					return;
				}
				if (message.length() == 0) {
					Lang.sendMsg(p, Lang.CHAT_MSG_EMPTY);
					return;
				}

				if (Config.FREE_CHAT) {
					int cooltime = remainChatCoolTime(p.getUniqueId());
					if (cooltime > 0) {
						Lang.sendMsg(p, ChatColor.translateAlternateColorCodes('&',
								Lang.CHAT_IN_COOL_TIME.replace("%cooltime%", cooltime + "")));
						return;
					}
					if (p.hasPermission("ServerChat.Color")) {
						message = message.replace("&", "§");
					}
					BungeeUtil.sendServerChat(plugin, p, message);
					updateChatCoolTime(p.getUniqueId());
					return;
				}

				if (Config.AUTO_USE) {
					int horncooltime = remainHornCoolTime(p.getUniqueId());
					if (horncooltime > 0) {
						Lang.sendMsg(p,
								Lang.HORN_IN_COOL_TIME.replace("&", "§").replace("%cooltime%", horncooltime + ""));
						return;
					}
					if (this.consumeItemStack(p.getInventory(), plugin.getHorn())) {
						if (p.hasPermission("ServerChat.Color")) {
							message = message.replace("&", "§");
						}
						BungeeUtil.sendServerChat(plugin, p, message);
						updateHornCoolTime(p.getUniqueId());
						Lang.sendMsg(p, Lang.AUTO_USE_SUCCESS);
						return;
					} else {
						if (!Config.AUTO_COST) {
							Lang.sendMsg(p, Lang.AUTO_USE_FAILED);
							return;
						}
					}
				}
				if (Config.AUTO_COST) {
					int mc = Config.COST_MONEY;
					double mn = plugin.getEconomy() == null ? -1 : plugin.getEconomy().getBalance(p);
					// int ppc =
					// plugin.getConfig().getInt("Cost.PlayerPoint");
					if (mc != 0 && mn >= mc) {
						plugin.getEconomy().depositPlayer(p, mc);
						if (p.hasPermission("ServerChat.Color")) {
							message = message.replace("&", "§");
						}
						BungeeUtil.sendServerChat(plugin, p, message);
						updateChatCoolTime(p.getUniqueId());
						Lang.sendMsg(p, Lang.AUTO_COST_MONEY.replace("%money%", mc + ""));
					} else {
						Lang.sendMsg(p, Lang.AUTO_COST_FAILED_MONEY.replace("%money%", mc + ""));
						return;
					}
					// TODO playerpoint
				}
			}

		}

	}

	@EventHandler
	public void useTrumpet(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack hand = e.getItem();
		if (hand != null && hand.isSimilar(plugin.getHorn())) {
			e.setCancelled(true);
			if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
				Lang.sendMsg(p, ChatColor.translateAlternateColorCodes('&', Lang.ALREADY_USING_HORN));
				return;
			}

			int cooltime = remainHornCoolTime(p.getUniqueId());
			if (cooltime > 0) {
				Lang.sendMsg(p, Lang.HORN_IN_COOL_TIME.replace("&", "§").replace("%cooltime%", cooltime + ""));
				return;
			}

			usingtrumple.put(p.getUniqueId(), true);
			Lang.sendMsg(p, ChatColor.translateAlternateColorCodes('&', Lang.HINT_WHEN_USING_HORN));
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
					Lang.sendMsg(p, ChatColor.translateAlternateColorCodes('&', Lang.HINT_OVERTIME_USEHORN));
					p.getInventory().addItem(plugin.getHorn());
				}
			};
			runable.runTaskLater(plugin, Config.HORNSET_RESPON_TIME * 20);
			playerrunnable.put(p.getUniqueId(), runable);
			this.updateHornCoolTime(p.getUniqueId());
		}

	}
	//
	// private boolean isAuthenticated(Player player) {
	// if (plugin.getAuthMeApi() != null &&
	// !plugin.getAuthMeApi().isAuthenticated(player)) {
	// return false;
	// }
	// if (plugin.getLoginSecurity() != null
	// &&
	// !LoginSecurity.getSessionManager().getPlayerSession(player).isAuthorized())
	// {
	// return false;
	// }
	// return true;
	// }

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
}
