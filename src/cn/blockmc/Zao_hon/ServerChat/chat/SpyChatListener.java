package cn.blockmc.Zao_hon.ServerChat.chat;

import org.bukkit.entity.Player;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;

public class SpyChatListener implements PrefixChatHandler{
	private ServerChat plugin;
	public SpyChatListener(ServerChat plugin) {
		this.plugin = plugin;
	}
	@Override
	public String getPrefix() {
		return ">>";
	}
	@Override
	public String getChatType() {
		return "SpyChat";
	}
	@Override
	public boolean handle(Player player, String message) {
		
		
		
		return true;
	}
	
	

}
