package hx.survivalist;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import hx.utils.Debug;
import hx.utils.IHandledPacket;

public class FactorUpdate implements IHandledPacket{

	public char[] name;
	public int value;
	public int saturation;
	public int reserve;
	
	@Override
	public boolean isActive(String channel) {
		return channel.equals("factor_update");
	}

	@Override
	public void onPacketReceived(INetworkManager manager, Player player) {
		PlayerFactor pf = PlayerStat.getStat((EntityPlayer) player).getFactor(new String(name));
		if(pf == null)Debug.dafuq("oops");
		else
		{
			pf.value = value;
			pf.saturation = saturation;
			pf.reserve = reserve;
		}
	}

}
