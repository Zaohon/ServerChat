package cn.blockmc.Zao_hon.ServerChat.chat;

import org.bukkit.entity.Player;

public interface PrefixChatHandler {
	public String getPrefix();
	public String getChatType();
	
	//return true if handle successfully
	public boolean handle(Player player ,String message);
}
