package cn.blockmc.Zao_hon.ServerChat.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CorrespondMessage {
	private final String senderName;
	private final String serverName;
	private final String msg;
	private final MessageType type;
	private final long sendingTime;
	private final List<String> hoverTexts;

	public CorrespondMessage(final String senderName, final String serverName, final String msg, final MessageType type,
			long sendingTime, final List<String> hoverTexts) {
		this.senderName = senderName;
		this.serverName = serverName;
		this.type = type;
		this.msg = msg;
		this.sendingTime = sendingTime;
		this.hoverTexts = hoverTexts;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getMessage() {
		return msg;
	}

	public MessageType getMessageType() {
		return type;
	}

	public long getSendingTime() {
		return sendingTime;
	}

	public static CorrespondMessage getByBytes(byte[] bytes) throws IOException {
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(bytes));
		Long sendingTime = msgin.readLong();
		String senderName = msgin.readUTF();
		String serverName = msgin.readUTF();
		String msg = msgin.readUTF();
		MessageType type = MessageType.valueOf(msgin.readUTF());
		int size = msgin.readInt();
		List<String> list = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			list.add(msgin.readUTF());
		}
		return new CorrespondMessage(senderName, serverName, msg, type, sendingTime, list);
	}

	public static byte[] toBytes(CorrespondMessage message) throws IOException {
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		msgout.writeLong(message.getSendingTime());
		msgout.writeUTF(message.getSenderName());
		msgout.writeUTF(message.getServerName());
		msgout.writeUTF(message.getMessage());
		msgout.writeUTF(message.getMessageType().name());
		msgout.writeInt(message.getHoverTexts().size());
		for (String s : message.hoverTexts) {
			msgout.writeUTF(s);
		}
		return msgbytes.toByteArray();

	}

	public List<String> getHoverTexts() {
		return hoverTexts;
	}

}
