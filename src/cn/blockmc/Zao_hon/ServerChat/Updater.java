package cn.blockmc.Zao_hon.ServerChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import cn.blockmc.Zao_hon.ServerChat.configuration.Config;
import cn.blockmc.Zao_hon.ServerChat.configuration.Message;

public class Updater {
	private static final int PROJECT = 43494;
	private static final long PERIOD = 20 * 60 * 60;
	private ServerChat plugin;
	private String latestVersion;
	private boolean outdated = true;

	public Updater(ServerChat plugin) {
		this.plugin = plugin;
	}

	public void hourlyUpdateCheck(final CommandSender sender, boolean update, boolean silent) {
		if (update) {
			plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> checkForUpdate(sender, silent),
					20, PERIOD);
		}

	}

	public void checkForUpdate(final CommandSender sender, boolean silent) {
		if (!silent) {
			Message.senderSendMessage(sender, ServerChat.PREFIX + Message.getString("update_latest_version_geting"));
		}
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + PROJECT);
				URLConnection connection = url.openConnection();
				latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				String nowVersion = plugin.getDescription().getVersion();
				outdated = !latestVersion.equals(nowVersion);
				if (outdated) {
					Message.senderSendMessage(sender, Message.getString("update_player"));
				}else {
					Message.senderSendMessage(sender, Message.getString("update_newest_version_already"));
				}

			} catch (Exception exception) {
				exception.printStackTrace();
				Message.senderSendMessage(sender, Message.getString("update_error"));
			}
		});

	}

}
