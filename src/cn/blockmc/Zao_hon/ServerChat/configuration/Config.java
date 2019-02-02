package cn.blockmc.Zao_hon.ServerChat.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;

public class Config {
	public static String LANG = "zh_cn";
	public static int CONFIG_VERSION = 1;
	public static String THIS_SERVER_NAME = "NoneServerName";
	public static boolean CHAT_PREFIX_ENABLE = true;
	public static String CHAT_PREFIX = "!";
	public static int CHAT_COOL_TIME = 30;
	public static boolean FREE_CHAT = false;
	public static boolean ITEM_BUY_MENU = true;
	public static boolean AUTO_USE = true;
	public static boolean AUTO_COST = false;
	public static boolean COST_ENABLE = true;
	public static int COST_MONEY = 50;
	public static int COST_PLAYER_POINT = 10;
//	public static boolean HORNSET_ALLOW_COOMAND_BUY = true;
	public static boolean HORNSET_BUY_MENU = true;
	public static int HORNSET_RESPON_TIME = 30;
	public static int HORNSET_COOL_TIME = 10;
	public static boolean BOSS_BAR_ENABLE = false;
	public static String BOSS_BAR_COLOR = "GREEN";
	public static String BOSS_BAR_STYLE = "SOLID";
	public static List<String> BOSS_BAR_FLAGS = new ArrayList<String>();
	public static int BOSS_BAR_CONTINUED = 6;
	public static String BOSS_BAR_MESSAGE = "%server% §a§l%player% §7如此说道: §r%message%";
	public static boolean ACTION_BAR_ENABLE = true;
	public static String ACTION_BAR_MESSAGE = "§a§l%player% : %message%";
	public static boolean CHAT_ENABLE = true;
	public static String CHAT_MESSAGE = "§7<%server%§7> §a§l%player% §7says §r%message%";
	public static int LENTH_LIMIT_MIN = 0;
	public static int LENTH_LIMIT_MAX = 40;
	public static List<String> SHIELD_MESSAGES = new ArrayList<String>();
	public static String SHILED_REPLACES = "♥";
	public static Boolean AUTO_UPDATE_CHECK = true;
	

	public static void reload() {
		ServerChat plugin = ServerChat.getInstance();
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		LANG = config.getString("Lang", "zh_cn");
		CONFIG_VERSION = config.getInt("Config_Version", 1);
		THIS_SERVER_NAME = config.getString("This_Server_Name", "NoneServerName");
		CHAT_PREFIX_ENABLE = config.getBoolean("ChatPrefixEnable", true);
		CHAT_PREFIX = config.getString("ChatPrefix", "!");
		CHAT_COOL_TIME = config.getInt("ChatCoolTime", 30);
		FREE_CHAT = config.getBoolean("FreeChat", false);
		ITEM_BUY_MENU = config.getBoolean("ItemBuyMenu", true);
		AUTO_USE = config.getBoolean("AutoUse", true);
		AUTO_COST = config.getBoolean("AutoCost", false);
		COST_ENABLE = config.getBoolean("Cost.Enable", true);
		COST_MONEY = config.getInt("Cost.Money", 50);
		COST_PLAYER_POINT = config.getInt("Cost.PlayerPoint", 10);
//		HORNSET_ALLOW_COOMAND_BUY = config.getBoolean("Hornset.AllowCommandBuy", true);
		HORNSET_BUY_MENU = config.getBoolean("HornSet.BuyMenu", true);
		HORNSET_RESPON_TIME = config.getInt("HornSet.ResponTime", 30);
		HORNSET_COOL_TIME = config.getInt("HornSet.HornCoolTime", 10);
		BOSS_BAR_ENABLE = config.getBoolean("BossBar", false);
		BOSS_BAR_COLOR = config.getString("BossBarColor", "GREEN");
		BOSS_BAR_STYLE = config.getString("BossBarStyle", "SOLID");
		BOSS_BAR_FLAGS = config.getStringList("BossBarFlags");
		BOSS_BAR_CONTINUED = config.getInt("BossBarContinued", 6);
		BOSS_BAR_MESSAGE = config.getString("BossBarMessage", "%server% §a§l%player% §7如此说道: §r%message%");
		ACTION_BAR_ENABLE = config.getBoolean("ActionBar", true);
		ACTION_BAR_MESSAGE = config.getString("ActionBarMessage", "§a§l%player% : %message%");
		CHAT_ENABLE = config.getBoolean("Chat", true);
		CHAT_MESSAGE = config.getString("ChatMessage", "§7<%server%§7> §a§l%player% §7says §r%message%");
		LENTH_LIMIT_MIN = config.getInt("LenthLimit.Min", 0);
		LENTH_LIMIT_MAX = config.getInt("LenthLimit.Max", 40);
		SHIELD_MESSAGES = config.getStringList("ShieldMessages");
		SHILED_REPLACES = config.getString("ShieldReplaces", " ♥");
		AUTO_UPDATE_CHECK = config.getBoolean("AutoUpdateCheck",true);
	}

}
