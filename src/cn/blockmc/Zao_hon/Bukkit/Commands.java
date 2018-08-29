package cn.blockmc.Zao_hon.Bukkit;

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
			sender.sendMessage(plugin.Message.OnlyPlayerUseCommand);
			return true;
		}
		Player p = (Player) sender;
		if (args.length >= 1) {
			String a1 = args[0];
			if (a1.equals("setitem")) {
				if (!p.hasPermission("ServerChat.Admin")) {
					p.sendMessage(plugin.Message.NoPermission);
					return true;
				}
				ItemStack trumple = p.getInventory().getItemInMainHand();
				if (trumple == null || trumple.getType() == Material.AIR) {
					p.sendMessage(plugin.Message.HornCantBeAir);
					return true;
				}
				plugin.setHorn(trumple);
				p.sendMessage(plugin.Message.SuccessSetHornItem);
				return true;
			} else if (a1.equals("give")) {
				if (!p.hasPermission("ServerChat.Admin")) {
					p.sendMessage(plugin.Message.NoPermission);
					return true;
				}
				Player gp = args.length == 1 ? p : Bukkit.getPlayer(args[1]);
				gp.getInventory().addItem(plugin.getHorn().clone());
				if (gp != p) {
					p.sendMessage(plugin.Message.GivePlayerHorn.replace("%player%", p.getName()));
				}
				gp.sendMessage(plugin.Message.ReceiveAHorn);
				return true;
			} else if (a1.equals("reload")) {
				if (!p.hasPermission("ServerChat.Admin")) {
					p.sendMessage(plugin.Message.NoPermission);
					return true;
				}
				plugin.Message.load();
				plugin.loadHorn();
				p.sendMessage(plugin.Message.ReloadCompletely);
				return true;
			} else if(a1.equals("ignore")){
				if(!p.hasPermission("ServerChat.Ignore")){
					p.sendMessage(plugin.Message.NoPermissionIgnore);
					return true;
				}
				if(!plugin.changePlayerIgnored(p.getUniqueId())){
					p.sendMessage(plugin.Message.IgnoredServerChat_On);
				}else{
					p.sendMessage(plugin.Message.IgnoredServerChat_Off);
				}
				return true;
			}
		}
		p.sendMessage(plugin.Message.Command_heading);
		p.sendMessage(plugin.Message.Command_setitem);
		p.sendMessage(plugin.Message.Command_giveplayer);
		p.sendMessage(plugin.Message.Command_ignored);
		p.sendMessage(plugin.Message.Command_reload);

		return true;
	}

}
