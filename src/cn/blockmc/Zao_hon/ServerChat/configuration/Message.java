package cn.blockmc.Zao_hon.ServerChat.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class Message {
	private ServerChat plugin;
	// Quote from Mobhunting , author Rocologo
	private static String PREFIX = "[ServerChat]";
	private static Map<String, String> mTranslationTable;
	private static String[] sources = new String[] { "zh_cn.lang", "en_us.lang" };
	private static String[] mValidEncodings = new String[] { "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-8", "ISO646-US" };

	public Message(ServerChat plugin) {
		this.plugin = plugin;
		exportDefaultLanguage(plugin);
	}

	public static void exportDefaultLanguage(ServerChat plugin) {
		File folder = new File(plugin.getDataFolder(), "lang");
		if (!folder.exists())
			folder.mkdirs();

		for (String source : sources) {
			File dest = new File(folder, source);
			if (!dest.exists()) {
				Bukkit.getServer().getConsoleSender()
						.sendMessage(PREFIX + " Creating language file " + source + " from JAR.");
				plugin.saveResource("lang/" + source, false);
			} else {
				if (!injectChanges(plugin.getResource("lang/" + source),
						new File(plugin.getDataFolder(), "lang/" + source))) {
					plugin.saveResource("lang/" + source, true);
				}
			}
			mTranslationTable = loadLang(dest);
		}
	}

	public static boolean injectChanges(InputStream source, File onDisk) {
		try {
			Map<String, String> sourceMap = loadLang(source, "UTF-8");
			Map<String, String> diskMap = loadLang(onDisk);

			Map<String, String> newEntries = new HashMap<String, String>();
			for (String key : sourceMap.keySet()) {
				if (!diskMap.containsKey(key)) {
					newEntries.put(key, sourceMap.get(key));
				}
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(onDisk, true), StandardCharsets.UTF_8));

				for (Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue());
				writer.close();
				Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + "已更新" + onDisk.getName() + "中缺失的语言消息");

			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static Map<String, String> loadLang(File file) {
		Map<String, String> map = null;
		try {
			FileInputStream instream = new FileInputStream(file);
			String encoding = detectEncoding(file);
			map = loadLang(instream, encoding);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Map<String, String> loadLang(InputStream stream, String encoding) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
		while (reader.ready()) {
			String line = reader.readLine();
			if (line == null)
				continue;
			int index = line.indexOf('=');
			if (index == -1)
				continue;
			String key = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();

			map.put(key, value);
		}
		reader.close();
		return map;
	}

	private static Pattern mDetectEncodingPattern = Pattern.compile("^[a-zA-Z\\.\\-0-9_]+=.+$");

	private static String detectEncoding(File file) throws IOException {
		for (String charset : mValidEncodings) {
			FileInputStream input = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
			String line = null;
			boolean ok = true;

			while (reader.ready()) {
				line = reader.readLine();
				if (line == null || line.trim().isEmpty())
					continue;

				if (!mDetectEncodingPattern.matcher(line.trim()).matches())
					ok = false;
			}

			reader.close();

			if (ok)
				return charset;
		}

		return "UTF-8";
	}

	public void setLanguage(String lang) {
		File file = new File(ServerChat.getInstance().getDataFolder(), "lang/" + lang);
		if (!file.exists()) {
			plugin.PR(lang + "不存在,已创建一个默认语言文件,可自行翻译成本国语言");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (file.exists()) {
			InputStream resource = plugin.getResource("lang/zh_cn.lang");
			injectChanges(resource, file);
			mTranslationTable = loadLang(file);
		} else {
			plugin.PR("无法加载" + lang + "文件,或许出了什么大问题");
		}

		if (mTranslationTable == null) {
			mTranslationTable = new HashMap<String, String>();
			plugin.PR("已创建一个空翻译表");
			;
		}
	}

	public static String getString(String key) {
		String value = mTranslationTable.get(key);
		if (value == null) {
			Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + "翻译表缺少" + key);
			throw new MissingResourceException("", "", key);
		}

		return key;

	}

	public static void playerSendMessage(Player player, String message) {
		if (isEmpty(message))
			return;
		player.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
	}

	public static void senderSendMessage(CommandSender sender, String message) {
		if (isEmpty(message))
			return;
		if (sender instanceof Player)
			((Player) sender).sendMessage(PlaceholderAPI.setPlaceholders((Player) sender, message));
		else
			sender.sendMessage(message);
	}

	private static boolean isEmpty(String message) {
		message = ChatColor.stripColor(message);
		return message.isEmpty();
	}

}
