package cn.blockmc.Zao_hon.ServerChat.Utils;

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
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);

			try {
				CorrespondMessage message = CorrespondMessage.getByBytes(msgbytes);
				if (message.getSendingTime() < System.currentTimeMillis() - 5000) {
					return;
				}
				plugin.sendMsg(message);
			} catch (IOException e) {
				plugin.getLogger().info("接收消息时失败，请上报mcbbs . Message transport error, report it to spigotmc!");
				e.printStackTrace();
			}

//			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
//			try {
//				Long time = msgin.readLong();
//				String msgType = msgin.readUTF();
//				MessageType type = MessageType.valueOf(msgType);
//				if (time < System.currentTimeMillis() - 5000) {
//					return;
//				}
//				String msg = msgin.readUTF();
//				plugin.sendMsg(msg, type);
//			} catch (IOException e) {
//				plugin.getLogger().info("接收消息时失败，请上报mcbbs . Message transport error, report it to spigotmc!");
//				e.printStackTrace();
//			}
		}
	}

}
