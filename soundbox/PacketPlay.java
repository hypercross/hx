package hx.soundbox;

import hx.utils.Debug;
import hx.utils.IHandledPacket;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.Player;

public class PacketPlay implements IHandledPacket{

	public int x;
	public int y;
	public int z;
	
	@Override
	public boolean isActive(String channel) {
		return channel.equals("play");
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		EntityClientPlayerMP ecpmp = (EntityClientPlayerMP)player;
		
		TileEntity te = ecpmp.worldObj.getBlockTileEntity(x, y, z) ;
		if(te == null || !(te instanceof TileEntityTransmittedNote))
		{
			Debug.dafuq("oops");
			return;
		}
		TileEntityTransmittedNote note = (TileEntityTransmittedNote) te;
		note.tickCount = 1;
	}

}
