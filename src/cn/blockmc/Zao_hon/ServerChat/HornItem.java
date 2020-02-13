package cn.blockmc.Zao_hon.ServerChat;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HornItem {
	private static final ItemStack DEFAULT_ITEM = new ItemStack(MaterialManager.getMaterial("SUNFLOWER")) {
		{
			ItemMeta meta = this.getItemMeta();
			meta.setDisplayName("°Ïa°Ïl¥Û¿Æ∞»");
			this.setItemMeta(meta);
		}
	};
	private static ItemStack hornItem;
	private static File hornFile;

	public static void init(ServerChat plugin) {
		hornFile = new File(plugin.getDataFolder(), "Item.yml");
		if (!hornFile.exists()) {
			try {
				hornFile.createNewFile();
				hornItem = DEFAULT_ITEM.clone();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loadHorn();
	}

	public static void loadHorn() {
		hornItem = YamlConfiguration.loadConfiguration(hornFile).getItemStack("Item");
		if (hornItem == null) {
			hornItem = DEFAULT_ITEM.clone();
		}
	}

	public static void saveHorn() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(hornFile);
		try {
			config.set("Item", hornItem);
			config.save(hornFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setHornItem(ItemStack item) {
		hornItem = item.clone();
		saveHorn();
	}

	public static ItemStack getHornItem() {
		return hornItem.clone();
	}

	public static boolean isHornItem(ItemStack item) {
		return item.isSimilar(hornItem);
	}

}
