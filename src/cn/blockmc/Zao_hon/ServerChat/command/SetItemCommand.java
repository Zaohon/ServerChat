package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class SetItemCommand implements ICommand {
	private ServerChat plugin;

	public SetItemCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "setitem";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "si" };
	}

	@Override
	public String getPermission() {
		return "ServerChat.Admin";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return null;
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_setitem");
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
		ItemStack trumple = player.getInventory().getItemInMainHand();
		if (trumple == null || trumple.getType() == Material.AIR) {
			Message.playerSendMessage(player, Message.getString("command_tip_setitem_air"));
			return true;
		}
		plugin.setHorn(trumple);
		Message.playerSendMessage(player, Message.getString("command_tip_setitem_success"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
