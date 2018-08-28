package cn.blockmc.Zao_hon.Bukkit;

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

public class EventListener implements Listener {
	private ServerChat plugin;
	private HashMap<UUID, Long> chattime = new HashMap<UUID, Long>();
	private HashMap<UUID, Boolean> usingtrumple = new HashMap<UUID, Boolean>();
	private HashMap<UUID, BukkitRunnable> playerrunnable = new HashMap<UUID, BukkitRunnable>();

	public EventListener(ServerChat plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if(e.isCancelled()){
			return;
		}
		Player p = e.getPlayer();
		if(!isAuthenticated(p)){
			p.sendMessage(plugin.getConfig().getString("Message.WithoutAuthenticated").replace("&", "§"));
			return;
		}
		String message = e.getMessage();
		if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
			if (message.length() < plugin.getConfig().getInt("LenthLimit.Min")
					|| message.length() > plugin.getConfig().getInt("LenthLimit.Max")) {
				p.sendMessage(plugin.getConfig().getString("LenthLimit.Message").replace("&", "§"));
				return;
			}
			e.setCancelled(true);
			usingtrumple.put(p.getUniqueId(), false);
			playerrunnable.get(p.getUniqueId()).cancel();
			BungeeUtil.sendServerChat(plugin, p, message);
			return;
		}
		if (plugin.getConfig().getBoolean("ChatPrefixEnable")) {
			String prefix = plugin.getConfig().getString("ChatPrefix");
			if (message.startsWith(prefix)) {
				e.setCancelled(true);
				if (chattime.containsKey(p.getUniqueId())) {
					if (chattime.get(p.getUniqueId()) + 1000 * plugin.getConfig().getInt("ChatCoolTime") > System
							.currentTimeMillis()) {
						int cooltime = (int) ((chattime.get(p.getUniqueId())
								+ 1000 * plugin.getConfig().getInt("ChatCoolTime") - System.currentTimeMillis())
								/ 1000);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
								.getString("Message.PrefixChatInCoolTime").replace("%cooltime%", cooltime + "")));
						return;
					}
				}
				message = message.substring(1);
				if (message.length() == 0) {
					p.sendMessage("§c消息不能为空！");
					return;
				}
				BungeeUtil.sendServerChat(plugin, p, message);
				chattime.put(p.getUniqueId(), System.currentTimeMillis());
				return;
			}
		}

	}

	@EventHandler
	public void useTrumpet(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!isAuthenticated(p)){
			p.sendMessage(plugin.getConfig().getString("Message.WithoutAuthenticated").replace("&", "§"));
			return;
		}
		ItemStack hand = e.getItem();
//		ItemStack hand = p.getItemInHand();

		if (hand != null && hand.isSimilar(plugin.getHorn())) {
			e.setCancelled(true);
			if (usingtrumple.getOrDefault(p.getUniqueId(), false)) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("Message.AlreadyUsingHorn")));
				return;
			}
			usingtrumple.put(p.getUniqueId(), true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					plugin.getConfig().getString("Message.HintWhenUsingHorn")));
			int amount = hand.getAmount();
			if(amount<=1){
				p.getInventory().remove(hand);
			}else{
				hand.setAmount(amount-1);
			}
			BukkitRunnable runable = new BukkitRunnable() {
				@Override
				public void run() {
					usingtrumple.put(p.getUniqueId(), false);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("Message.HintOvertimeUseHorn")));
					p.getInventory().addItem(plugin.getHorn());
				}
			};
			runable.runTaskLater(plugin, plugin.getConfig().getInt("ItemResponTime") * 20);
			playerrunnable.put(p.getUniqueId(), runable);
		}

	}
	private boolean isAuthenticated(Player player){
		if (plugin.getAuthMeApi() != null&&!plugin.getAuthMeApi().isAuthenticated(player)) {
			return false;
		}
		if(plugin.getLoginSecurity()!=null&&!LoginSecurity.getSessionManager().getPlayerSession(player).isAuthorized()){
			return false;
		}
		return true;
	}
}
