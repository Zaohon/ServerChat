package cn.blockmc.ServerChat.Zao_hon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands implements CommandExecutor {
	private ServerChat plugin;

	public Commands(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§2该插件指令只能玩家使用");
			return true;
		}
		Player p = (Player) sender;
		if (!p.hasPermission("ServerChat.Admin")) {
			p.sendMessage("§c权限不足");
			return true;
		}
		if (args.length >= 1) {
			String a1 = args[0];
			if (a1.equals("setitem")) {
				ItemStack trumple = p.getInventory().getItemInMainHand();
				if (trumple == null || trumple.getType() == Material.AIR) {
					p.sendMessage("§c喇叭不能为空气！");
					return true;
				}
				plugin.getConfig().set("Item.Material", trumple.getType().name());
				plugin.getConfig().set("Item.Name", trumple.getItemMeta().getDisplayName());
				plugin.getConfig().set("Item.Lore", trumple.getItemMeta().getLore());
				plugin.saveConfig();
				plugin.reloadConfig();
				plugin.updateTrumpleItem();
				p.sendMessage("§b设置跨服喇叭成功");
				return true;
			} else if (a1.equals("give")) {
				Player gp = args.length == 1 ? p : Bukkit.getPlayer(args[1]);
				gp.getInventory().addItem(plugin.getHorn().clone());
				if (gp != p) {
					p.sendMessage("§b已给予玩家§a" + gp.getDisplayName() + "§b一个跨服喇叭");
				}
				gp.sendMessage("§b你获得了一个跨服喇叭");
				return true;
			} else if (a1.equals("reload")) {
				plugin.reloadConfig();
				plugin.updateTrumpleItem();
				p.sendMessage("§2ServerChat重载完成");
				return true;
			}
		}
		p.sendMessage("§2---ServerChat---");
		p.sendMessage("§2/sc setitem   §6--将手上拿着的物品设置为喇叭");
		p.sendMessage("§2/sc give [玩家]   §6--给予玩家一个喇叭,留空则给自己");
		p.sendMessage("§2/sc reload   §6--重载插件配置");

		return true;
	}

}
