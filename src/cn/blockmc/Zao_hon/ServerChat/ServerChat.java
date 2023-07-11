package cn.blockmc.Zao_hon.ServerChat;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.Utils.MessageListener;
import cn.blockmc.Zao_hon.ServerChat.Utils.NMSUtils;
import cn.blockmc.Zao_hon.ServerChat.chat.ChatListener;
import cn.blockmc.Zao_hon.ServerChat.chat.CorrespondMessage;
import cn.blockmc.Zao_hon.ServerChat.chat.HornListener;
import cn.blockmc.Zao_hon.ServerChat.chat.ServerChatHandler;
import cn.blockmc.Zao_hon.ServerChat.chat.SpyChatListener;
import cn.blockmc.Zao_hon.ServerChat.command.CommandDispatcher;
import cn.blockmc.Zao_hon.ServerChat.command.GiveCommand;
import cn.blockmc.Zao_hon.ServerChat.command.IgnoreCommand;
import cn.blockmc.Zao_hon.ServerChat.command.InfoCommand;
import cn.blockmc.Zao_hon.ServerChat.command.ReloadCommand;
import cn.blockmc.Zao_hon.ServerChat.command.SendCommand;
import cn.blockmc.Zao_hon.ServerChat.command.SetItemCommand;
import cn.blockmc.Zao_hon.ServerChat.command.UpdateCommand;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerChat extends JavaPlugin implements Listener {
	public static final String PREFIX = ChatColor.GREEN + "[ServerChat]" + ChatColor.RESET;
//	private Economy economy;
	private Message message;
	private HashMap<UUID, Boolean> ignored = new HashMap<UUID, Boolean>();
	private CommandDispatcher commandDispatcher;
	private ChatListener chatListener;
	private HornListener hornListener;
	private Plugin placeholderAPI = null;
	private Updater updater = null;
	@SuppressWarnings("unused")
	private Metrics metrics;
	
	private String nmsVersion;

	@Override
	public void onEnable() {
		Config.init(this);

		this.loadDepends();
		HornItem.init(this);
		BungeeUtil.init(this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));
		
		this.message = new Message(this);
		message.setLanguage(Config.LANG);

		updater = new Updater(this);
		updater.hourlyUpdateCheck(getServer().getConsoleSender(), Config.AUTO_UPDATE_CHECK, false);

		commandDispatcher = new CommandDispatcher(this, "ServerChat", Message.getString("command_description_plugin"));
		commandDispatcher.registerCommand(new InfoCommand(this));
		commandDispatcher.registerCommand(new SetItemCommand(this));
		commandDispatcher.registerCommand(new GiveCommand(this));
		commandDispatcher.registerCommand(new SendCommand(this));
		commandDispatcher.registerCommand(new IgnoreCommand(this));
//		commandDispatcher.registerCommand(new BuyCommand(this));
		commandDispatcher.registerCommand(new UpdateCommand(this));
		commandDispatcher.registerCommand(new ReloadCommand(this));
		this.getCommand("ServerChat").setExecutor(commandDispatcher);

		this.hornListener = new HornListener(this);
		this.getServer().getPluginManager().registerEvents(hornListener, this);
		
		this.chatListener = new ChatListener(this);
		this.chatListener.addPrefixHandler(hornListener);
		if (Config.CHAT_PREFIX_ENABLE)
			chatListener.addPrefixHandler(new ServerChatHandler(this));
		if (Config.SPY_ENABLE) {
			chatListener.addPrefixHandler(new SpyChatListener(this));
		}
		
		this.metrics = new Metrics(this);
		this.nmsVersion = NMSUtils.getVersion();
		

		PR("========================");
		PR("      ServerChat          ");
		PR("     Version: " + this.getDescription().getVersion());
		PR("     NMS: "+nmsVersion);
		PR("     Author:Zao_hon           ");
		PR("========================");

	}

	@Override
	public void onDisable() {
		PR("ServerChat Disabled");
	}

	private void loadDepends() {
//		if (setupEconomy()) {
//			PR("�Ѽ��ؾ��ò��Vault");
//		}

		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			PR("�Ѽ���ǰ�ò��PlaceholderAPI");
			placeholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI");
		}
	}

//	private boolean setupEconomy() {
//		if (getServer().getPluginManager().getPlugin("Vault") == null) {
//			return false;
//		}
//		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
//		if (rsp == null) {
//			return false;
//		}
//		economy = rsp.getProvider();
//		return economy != null;
//	}

	public void sendMsg(CorrespondMessage message) {

		switch (message.getMessageType()) {
		case CHAT:
			String msg = message.getMessage();

			if (Config.AT_ENABLE) {
				Pattern p = Pattern.compile("@(.+)\\s");
				Matcher m = p.matcher(msg);
				while (m.find()) {
					String name = m.group(1);
					Player target = Bukkit.getServer().getPlayerExact(name);
					if (target != null && target.isOnline()) {

						Message.playerSendMessage(target,
								Message.getString("at_tip", "%player%", message.getSenderName()));
						target.playSound(target.getLocation(), Sound.valueOf(Config.AT_SOUND), 10, 10);
						msg = msg.replace(m.group(0), "��b" + m.group(0) + "��r");
					}
				}
			}
			TextComponent text = new TextComponent(msg);
			
			if(Config.SPY_ENABLE) {
				text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ">>" + message.getSenderName() + " "));
			}

			if (!message.getHoverTexts().isEmpty()&&Config.HOVER_ENABLE) {
				int size = message.getHoverTexts().size();
				BaseComponent[] hovers = new BaseComponent[size];
				for (int i = 0; i < size; i++) {
					String m = message.getHoverTexts().get(i);
					m = m.replaceAll("%server%", message.getServerName()).replaceAll("%player%", message.getSenderName()) + "\n";
					hovers[i] = new TextComponent(m);
				}
				text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovers));
			}

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!ignored.getOrDefault(player.getUniqueId(), false))
					player.spigot().sendMessage(text);
			}

			break;
		case BOSS_BAR:
			BarColor color = BarColor.valueOf(Config.BOSS_BAR_COLOR);
			BarStyle style = BarStyle.valueOf(Config.BOSS_BAR_STYLE);
			List<String> sflags = Config.BOSS_BAR_FLAGS;
			BarFlag[] flags = new BarFlag[sflags.size()];
			for (int i = 0; i < sflags.size(); i++) {
				flags[i] = BarFlag.valueOf(sflags.get(i));
			}
			BossBar bar = Bukkit.createBossBar(message.getMessage(), color, style, flags);
			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				if (!ignored.getOrDefault(p.getUniqueId(), false))
					bar.addPlayer(p);
			});
			bar.setVisible(true);
			Bukkit.getScheduler().runTaskLater(this, () -> bar.removeAll(),
					Config.BOSS_BAR_CONTINUED * 20);
			break;
		case ACTION_BAR:
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (!ignored.getOrDefault(p.getUniqueId(), false)) {
					NMSUtils.sendActionBar(p, message.getMessage(), nmsVersion);
				}
			});

			break;
		default:
			return;
		}
	}

	public Updater getUpdater() {
		return updater;
	}

//	public Economy getEconomy() {
//		return economy;
//	}

	public Plugin getPlaceholderAPI() {
		return placeholderAPI;
	}

	public Message getMessage() {
		return message;
	}

	public boolean isServerChatIgnored(UUID uuid) {
		return ignored.getOrDefault(uuid, false);
	}

	public boolean changePlayerIgnored(UUID uuid) {
		ignored.putIfAbsent(uuid, Boolean.FALSE);
		return !ignored.put(uuid, !ignored.get(uuid));
	}

	public void PR(String str) {
		this.getLogger().info(str);
	}
	
	

}
