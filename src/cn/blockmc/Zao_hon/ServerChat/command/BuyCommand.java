package cn.blockmc.Zao_hon.ServerChat.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Lang;

public class BuyCommand implements ICommand{
	private ServerChat plugin;
	public BuyCommand(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "buy";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "ServerChat.Buy";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {Lang.COMMAND_BUY};
	}

	@Override
	public String getDescription() {
		return Lang.COMMAND_BUY;
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
		int number = 1;
		try {
			number = Integer.valueOf(args[0]);
		} catch (Exception e) {
			// ignore
		}
		int mc = Config.COST_MONEY;
		double mn = plugin.getEconomy() == null ? -1 : plugin.getEconomy().getBalance(p);
		if (mc != 0 && mn >= mc) {
			plugin.getEconomy().depositPlayer(p, mc);
			Lang.sendMsg(p, Lang.COMMAND_BUY_COST_MONEY.replace("%number%", number + "").replace("%money%",
					mc * number + ""));
			ItemStack horn = plugin.getHorn().clone();
			horn.setAmount(number);
			p.getInventory().addItem(horn);
		} else {
			Lang.sendMsg(p, Lang.COMMAND_BUY_COST_FAILED);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}
}
