package cn.blockmc.Zao_hon.Bukkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class MaterialManager {
	private static Map<String, String> map = new HashMap<String, String>();
	public static Material getMaterial(String str){
		try{
			return Material.getMaterial(str);
		}catch(Exception e){
			e.printStackTrace();
			return Material.getMaterial(map.get(str));
		}
	}
	static{
		map.put("SUNFLOWER", "YELLOWFLOWER");
	}
}
