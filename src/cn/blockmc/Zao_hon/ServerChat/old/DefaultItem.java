package cn.blockmc.Zao_hon.ServerChat.old;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cn.blockmc.Zao_hon.ServerChat.MaterialManager;

@Deprecated
public class DefaultItem {
	private static ItemStack horn;

	public static ItemStack getHorn() {
		return horn;
	}

	static {
		horn = new ItemStack(MaterialManager.getMaterial("SUNFLOWER"));
		ItemMeta meta = horn.getItemMeta();
		meta.setDisplayName("��a��l������");
		horn.setItemMeta(meta);
	}

}
