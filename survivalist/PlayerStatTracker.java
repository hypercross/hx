package hx.survivalist;

import hx.utils.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerStatTracker implements IPlayerTracker{

	private File saveFile(EntityPlayer player)
	{
		File file = saveFolder(player);
		file.mkdirs();
		return new File(file,player.username + ".dat");
	}
	
	private File saveFolder(EntityPlayer player)
	{
		MinecraftServer server = MinecraftServer.getServer();
		if(server == null)return null;
		
		if(!server.isDedicatedServer())
			return new File(Minecraft.getMinecraftDir(), "saves/" + player.worldObj.getSaveHandler().getSaveDirectoryName()
		+ "/survivalist/");
		
		return server.getFile("/" + player.worldObj.getSaveHandler().getSaveDirectoryName()
		+ "/survivalist/");
	}
	
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		File saveFile = saveFile(player);
		
		NBTTagCompound nbt;
		try {
			if(!saveFile.exists())return;
			nbt = CompressedStreamTools.readCompressed(new FileInputStream(saveFile));
			PlayerStat.getStat(player).fromNBT(nbt);
			for(PlayerFactor pf : PlayerStat.getStat(player).factors())pf.checkAndUpdate(player);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {	
		File saveFile = saveFile(player);
		
		NBTTagCompound nbt = new NBTTagCompound();
		PlayerStat.getStat(player).toNBT(nbt);
		try {
			if(!saveFile.exists())saveFile.createNewFile();
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(saveFile));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		PlayerStat.removePlayer(player);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		for(PlayerFactor factor : PlayerStat.getStat(player).factors())factor.onReset();
	}

}
