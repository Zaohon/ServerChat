package configuration;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import cn.blockmc.Zao_hon.ServerChat;

public class Lang {
	private static ServerChat plugin;
	public static String COMMAND_HEADING;
	public static String COMMAND_SETITEM;
	public static String COMMAND_GIVEPLAYER;
	public static String COMMAND_IGNORED;
	public static String COMMAND_RELOAD;
	public static String CHAT_LENTH_ERROR;
	public static String CHAT_MSG_EMPTY;
	public static String IGNORE_SERVERCHAT_ON;
	public static String IGNORE_SERVERCHAT_OFF;
	public static String RELOAD_COMPLETELY;
	public static String RECEIVE_HORN;
	public static String GIVE_PLAYER_HORN;
	public static String SUCCESS_SET_HORN;
	public static String HORN_CANT_BE_AIR;
	public static String NO_PERMISSION_IGNORE;
	public static String NO_PERMISSION;
	public static String ONLY_PLAYER_USE_COMMAND;
	public static String WITHOUT_AUTHENTICATED;
	public static String CHAT_IN_COOL_TIME;
	public static String HORN_IN_COOL_TIME;
	public static String HINT_OVERTIMEUSEHORN;
	public static String HINT_WHENUSINGHORN;
	public static String ALREADY_USING_HORN;
	public static String AUTO_COST_MONEY;
	public static String AUTO_COST_POINT;
	public static String AUTO_COST_FAILED;
	public static String AUTO_USE_SUCCESS;
	public static String AUTO_USE_FAILED;

	public static void reload() {
		plugin = ServerChat.getInstance();
		String langfile = plugin.getConfig().getString("Lang") + ".yml";
		File langFile = new File(plugin.getDataFolder(), langfile);
		if (!langFile.exists()) {
			plugin.saveResource(langfile, false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(langFile);

		COMMAND_HEADING = config.getString("Command_heading", "§2---ServerChat---");
		COMMAND_SETITEM = config.getString("Command_setitem", "§2/sc setitem   §6--将手上拿着的物品设置为喇叭");
		COMMAND_GIVEPLAYER = config.getString("Command_giveplayer", "§2/sc give [玩家]   §6--给予玩家一个喇叭,留空则给自己");
		COMMAND_IGNORED = config.getString("Command_ignored", "§2/sc ignore --§6无视所有跨服消息");
		COMMAND_RELOAD = config.getString("Command_reload: ", "§2/sc reload   §6--重载插件配置");
		CHAT_LENTH_ERROR = config.getString("ChatLenthError", "§c你说的话太长或者太短了");
		CHAT_MSG_EMPTY = config.getString("ChatMsgEmpty", "§c消息不能为空");
		IGNORE_SERVERCHAT_ON = config.getString("Ignore_ServerChat_On", "§e忽略所有跨服消息");
		IGNORE_SERVERCHAT_OFF = config.getString("Ignore_ServerChat_Off", "§e接收所有跨服消息");
		RELOAD_COMPLETELY = config.getString("ReloadCompletely", "§2ServerChat重载完成");
		RECEIVE_HORN = config.getString("ReceiveAHorn", "§b你收到了§e%number%§b个跨服喇叭");
		GIVE_PLAYER_HORN = config.getString("GivePlayerHorn", "§b已给予玩家§a%player%§e%number%§b跨服喇叭");
		SUCCESS_SET_HORN = config.getString("SuccessSetHornItem", "§b设置跨服喇叭成功");
		HORN_CANT_BE_AIR = config.getString("HornCantBeAir", " §c喇叭不能为空气！");
		NO_PERMISSION_IGNORE = config.getString("NoPermissionIgnore", "§c缺少屏蔽跨服聊天的权限");
		NO_PERMISSION = config.getString("NoPermission", "§c缺陷不足");
		ONLY_PLAYER_USE_COMMAND = config.getString("OnlyPlayerUseCommand", "§2该指令只能玩家使用");
		WITHOUT_AUTHENTICATED = config.getString("WithoutAuthenticated", "§c请先登录后再使用跨服喇叭");
		CHAT_IN_COOL_TIME = config.getString("ChatInCoolTime", "§c你还要再等§e%cooltime%§c秒才能再次发送跨服消息");
		HORN_IN_COOL_TIME = config.getString("HornInCoolTime", "§c你还要再等§e%cooltime%§c秒才能再次使用跨服喇叭");
		HINT_OVERTIMEUSEHORN = config.getString("Hint_OvertimeUseHorn", "§a由于长时间没有使用，跨服喇叭已自动退回");
		HINT_WHENUSINGHORN = config.getString("HintWhenUsingHorn", " §d请输入你要发送的喇叭内容");
		ALREADY_USING_HORN = config.getString("AlreadyUsingHorn", "§6你已经有一个正在使用的跨服喇叭了");
		AUTO_COST_MONEY = config.getString("Auto_Use_Money", "§a已自动购买跨服喇叭,花费§e%money%§a金钱");
		AUTO_COST_POINT = config.getString("Auto_Cost_Point", "§a已自动购买跨服喇叭,花费§d%point%点券");
		AUTO_COST_FAILED = config.getString("Auto_Cost_Failed", "§c缺少跨服喇叭");
		AUTO_USE_SUCCESS = config.getString("Auto_USE_HORN", "§a喊话成功,消耗一个跨服喇叭");
		AUTO_USE_FAILED = config.getString("Auto_Use_Failed", "§c缺少跨服喇叭");

	}

}
