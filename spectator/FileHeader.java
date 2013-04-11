package hx.spectator;

import java.util.HashMap;

import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import hx.utils.Debug;
import hx.utils.IHandledPacket;

public class FileHeader implements IHandledPacket{
	static String CHANNEL = "file_header";
	static HashMap<Integer,FileReceiver> receivers = new HashMap<Integer,FileReceiver>();

	public int id;
	public int length;
	public char[] file_name;
	
	@Override
	public boolean isActive(String channel) {
		if(FMLCommonHandler.instance().getSide().isClient())return false;
		return channel.equals(CHANNEL);
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		receivers.put(id, new FileReceiver(length, file_name));
	}

}
