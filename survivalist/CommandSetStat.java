package hx.survivalist;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandSetStat extends CommandBase{

	private String[] stats = new String[] {"stamina", "sleep", "thirst", "health"};
	private String[] options = new String[] {"value", "saturation", "reserve"};
	
	@Override
	public String getCommandName() {
		return "setplayerstat";
	}

	public int getRequiredPermissionLevel()
	{
		return 2;
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "setplayerstat <username> <stat> value|saturation|reserve num";
	}
	
	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(astring.length != 4)throw new WrongUsageException(getCommandUsage(icommandsender));
		
		String user   = astring[0];
		String stat   = astring[1];
		String option = astring[2];
		String value  = astring[3];
		
		EntityPlayerMP epmp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(user);
		PlayerFactor pf = PlayerStat.getStat(epmp).getFactor(stat);
		
		if(option.equals("value"))pf.value = Integer.parseInt(value);
		else if(option.equals("saturation"))pf.saturation = Integer.parseInt(value);
		else if(option.equals("reserve"))pf.reserve = Integer.parseInt(value);
		else throw new WrongUsageException("Something is not right.");
		
		pf.checkAndUpdate(epmp);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		if(var2.length == 3)return getListOfStringsMatchingLastWord(var2, options);
		if(var2.length == 2)return getListOfStringsMatchingLastWord(var2, stats);
		if(var2.length == 1)return getListOfStringsMatchingLastWord(var2, this.getPlayers());
		return null;
	}
	
	protected String[] getPlayers()
	{
		return MinecraftServer.getServer().getAllUsernames();
	}
	
	public boolean isUsernameIndex(String[] astring, int i){
		return i == 0;
	}
}
