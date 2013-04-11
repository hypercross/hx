package hx.utils;

import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public interface IHandledPacket {

	public boolean isActive(String channel);
	
	public void onPacketReceived(INetworkManager manager, Player player);
}
