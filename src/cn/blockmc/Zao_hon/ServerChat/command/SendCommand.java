package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cn.blockmc.Zao_hon.ServerChat.BungeeUtil;
import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;
import cn.blockmc.Zao_hon.ServerChat.old.Lang;

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
		String msg = lenth>=1?args[0]:"";
		if (sender instanceof Player){
			AsyncPlayerChatEvent e = new AsyncPlayerChatEvent(false,(Player)sender,(Config.CHAT_PREFIX_ENABLE?Config.CHAT_PREFIX:"")+msg,null);
			plugin.getServer().getPluginManager().callEvent(e);
			return true;
		}else{
			if(plugin.getServer().getOnlinePlayers().isEmpty()){
//			sender.sendMessage("服务器需至少有一个人才能发送跨服消息");	
			Message.senderSendMessage(sender, Message.getString("command_error_at_least_one_player"));
			}else{
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
