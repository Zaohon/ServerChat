package cn.blockmc.Zao_hon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Message {
	private File msgfile = null;
	private ServerChat plugin;
	public String Command_heading = "§2---ServerChat---";
	public String Command_setitem = "§2/sc setitem   §6--将手上拿着的物品设置为喇叭";
	public String Command_giveplayer = "§2/sc give [玩家]   §6--给予玩家一个喇叭,留空则给自己";
	public String Command_ignored = "§2/sc ignore --§6无视所有跨服消息";
	public String Command_reload = "§2/sc reload   §6--重载插件配置";
	public String Chat_Lenth_Error = "§c你说的话太长或者太短了";
	public String IgnoredServerChat_On = "§e忽略所有跨服消息";
	public String IgnoredServerChat_Off = "§e接收所有跨服消息";
	public String ReloadCompletely = "§2ServerChat重载完成";
	public String ReceiveAHorn = "§b你获得了一个跨服喇叭";
	public String GivePlayerHorn = "§b已给予玩家§a%player%§b一个跨服喇叭";
	public String SuccessSetHornItem = "§b设置跨服喇叭成功";
	public String HornCantBeAir = "§c喇叭不能为空气！";
	public String NoPermissionIgnore = "§c你还没有权限忽略跨服消息";
	public String NoPermission = "§c权限不足";
	public String OnlyPlayerUseCommand = "§2该插件指令只能玩家使用";
	public String WithoutAuthenticated = "§c请先登录后再使用跨服喇叭";
	public String PrefixChatInCoolTime = "§c你还要再等§e%cooltime%§c秒才能再次发送跨服喇叭";
	public String HintOvertimeUseHorn = "§a由于长时间没有使用，跨服喇叭已自动退回";
	public String HintWhenUsingHorn = "§d请输入你要发送的喇叭内容";
	public String AlreadyUsingHorn = "§6你已经有一个正在使用的跨服喇叭了";

	public Message(ServerChat plugin) {
		this.plugin = plugin;
	}

	public void load() {
		if (msgfile == null) {
			msgfile = new File(plugin.getDataFolder(), "Message.yml");
		}
		if (!msgfile.exists()) {
			plugin.PR("没有找到语言文件..");
			plugin.PR("正在创建新的..");
			this.saveToFile();
			return;

		}
		Configuration config = YamlConfiguration.loadConfiguration(msgfile);
		Field[] msgfields = this.getClass().getFields();
		boolean update = false;
		for (int i = 0; i < msgfields.length; i++) {
			Field f = msgfields[i];
			if (f.getName().equals("msgfile") || f.getName().equals("plugin")) {
				continue;
			}
			try {
				String msg = config.getString(f.getName());
				if (msg == null) {
					plugin.PR("加载语言文件出错！请重新修改" + f.getName());
					update = true;
				} else {
					f.set(this, config.getString(f.getName()));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(update){
			this.saveToFile();
		}
		//
		// Command_heading = config.getString("Command_heading");
		// Command_setitem = config.getString("Command_setitem");
		// Command_giveplayer = config.getString("Command_giveplayer");
		// Command_reload = config.getString("Command_reload");
		// ReloadCompletely = config.getString("ReloadCompletely");
		// ReceiveAHorn = config.getString("ReceiveAHorn");
		// GivePlayerHorn = config.getString("GivePlayerHorn");
		// SuccessSetHornItem = config.getString("SuccessSetHornItem");
		// HornCantBeAir = config.getString("HornCantBeAir");
		// NoPermission = config.getString("NoPermission");
		// OnlyPlayerUseCommand = config.getString("OnlyPlayerUseCommand");
		// WithoutAuthenticated = config.getString("WithoutAuthenticated");
		// PrefixChatInCoolTime = config.getString("PrefixChatInCoolTime");
		// HintOvertimeUseHorn = config.getString("HintOvertimeUseHorn");
		// HintWhenUsingHorn = config.getString("HintWhenUsingHorn");
		// AlreadyUsingHorn = config.getString("AlreadyUsingHorn");
	}
	private void saveToFile(){
		FileConfiguration config = YamlConfiguration.loadConfiguration(msgfile);
		Field[] msgfields = this.getClass().getFields();
		for (int i = 0; i < msgfields.length; i++) {
			Field f = msgfields[i];
			if (f.getName().equals("msgfile") || f.getName().equals("plugin")) {
				continue;
			}
			try {
				config.set(f.getName(), f.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		try {
			config.save(msgfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
