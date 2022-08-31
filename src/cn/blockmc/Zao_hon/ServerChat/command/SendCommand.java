package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.HashSet;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.Utils.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class SendCommand implements ICommand {
	private ServerChat plugin;

	public SendCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "send";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { Message.getString("command_description_send") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_send");
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
		int lenth = args.length;
		String msg = lenth >= 1 ? args[0] : "";
		if (sender instanceof Player) {
			Player p = (Player) sender;
			AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(false, p,
					(Config.CHAT_PREFIX_ENABLE ? Config.CHAT_PREFIX : "") + msg, new HashSet<Player>());
			plugin.getServer().getPluginManager().callEvent(e);
			return true;
		} else {
			if (plugin.getServer().getOnlinePlayers().isEmpty()) {
				Message.senderSendMessage(sender, Message.getString("command_error_at_least_one_player"));
			} else {
				BungeeUtil.sendServerChat(plugin, sender, msg);
			}
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
