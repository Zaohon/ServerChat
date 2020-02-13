package cn.blockmc.Zao_hon.ServerChat.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cn.blockmc.Zao_hon.ServerChat.ServerChat;

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
			String msgType = in.readUTF();
			MessageType type = MessageType.valueOf(msgType);
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				Long time = msgin.readLong();
				if (time < System.currentTimeMillis() - 5000) {
					return;
				}
				String msg = msgin.readUTF();
				plugin.sendMsg(msg, type);
			} catch (IOException e) {
				plugin.getLogger().info("������Ϣʱʧ�ܣ����ϱ�mcbbs . Message transport error, report it to spigotmc!");
				e.printStackTrace();
			}
		}
	}
	


}