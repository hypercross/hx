package hx.spectator;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import hx.utils.Debug;
import hx.utils.IHandledPacket;

public class FileSegment implements IHandledPacket{
	public static String CHANNEL = "file_segment";

	public int file_id;

	public byte[] data;

	@Override
	public boolean isActive(String channel) {
		if(FMLCommonHandler.instance().getSide().isClient())return false;
		return channel.equals(CHANNEL);
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		FileReceiver fr = FileHeader.receivers.get(file_id);
		if(fr == null)return;
		fr.receive(data);
		
		if(fr.finished())
		{
			fr.save();
			FileHeader.receivers.remove(file_id);
		}

		
	}
}
