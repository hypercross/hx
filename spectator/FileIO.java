package hx.spectator;

import hx.utils.Debug;
import hx.utils.PacketHelper;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import net.minecraft.network.packet.Packet250CustomPayload;

public class FileIO {

	public static Packet250CustomPayload[] toPackets(File file, int id, char[] file_name) throws IOException
	{
		int size = 100;
		
		byte[] bytes = Files.toByteArray(file);
		
		Packet250CustomPayload[] packets = new Packet250CustomPayload[bytes.length/size + 2]; 
		
		packets[0] = PacketHelper.toPacket(FileHeader.CHANNEL, id, bytes.length, file_name);
		
		for(int i = 0; i < bytes.length; i += size)
		{
			byte[] toSend = new byte[ Math.min(size, bytes.length - i)];
			System.arraycopy(bytes, i, toSend, 0, toSend.length);
			
			packets[1 + i/size] = PacketHelper.toPacket(FileSegment.CHANNEL, id, toSend);
		}
		
		return packets;
	}
	
	public static void saveTo(File file, byte[] data) throws IOException
	{
		Files.write(data, file);
	}
}
