package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;

public class SendCommand implements ICommand{
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
		return new String[] {Lang.COMMAND_SEND};
	}

	@Override
	public String getDescription() {
		return Lang.COMMAND_SEND;
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
		String msg = lenth>=1?args[0]:"";
		if (sender instanceof Player){
			AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(false,(Player)sender,(Config.CHAT_PREFIX_ENABLE?Config.CHAT_PREFIX:"")+msg,null);
			plugin.getServer().getPluginManager().callEvent(e);
			return true;
		}else{
			plugin.sendServerChat(Config.THIS_SERVER_NAME, "¡ìeServer", msg);
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
