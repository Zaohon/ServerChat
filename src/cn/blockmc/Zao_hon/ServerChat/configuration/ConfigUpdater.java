package cn.blockmc.Zao_hon.ServerChat.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;

public class ConfigUpdater {
	public static int CONFIG_VERSION = 2;

	public static void configUpdate() {
		ServerChat plugin = ServerChat.getInstance();
		try {
			File file = new File(plugin.getDataFolder(), "confi.yml");
			if (!file.exists()) {
				file.createNewFile();
			}
			InputStream is = plugin.getResource("config.yml");
			FileOutputStream os = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int lenth;
			while ((lenth = is.read(b)) > 0) {
				String str = new String(b);
				plugin.PR(str);
				os.write(b, 0, lenth);
			}
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
