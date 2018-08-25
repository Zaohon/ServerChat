package cn.blockmc.ServerChat.Zao_hon;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.lenis0012.bukkit.loginsecurity.LoginSecurity;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerChat extends JavaPlugin implements Listener {
	private LoginSecurity loginsecurity;
	private AuthMeApi authmeapi;
	private ItemStack horn = null;

	@Override
	public void onEnable() {
		getLogger().info("========================");
		getLogger().info("      ServerChat          ");
		getLogger().info("     Version: " + this.getDescription().getVersion());
		getLogger().info("     Author:Zao_hon           ");
		getLogger().info("========================");
		
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()){
			this.saveDefaultConfig();
		}
//		this.saveDefaultConfig();
		this.reloadConfig();
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.getCommand("ServerChat").setExecutor(new Commands(this));
		this.updateTrumpleItem();
		
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("servers", () -> "Spigot"));
		
		this.loadDepends();
	}
	private void loadDepends(){
		if(this.getServer().getPluginManager().getPlugin("Authme")!=null){
			authmeapi = AuthMeApi.getInstance();
		}
		if(this.getServer().getPluginManager().getPlugin("LoginSecurity")!=null){
			loginsecurity = (LoginSecurity) getServer().getPluginManager().getPlugin("LoginSecurity");
		}
	}

	public void updateTrumpleItem() {
//		getConfig().addDefault("Item.Name", null);
//		getConfig().addDefault("Item.Lore", null);
		String n = getConfig().getString("Item.Name");
		List<String> lore = getConfig().getStringList("Item.Lore");
		Material m = MaterialManager.getMaterial(getConfig().getString("Item.Material"));
		horn = new ItemStack(m);
		ItemMeta meta = horn.getItemMeta();
		meta.setDisplayName(n);
		meta.setLore(lore);
		horn.setItemMeta(meta);
	}

	public ItemStack getHorn() {
		return horn;
	}

	public void setHorn(ItemStack item) {
		this.horn = item;
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
					p -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(shieldReplace(message))));
		}
	}
	public AuthMeApi getAuthMeApi(){
		return authmeapi;
	}
	public LoginSecurity getLoginSecurity(){
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
}
