package hx.utils;

import java.util.ArrayList;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class PacketHandler implements IPacketHandler{

	protected ArrayList<IHandledPacket> packet_prototypes = new ArrayList<IHandledPacket>(); 
	
	public abstract void registerPackeTypes();
	
	public PacketHandler()
	{
		this.registerPackeTypes();
	}
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		for(IHandledPacket ihp : packet_prototypes)
			if(ihp.isActive(packet.channel))
			{
				try {
					PacketHelper.fromPacket(ihp, packet.data);
					ihp.onPacketReceived(manager, player);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
	}
}
