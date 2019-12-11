package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class GiveCommand implements ICommand {
	private ServerChat plugin;

	public GiveCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "give";
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
		return new String[] { Message.getString("command_description_giveplayer") };
	}

	@Override
	public String getDescription() {
		return Message.getString("command_description_giveplayer");
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
		Player gp = args.length == 0 ? p : Bukkit.getPlayer(args[0]) == null ? p : Bukkit.getPlayer(args[0]);
		int number = 1;
		try {
			number = Integer.valueOf(args[1]);
		} catch (Exception e) {
			// ignore
		}
		ItemStack horn = plugin.getHorn().clone();
		horn.setAmount(number);
		gp.getInventory().addItem(horn);
		if (gp != p) {
			Message.playerSendMessage(p,
					Message.getString("command_tip_giveplayer_give", "%player%", gp.getName(), "%number%", number));
		}
		Message.playerSendMessage(p, Message.getString("command_tip_giveplayer_recieve", "%number%", number));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
