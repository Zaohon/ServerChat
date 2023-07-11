package cn.blockmc.Zao_hon.ServerChat.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class NMSUtils {
	
	public static void sendActionBar(Player player,String message,String nmsVersion) {
	    message = ChatColor.translateAlternateColorCodes('&', message);
	    try {
	      if (nmsVersion.contains("v1_17") || nmsVersion.contains("v1_18")||nmsVersion.contains("v1_19") ||nmsVersion.contains("v1_20")) {
	    	  player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(message));
	      } else if (nmsVersion.contains("v1_16")) {
	    	  preSendActionBar(player, message);
	      } else if (nmsVersion.equals("v1_12_R1") || nmsVersion.startsWith("v1_13") || nmsVersion.startsWith("v1_14_") || nmsVersion.startsWith("v1_15_")) {
	    	  legacyPreSendActionBar(player, message);
	      } else if (!nmsVersion.equalsIgnoreCase("v1_8_R1") && !nmsVersion.contains("v1_7_")) {
	        Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
	        Object p = c1.cast(player);
	        Class<?> c4 = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
	        Class<?> c5 = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
	        Class<?> c2 = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
	        Class<?> c3 = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
	        Object o = c2.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
	        Object ppoc = c4.getConstructor(new Class[] { c3, byte.class }).newInstance(new Object[] { o, Byte.valueOf((byte)2) });
	        Method getHandle = c1.getDeclaredMethod("getHandle", new Class[0]);
	        Object handle = getHandle.invoke(p, new Object[0]);
	        Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
	        Object playerConnection = fieldConnection.get(handle);
	        Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", new Class[] { c5 });
	        sendPacket.invoke(playerConnection, new Object[] { ppoc });
	      } else {
	        Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
	        Object p = c1.cast(player);
	        Class<?> c4 = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
	        Class<?> c5 = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
	        Class<?> c2 = Class.forName("net.minecraft.server." + nmsVersion + ".ChatSerializer");
	        Class<?> c3 = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
	        Method m3 = c2.getDeclaredMethod("a", new Class[] { String.class });
	        Object cbc = c3.cast(m3.invoke(c2, new Object[] { "{\"text\": \"" + message + "\"}" }));
	        Object ppoc = c4.getConstructor(new Class[] { c3, byte.class }).newInstance(new Object[] { cbc, Byte.valueOf((byte)2) });
	        Method getHandle = c1.getDeclaredMethod("getHandle", new Class[0]);
	        Object handle = getHandle.invoke(p, new Object[0]);
	        Field fieldConnection = handle.getClass().getDeclaredField("playerConnection");
	        Object playerConnection = fieldConnection.get(handle);
	        Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", new Class[] { c5 });
	        sendPacket.invoke(playerConnection, new Object[] { ppoc });
	      } 
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 
	  }
	
	public static void preSendActionBar(Player player,String message) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, NoSuchFieldException, ClassNotFoundException{
	        Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
	        Object chatMessageType = getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
	        Object packetPlayOutChat = getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"), Class.forName("java.util.UUID") }).newInstance(new Object[] { chatComponentText, chatMessageType, player.getUniqueId() });
	        Object getHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
	        Object playerConnection = getHandle.getClass().getField("playerConnection").get(getHandle);
	        playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packetPlayOutChat });
	}
	
	public static void legacyPreSendActionBar(Player player, String message) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException, InvocationTargetException, NoSuchMethodException {
	      Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
	      Object chatMessageType = getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
	      Object packetPlayOutChat = getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType") }).newInstance(new Object[] { chatComponentText, chatMessageType });
	      Object getHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
	      Object playerConnection = getHandle.getClass().getField("playerConnection").get(getHandle);
	      playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packetPlayOutChat });
	  }
	
	public static String printConstructors(Class<?> clazz) {
		String str = "";
		for(Constructor<?> c:clazz.getConstructors()) {
			for(Class<?> claz:c.getParameterTypes()) {
				str += claz.getName() + " ";
			}
			str+="/r";
		}
		return str;
	}
	
	
	
	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		version = version.substring(version.lastIndexOf(".")+1);
		return version;
	}
	public static Class<?> getCraftClass(String classname) {
		String fullname = "org.bukkit.craftbukkit." + getVersion() + "."+classname;

		Class<?> clazz;
		try {
			clazz = Class.forName(fullname);
			return clazz;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Class<?> getNMSClass(String classname) {
		String fullname = "net.minecraft.server." + getVersion() + "."+classname;
		try {
			Class<?> clazz = Class.forName(fullname);
			return clazz;
		} catch (ClassNotFoundException e) {
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
	
	public static void sendPacket(Player p, Object packet) {
		Object connection = getField("playerConnection", getHandle(p));
		if (connection == null) {
			Bukkit.broadcastMessage("Packet send error,contack @Zao_hon");
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


}
