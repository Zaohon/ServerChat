package cn.blockmc.Zao_hon.Bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DefaultItem {
	private static ItemStack horn;
	public static ItemStack getHorn(){
		return horn;
	}
	static{
		horn = new ItemStack(MaterialManager.getMaterial("SUNFLOWER"));
		ItemMeta meta = horn.getItemMeta();
		meta.setDisplayName("°Ïa°Ïl¥Û¿Æ∞»");
		horn.setItemMeta(meta);
	}

}
