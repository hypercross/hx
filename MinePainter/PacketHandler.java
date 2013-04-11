package hx.MinePainter;

import hx.utils.Debug;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler{

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(!(player instanceof EntityPlayerMP))return;
		EntityPlayerMP ep = (EntityPlayerMP)player;
		
		int[] pos = new int[6];
		int mode = 0;
		int xpos = 0,ypos = 0,zpos = 0;
		int face = 0;
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			pos[0] = inputStream.readByte();
			pos[1] = inputStream.readByte();
			pos[2] = inputStream.readByte();
			pos[3] = inputStream.readByte();
			pos[4] = inputStream.readByte();
			pos[5] = inputStream.readByte();
			mode   = inputStream.readByte();
			face   = inputStream.readByte();
			xpos   = inputStream.readInt();
			ypos   = inputStream.readInt();
			zpos   = inputStream.readInt();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		World w = ep.worldObj;		
		BlockSculpture sculpture = (BlockSculpture) ModMinePainter.instance.block("Sculpture").block();
		int id_inhand = ep.getCurrentEquippedItem().getItemDamage() & 15;
		int meta_inhand = ep.getCurrentEquippedItem().getItemDamage() >> 4;
		int modCount = 0;	
		
		TileEntitySculpture tes = (TileEntitySculpture) w.getBlockTileEntity(xpos,ypos,zpos);

		if(tes == null && mode > 2)
		{
			tes = TileEntitySculpture.full;
			tes.xCoord = xpos;
			tes.yCoord = ypos;
			tes.zCoord = zpos;
			tes.worldObj = w;
			tes.blockMetadata = ep.getCurrentEquippedItem().getItemDamage() >> 4;
		}else if(tes == null)
		{
			int materialID  = w.getBlockId(xpos,ypos,zpos);
			int meta        = w.getBlockMetadata(xpos,ypos,zpos);
			
			BlockSculpture.createEmpty = false;
			
			for(int i =0;i<16;i++)
				if(sculpture.materialBlock(i).blockID == materialID)
				{
					w.setBlock(xpos,ypos,zpos, sculpture.blockID, i, 3);
					tes = (TileEntitySculpture)w.getBlockTileEntity(xpos,ypos,zpos);
					tes.blockMeta = meta;
					tes.needUpdate = true;
					break;
				}
		}
		
		if(pos != null)
		{	
			if(mode < 3)
				for(int x=pos[0];x<pos[3];x++)
					for(int y=pos[1];y<pos[4];y++)
						for(int z=pos[2];z<pos[5];z++)
						{
							if(tes.get(x,y,z))modCount++;
							tes.del(x, y, z);
						}

			else
				for(int x=pos[0];x<pos[3];x++)
					for(int y=pos[1];y<pos[4];y++)
						for(int z=pos[2];z<pos[5];z++)
						{
							if(tes.invalid(x, y, z))
							{
								int ox = tes.xCoord + Facing.offsetsXForSide[face];
								int oy = tes.yCoord + Facing.offsetsYForSide[face];
								int oz = tes.zCoord + Facing.offsetsZForSide[face];

								if(w.isAirBlock(ox, oy, oz))
								{
									BlockSculpture.createEmpty = true; 
									w.setBlock( ox,oy,oz, sculpture.blockID, id_inhand, 3);
									TileEntitySculpture another = (TileEntitySculpture) w.getBlockTileEntity(ox,oy,oz);
									another.clear();
									another.blockMeta = meta_inhand;
									another.needUpdate = true;
								}
								if(w.getBlockId(ox,oy,oz) != sculpture.blockID)continue;
								if(w.getBlockMetadata(ox, oy, oz)!= id_inhand)continue;

								TileEntitySculpture another = (TileEntitySculpture) w.getBlockTileEntity(ox,oy,oz);
								another.set(x , y , z );
								modCount++;
							}else if(id_inhand == tes.getBlockMetadata()){
								tes.set(x, y, z);
								modCount++;
							}
						}
		}

		if(mode < 3)
		{
			sculpture.dropAllScrap(w,tes.xCoord,tes.yCoord,tes.zCoord,modCount);			

			if(tes.isEmpty())
			{
				w.setBlock(tes.xCoord,tes.yCoord,tes.zCoord, 0,0, 3);
				w.setBlockTileEntity(tes.xCoord,tes.yCoord,tes.zCoord, null);
			}
		}else if(modCount > 0)
			ep.getCurrentEquippedItem().stackSize--;
	}
	
	public static Packet250CustomPayload sendPacket(int x,int y,int z,int... params)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
				for(int i : params)
				{
					outputStream.writeByte(i);
				}
			
		        outputStream.writeInt(x);
		        outputStream.writeInt(y);
		        outputStream.writeInt(z);
		} catch (Exception ex) {
		        ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "ModMinePainter";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}
}
