package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;

public class SetItemCommand implements ICommand{
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
		return new String[] {"si"};
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
		return Lang.COMMAND_SETITEM;
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
		Player p =  (Player) sender;
		ItemStack trumple = p.getInventory().getItemInMainHand();
		if (trumple == null || trumple.getType() == Material.AIR) {
			Lang.sendMsg(p, Lang.HORN_CANT_BE_AIR);
			return true;
		}
		plugin.setHorn(trumple);
		Lang.sendMsg(p, Lang.SUCCESS_SET_HORN);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
