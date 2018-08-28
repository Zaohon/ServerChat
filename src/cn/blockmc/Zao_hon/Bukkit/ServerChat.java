package cn.blockmc.Zao_hon.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.lenis0012.bukkit.loginsecurity.LoginSecurity;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.md_5.bungee.api.ChatColor;

public class ServerChat extends JavaPlugin implements Listener {
	private File file;
	private FileConfiguration hornconfig;
	private LoginSecurity loginsecurity;
	private AuthMeApi authmeapi;
	private ItemStack horn = null;

	@Override
	public void onEnable() {

		this.saveDefaultConfig();
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("ServerChat").setExecutor(new Commands(this));

		file = new File(this.getDataFolder(), "Item.yml");
		this.loadHorn();

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("servers", () -> "Spigot"));

		this.loadDepends();
		PR("========================");
		PR("      ServerChat          ");
		PR("     Version: " + this.getDescription().getVersion());
		PR("     Author:Zao_hon           ");
		PR("========================");
	}

	private void loadDepends() {
		if (this.getServer().getPluginManager().getPlugin("Authme") != null) {
			authmeapi = AuthMeApi.getInstance();
		}
		if (this.getServer().getPluginManager().getPlugin("LoginSecurity") != null) {
			loginsecurity = (LoginSecurity) getServer().getPluginManager().getPlugin("LoginSecurity");
		}
	}

	public boolean loadHorn() {
		if (!file.exists()) {
			try {
				file.createNewFile();
				hornconfig = YamlConfiguration.loadConfiguration(file);
				this.setHorn(DefaultItem.getHorn());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		hornconfig = YamlConfiguration.loadConfiguration(file);
		horn = hornconfig.getItemStack("Item");
		return true;
	}
	public ItemStack getHorn(){
		return horn;
	}
	public boolean setHorn(ItemStack item){
		horn = item;
		hornconfig.set("Item", horn);
		try {
			hornconfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void sendServerChat(String servername, String playername, String msg) {
		if (getConfig().getBoolean("BossBar")) {
			String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("BossBarMessage")
					.replace("%message%", msg).replaceAll("%server%", servername).replaceAll("%player%", playername));

			BossBar bar = Bukkit.createBossBar(shieldReplace(message),
					BarColor.valueOf(getConfig().getString("BossBarColor")), BarStyle.SOLID, new BarFlag[] {});

			Bukkit.getOnlinePlayers().forEach(p -> bar.addPlayer(p));
			bar.setVisible(true);
			Bukkit.getScheduler().runTaskLater(this, () -> bar.removeAll(),
					getConfig().getInt("BossBarContinued") * 20);
		}

		if (getConfig().getBoolean("Chat")) {

			String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ChatMessage")
					.replace("%message%", msg).replaceAll("%server%", servername).replaceAll("%player%", playername));
			Bukkit.broadcastMessage(shieldReplace(message));
		}

		if (getConfig().getBoolean("ActionBar")) {
			String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ActionBarMessage")
					.replace("%message%", msg).replaceAll("%server%", servername).replaceAll("%player%", playername));
			Bukkit.getOnlinePlayers().forEach(
					p ->NMSUtils.sendActionBar(p,shieldReplace(message)));
		}
	}

	public AuthMeApi getAuthMeApi() {
		return authmeapi;
	}

	public LoginSecurity getLoginSecurity() {
		return loginsecurity;
	}

	private String shieldReplace(String msg) {
		String r = getConfig().getString("ShieldReplaces");
		for (String s : getConfig().getStringList("ShieldMessages")) {
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
}
