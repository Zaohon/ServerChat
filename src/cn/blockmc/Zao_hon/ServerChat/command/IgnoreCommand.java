package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;

public class IgnoreCommand implements ICommand{
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
		return new String[] {Lang.COMMAND_IGNORED};
	}

	@Override
	public String getDescription() {
		return Lang.COMMAND_IGNORED;
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
		Player p = (Player) sender;
		if (!plugin.changePlayerIgnored(p.getUniqueId())) {
			Lang.sendMsg(p, Lang.IGNORE_SERVERCHAT_ON);
		} else {
			Lang.sendMsg(p, Lang.IGNORE_SERVERCHAT_OFF);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}
}
