package cn.blockmc.Zao_hon.ServerChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
	public static String getLatestVersion() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=43494");
			URLConnection conn = url.openConnection();
			return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
		} catch (Exception e) {}
		return ServerChat.getInstance().getDescription().getVersion();
	}
}
