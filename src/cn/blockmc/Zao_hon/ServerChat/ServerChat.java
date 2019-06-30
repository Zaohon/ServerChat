package cn.blockmc.Zao_hon.ServerChat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.lenis0012.bukkit.loginsecurity.LoginSecurity;

import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.milkbowl.vault.economy.Economy;

public class ServerChat extends JavaPlugin implements Listener {
	private File itemfile;
	private Economy economy;
	private LoginSecurity loginsecurity;
	private AuthMeApi authmeapi;
	private ItemStack horn = null;
	private HashMap<UUID, Boolean> ignored = new HashMap<UUID, Boolean>();
	private boolean outdate = true;
	// public Message Message;

	@Override
	public void onEnable() {
		instance = this;
		Config.reload();
		Lang.reload();
		this.loadDepends();
		this.loadHorn();
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getCommand("ServerChat").setExecutor(new Commands());

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("servers", () -> "Bungee"));

		PR("========================");
		PR("      ServerChat          ");
		PR("     Version: " + this.getDescription().getVersion());
		PR("     Author:Zao_hon           ");
		PR("========================");

		if (Config.AUTO_UPDATE_CHECK)
			this.checkUpdate();

	}

	private void checkUpdate() {
		String latest = UpdateChecker.getLatestVersion();
		String now = this.getDescription().getVersion();
		if (now.equals(latest)) {
			outdate = false;
		} else {
			outdate = true;
			PR("发现一个新版本v" + latest + "!,而你还在用旧版本v" + now);
			PR("快去MCBBS下载最新版本吧!http://www.mcbbs.net/thread-704339-1-1.html");
		}
	}

	private void loadDepends() {
		if (getServer().getPluginManager().getPlugin("Authme") != null) {
			authmeapi = AuthMeApi.getInstance();
			PR("检测到登录插件Authme");
		}
		if (getServer().getPluginManager().getPlugin("LoginSecurity") != null) {
			loginsecurity = (LoginSecurity) getServer().getPluginManager().getPlugin("LoginSecurity");
			PR("检测到登录插件Authme");
		}
		if (setupEconomy()) {
			PR("已加载经济插件Vault");
		}

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	public boolean loadHorn() {
		if (itemfile == null) {
			itemfile = new File(this.getDataFolder(), "Item.yml");
		}
		if (!itemfile.exists()) {
			this.setHorn(DefaultItem.getHorn());
			return true;
		}

		horn = YamlConfiguration.loadConfiguration(itemfile).getItemStack("Item");
		return true;
	}

	public ItemStack getHorn() {
		return horn;
	}

	public boolean setHorn(ItemStack item) {
		horn = item;
		FileConfiguration config = YamlConfiguration.loadConfiguration(itemfile);
		try {
			config.set("Item", item);
			config.save(itemfile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void sendServerChat(String servername, String playername, String msg) {

		if (Config.BOSS_BAR_ENABLE) {
			String message = Config.BOSS_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", servername)
					.replaceAll("%player%", playername);

			BarColor color = BarColor.valueOf(Config.BOSS_BAR_COLOR);
			BarStyle style = BarStyle.valueOf(Config.BOSS_BAR_STYLE);
			List<String> sflags = Config.BOSS_BAR_FLAGS;
			// String[] sflags = (String[])
			// getConfig().getStringList("BossBarFlags").toArray();

			BarFlag[] flags = new BarFlag[sflags.size()];
			for (int i = 0; i < sflags.size(); i++) {
				flags[i] = BarFlag.valueOf(sflags.get(i));
			}
			BossBar bar = Bukkit.createBossBar(shieldReplace(message), color, style, flags);

			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				if (!ignored.getOrDefault(p.getUniqueId(), false))
					bar.addPlayer(p);
			});
			bar.setVisible(true);
			Bukkit.getScheduler().runTaskLater(this, () -> bar.removeAll(),
					getConfig().getInt("BossBarContinued") * 20);
		}

		if (Config.CHAT_ENABLE) {
			String message = Config.CHAT_MESSAGE.replace("%message%", msg).replaceAll("%server%", servername)
					.replaceAll("%player%", playername);
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (!ignored.getOrDefault(p.getUniqueId(), false))
					p.sendMessage(shieldReplace(message));
			});
		}

		if (Config.ACTION_BAR_ENABLE) {
			String message = Config.ACTION_BAR_MESSAGE.replace("%message%", msg).replaceAll("%server%", servername)
					.replaceAll("%player%", playername);
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (!ignored.getOrDefault(p.getUniqueId(), false)) {
					NMSUtils.sendActionBar(p, shieldReplace(message));
				}
			});
		}
		PR("<" + servername + "> " + playername + " : " + msg);
	}

	public Economy getEconomy() {
		return economy;
	}

	public AuthMeApi getAuthMeApi() {
		return authmeapi;
	}

	public LoginSecurity getLoginSecurity() {
		return loginsecurity;
	}

	public boolean changePlayerIgnored(UUID uuid) {
		ignored.putIfAbsent(uuid, Boolean.FALSE);
		return ignored.put(uuid, !ignored.get(uuid));
	}

	public boolean isOutdate() {
		return outdate;
	}

	private String shieldReplace(String msg) {
		String r = Config.SHILED_REPLACES;
		for (String s : Config.SHIELD_MESSAGES) {
			if (msg.contains(s)) {
				msg = msg.replaceAll(s, copy(r, s.length()));
			}
		}
		return msg;
	}

	private String copy(String s, int n) {
		String str = "";
		for (int i = 0; i < n; i++) {
			str = str + s;
		}
		return str;
	}

	public void PR(String str) {
		this.getLogger().info(str);
	}

	private static ServerChat instance = null;

	public static ServerChat getInstance() {
		return instance;
	}
}
