package cn.blockmc.Zao_hon.Bukkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class MaterialManager {
	private static Map<String, String> map = new HashMap<String, String>();

	public static Material getMaterial(String name) {
		try {
			return Material.valueOf(name);
		} catch (IllegalArgumentException e) {
			try {
				return Material.valueOf(map.get(name));
			} catch (IllegalArgumentException ignored) {
				return null;
			}
		}
	}

	static {
		map.put("SUNFLOWER", "DOUBLE_PLANT");
	}
}
