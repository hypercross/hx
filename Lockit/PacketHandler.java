package hx.Lockit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		if(!packet.channel.equals("Lockit_Monument"))return;
		EntityPlayer ep = (EntityPlayer) player;
		if(ep == null)return;
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int fieldID, value,x,y,z;
		
		try {
			x		= inputStream.readInt();
			y		= inputStream.readInt();
			z		= inputStream.readInt();
			fieldID = inputStream.readByte();
			value	= inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		World w = ep.worldObj;
		TileEntityMonument tem = (TileEntityMonument) w.getBlockTileEntity(x,y,z);
		if(tem == null)return;
		
		switch(fieldID)
		{
		case 0:
			tem.state = tem.toState((byte) value);
			ModLockit.instance.monuments.reportAdd(x, y, z, w);
			break;
		case 1:
			byte n1 = -1;
			tem.setPermission( (byte) ((byte)value & n1), (value >> 8) != 0);
			break;
		case 2:
			tem.plotRange = (byte) value;
			break;
		case 3:
			tem.milestoneRange = (byte)  value;
			break;
		case 4:
			tem.landmarkRange = value;
			break;
		}
	}
	
	public static Packet250CustomPayload getPacket(int x,int y,int z,int fieldID,int value)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try
		{
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			dos.writeByte(fieldID);
			dos.writeInt(value);
		}
		catch (Exception var6)
		{
			var6.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "Lockit_Monument";
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        
        return packet;
	}

	private static String[] names= {"autoAssign","alwaysLock","new","animals","monsters","move","build","pvp"};
	
	public static int[] parsePacket(TileEntityMonument tem , String scribble)
	{
		String[] blah = scribble.split(":");
		if(blah.length != 2)return null;
		
		String name = blah[0].trim();
		int val;
		try
		{
			val= Integer.parseInt(blah[1].trim());
		}catch(Exception e)
		{
			return null;
		}
		if(name.equals("plot"))
		{
			val = Math.max(val, ModLockit.maxPlotRange);
			tem.plotRange = (byte) val;
			return new int[]{2, val };
		}else if(name.equals("milestone"))
		{
			val = Math.max(val, ModLockit.maxMilestoneRange);
			tem.milestoneRange = (byte) val;
			return new int[]{3, val };
		}else if(name.equals("landmark"))
		{
			val = Math.max(val, ModLockit.maxLandmarkRange);
			tem.landmarkRange = val;
			return new int[]{4, val };
		}else 
		{
			int i =0;
			int f =1;
			for(;i<7;i++)
			{
				if(names[i].equals(name))
				{
					tem.setPermission((byte)f, val != 0);
					return new int[]{ 1, f + (val<<8)}; 
				}
				f = (f << 1);
			}
			if(names[i].equals(name))
			{
				tem.setPermission((byte) -128, val != 0);
				return new int[]{ 1, f + (val<<8)};
			}
		}
		return null;
	}
}