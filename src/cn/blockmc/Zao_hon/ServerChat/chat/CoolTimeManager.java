package cn.blockmc.Zao_hon.ServerChat.chat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import cn.blockmc.Zao_hon.ServerChat.configuration.Config;

public class CoolTimeManager {
	private static HashMap<UUID, Long> horncooltime = new HashMap<UUID, Long>();
	private static HashMap<UUID, Long> chatcooltime = new HashMap<UUID, Long>();


	public static int remainHornCoolTime(UUID uuid) {
		int cooltime = 0;
		if (horncooltime.containsKey(uuid)) {
			Long t = horncooltime.get(uuid);
			if (t + 1000 * Config.HORNSET_COOL_TIME > System.currentTimeMillis()) {
				cooltime = (int) ((t + 1000 * Config.HORNSET_COOL_TIME - System.currentTimeMillis()) / 1000);
			}
		}
		return cooltime;
	}

	public static int remainChatCoolTime(UUID uuid) {
		int cooltime = 0;
		if (chatcooltime.containsKey(uuid)) {
			Long t = chatcooltime.get(uuid);
			if (t + 1000 * Config.CHAT_COOL_TIME > System.currentTimeMillis()) {
				cooltime = (int) ((t + 1000 * Config.CHAT_COOL_TIME - System.currentTimeMillis()) / 1000);
			}
		}
		return cooltime;
	}

	public static void updateChatCoolTime(UUID uuid) {
		chatcooltime.put(uuid, System.currentTimeMillis());
	}

	public static void updateHornCoolTime(UUID uuid) {
		horncooltime.put(uuid, System.currentTimeMillis());
	}

}
