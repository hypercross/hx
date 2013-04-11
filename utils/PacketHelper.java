package hx.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketHelper {

	public static Packet250CustomPayload toPacket(String channel, Object... data)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(3);
		DataOutputStream outputStream = new DataOutputStream(bos);

		try {
			for(Object obj : data)
			{
				serialize(obj, outputStream);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		return packet;
	}

	public static void fromPacket(Object obj, byte[] data)
	{
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));

		try{
			for(Field f : obj.getClass().getFields())
			{
				if(Modifier.isStatic(f.getModifiers()))continue;
				f.set(obj, deserialize(f.getType(), inputStream));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void serialize(Object obj, DataOutputStream dos) throws IOException
	{
		if(obj instanceof Integer)
			dos.writeInt((Integer) obj);
		else if(obj instanceof Byte)
			dos.writeByte((Byte)obj);
		else if(obj instanceof Float)
			dos.writeFloat((Float)obj);
		else if(obj instanceof Character)
			dos.writeChar((Character)obj);
		else if(obj.getClass().isArray())
		{
			if(obj.getClass().getComponentType().equals(int.class))
			{
				int[] objs = (int[])obj;
				dos.writeByte(objs.length);
				for(int aobj:objs)serialize(aobj, dos);
			}
			else if(obj.getClass().getComponentType().equals(byte.class))
			{
				byte[] objs = (byte[])obj;
				dos.writeByte(objs.length);
				for(byte aobj:objs)serialize(aobj, dos);
			}
			else if(obj.getClass().getComponentType().equals(float.class))
			{
				float[] objs = (float[])obj;
				dos.writeByte(objs.length);
				for(float aobj:objs)serialize(aobj, dos);
			}
			else if(obj.getClass().getComponentType().equals(char.class))
			{
				char[] objs = (char[])obj;
				dos.writeByte(objs.length);
				for(char aobj:objs)serialize(aobj, dos);
			}
		}else Debug.dafuq("fail to serialize !");
	}

	private static Object deserialize(Class<?> type, DataInputStream dis) throws IOException
	{
		if(type.equals(int.class))
			return dis.readInt();
		else if(type.equals(float.class))
			return dis.readFloat();
		else if(type.equals(byte.class))
			return dis.readByte();
		else if(type.equals(char.class))
			return dis.readChar();
		else if(type.isArray())
		{
			int len = dis.readByte();
			
			if(type.getComponentType().equals(int.class))
			{
				int[] objs = new int[len];
				for(int i = 0; i<len; i++)objs[i] = (Integer) deserialize(type.getComponentType(), dis);
				return objs;
			}
			else if(type.getComponentType().equals(float.class))
			{
				float[] objs = new float[len];
				for(int i = 0; i<len; i++)objs[i] = (Float) deserialize(type.getComponentType(), dis);
				return objs;
			}
			else if(type.getComponentType().equals(byte.class))
			{
				byte[] objs = new byte[len];
				for(int i = 0; i<len; i++)objs[i] = (Byte) deserialize(type.getComponentType(), dis);
				return objs;
			}
			else if(type.getComponentType().equals(char.class))
			{
				char[] objs = new char[len];
				for(int i = 0; i<len; i++)objs[i] = (Character) deserialize(type.getComponentType(), dis);
				return objs;
			}else Debug.dafuq("fail to deserialize!");
		}
		Debug.dafuq("fail to deserialize!");
		return null;
	}


}
