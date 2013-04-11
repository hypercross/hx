package hx.spectator;

import hx.utils.Debug;
import hx.utils.PacketHelper;

import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSpectate extends CommandBase{

	@Override
	public String getCommandName() {
		return "spectate";
	}

	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "spectate <username>";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2.length != 1)throw new WrongUsageException("spectate <username>", new Object[0]);

		EntityPlayerMP epmp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		if(epmp == null)throw new PlayerNotFoundException();

		PacketDispatcher.sendPacketToPlayer(PacketHelper.toPacket("file_request", FileHeader.receivers.size()), (Player)epmp);
	}
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return var2.length == 1 ? getListOfStringsMatchingLastWord(var2, this.getPlayers()) : null;
	}

	protected String[] getPlayers()
	{
		return MinecraftServer.getServer().getAllUsernames();
	}

	public boolean isUsernameIndex(String[] astring, int i){
		return i == 0;
	}

}
