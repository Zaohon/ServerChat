package cn.blockmc.Zao_hon.Bukkit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class MessageListener implements PluginMessageListener {
	private ServerChat plugin;

	public MessageListener(ServerChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPluginMessageReceived(String tag, Player player, byte[] data) {
		if (!tag.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
		String channel = in.readUTF();

		if (channel.equals("ServerChat")) {
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				String servername = msgin.readUTF();
				String name = msgin.readUTF();
				String msg = msgin.readUTF();

				plugin.sendServerChat(servername, name, msg);

			} catch (IOException e) {
				plugin.getLogger().info("接收消息时失败，请上报mcbbs");
				e.printStackTrace();
			}
		}
	}

}
