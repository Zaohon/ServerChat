package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class ReloadCommand implements ICommand{
	private ServerChat plugin;
	public ReloadCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "ServerChat.Admin";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {Message.getString("command_description_reload")};
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_reload");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		Config.reload();
		plugin.getMessage().setLanguage(Config.LANG);
//		Lang.sendMsg(sender, Lang.COMMAND_RELOAD_COMPLETELY);
		Message.senderSendMessage(sender,  Message.getString("command_tip_reload_success"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
