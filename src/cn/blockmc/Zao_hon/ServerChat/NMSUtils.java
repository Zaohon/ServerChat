package cn.blockmc.Zao_hon.ServerChat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {
	public static Class<?> PACKET_PLAY_OUT_CHAT = getNMSClass("PacketPlayOutChat");
	public static Class<?> CHAT_COMPONENT_TEXT = getNMSClass("ChatComponentText");
	public static Class<?> Chat_Message_Type = getNMSClass("ChatMessageType");

	public static void sendActionBar(Player p, String message) {
		Object packet = null;
		try {
			packet = PACKET_PLAY_OUT_CHAT.getConstructor(new Class[] { getNMSClass("IChatBaseComponent"),byte.class})
					.newInstance(CHAT_COMPONENT_TEXT.getConstructor(String.class).newInstance(message), (byte) 2);

		} catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			try {
				packet = PACKET_PLAY_OUT_CHAT.getConstructor(new Class[] { getNMSClass("IChatBaseComponent"),Chat_Message_Type})
						.newInstance(CHAT_COMPONENT_TEXT.getConstructor(String.class).newInstance(message),Chat_Message_Type.getEnumConstants()[2]);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		sendPacket(p,packet);
	}

	public static void sendPacket(Player p, Object packet) {
		Object connection = getField("playerConnection", getHandle(p));
		if(connection==null){
			Bukkit.broadcastMessage("Packet 发送错误,请联系作者@Zao_hon");
			return;
		}
		try {
			getMethod("sendPacket", connection.getClass(), new Class[] { getNMSClass("Packet") }).invoke(connection,
					packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static Object getHandle(Object o) {
		try {
			Method m = getMethod("getHandle", o.getClass(), new Class[0]);
			Object handle = m.invoke(o, new Object[0]);
			return handle;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object invokeMethod(String method, Object o, Object[] parameters) {

		try {
			Class<?>[] paraclazz = new Class<?>[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				paraclazz[i] = parameters[i].getClass();
			}

			Method m = getMethod(method, o.getClass(), paraclazz);
			return m.invoke(o, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Object invokeMethod(String method, Object o) {
		try {
			return o.getClass().getMethod(method, new Class[0]).invoke(o, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(String name, Class<?> clazz, Class<?>[] parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getField(String fieldname, Class<?> clazz) {
		try {
			Field field = clazz.getField(fieldname);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getField(String fieldname, Object o) {
		try {
			Field field = o.getClass().getField(fieldname);
			field.setAccessible(true);
			return field.get(o);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getNMSClass(String classname) {
		String fullname = "net.minecraft.server" + getVersion() + classname;
		try {
			Class<?> clazz = Class.forName(fullname);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getCraftClass(String classname) {
		String fullname = "org.bukkit.craftbukkit" + getVersion() + classname;

		Class<?> clazz;
		try {
			clazz = Class.forName(fullname);
			return clazz;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		version = version.substring(version.lastIndexOf(".")) + ".";
		return version;
	}
}
