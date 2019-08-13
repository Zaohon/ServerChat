package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class IgnoreCommand implements ICommand {
	private ServerChat plugin;

	public IgnoreCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "ignore";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "ServerChat.Ignore";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { Message.getString("command_description_ignore") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_ignore");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
		if (!plugin.changePlayerIgnored(player.getUniqueId())) {
//			Lang.sendMsg(p, Lang.IGNORE_SERVERCHAT_ON);
			Message.playerSendMessage(player, Message.getString("command_tip_ignore_on"));
		} else {
//			Lang.sendMsg(p, Lang.IGNORE_SERVERCHAT_OFF);?
			Message.playerSendMessage(player, Message.getString("command_tip_ignore_off"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}
}
