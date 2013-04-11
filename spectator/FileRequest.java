package hx.spectator;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScreenShotHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import hx.utils.Debug;
import hx.utils.IHandledPacket;

public class FileRequest implements IHandledPacket{
	public static String CHANNEL = "file_request";

	public int file_id;

	@Override
	public boolean isActive(String channel) {
		return FMLCommonHandler.instance().getSide().isClient() && channel.equals(CHANNEL);
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		EntityClientPlayerMP ecpmp = (EntityClientPlayerMP)player; 

		String blah = ScreenShotHelper.saveScreenshot(getSaveFile(ecpmp.username), 
				Minecraft.getMinecraft().displayWidth,
				Minecraft.getMinecraft().displayHeight);

		//Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(blah);

		for(File f : getSaveFile(ecpmp.username).listFiles()[0].listFiles())
		{
			try{
				for(Packet250CustomPayload p : FileIO.toPackets(f, file_id, (ecpmp.username + "_" + f.getName()).toCharArray()))
					if(p != null)
					ecpmp.sendQueue.addToSendQueue(p);
				f.delete();
				break;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public File getSaveFile(String prefix) {
		File saveFolder = new File(Minecraft.getMinecraftDir().getPath() + "/spectator/" + prefix);
		saveFolder.mkdirs();
		return saveFolder;
	}
}
