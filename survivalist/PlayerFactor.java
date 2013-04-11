package hx.survivalist;

import hx.utils.Debug;
import hx.utils.PacketHelper;
import hx.utils.Range;
import hx.utils.TickLooper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public abstract class PlayerFactor implements IFactor{

	public final String name;
	public int value;
	public final int max;
	public int saturation;
	public int reserve;
	public int rate;

	public int delta_value = 0, delta_saturation = 0;
	
	private int cachedValue = -1, cachedSaturation = -1, cachedReserve = -1;
	private TickLooper updateTick = new TickLooper(1);
	
	private Range rangeMax;
	private Range rangeMin;

	public PlayerFactor(String name, int max)
	{
		this.name = name;
		this.max  = max;
		this.rate = 1;
		this.onReset();
		
		rangeMax = new Range(max - max/20,Integer.MAX_VALUE);
		rangeMin = new Range(Integer.MIN_VALUE,max/20);
	}

	@Override
	public int getLevel() {
		return value;
	}

	@Override
	public int getSaturation() {
		return saturation;
	}

	@Override
	public int getMaxLevel() {
		return max;
	}

	public void checkAndUpdate(EntityPlayer ep)
	{
		boolean update = false;

		int val = value * 40 / max;
		if(cachedValue != val)
		{
			cachedValue = val;
			update = true;
		}

		val = Math.min(value,saturation) * 40 / Math.max(1, value);
		if(cachedSaturation != val)
		{
			cachedSaturation = val;
			update = true;
		}
		
		if(updateTick.tickWithHault())
		{
			val = reserve;
			if(cachedReserve != reserve)
			{
				cachedReserve = reserve;
				update = true;
				updateTick.hault(20);
			}
		}

		if(update)sendUpdate(ep);

	}

	protected void sendUpdate(EntityPlayer ep)
	{
		if(FMLCommonHandler.instance().getSide().isClient())
			PacketDispatcher.sendPacketToServer(
					PacketHelper.toPacket("factor_update", this.name.toCharArray(), value, saturation, reserve)
					);
		else
			PacketDispatcher.sendPacketToPlayer(
					PacketHelper.toPacket("factor_update", this.name.toCharArray(), value, saturation, reserve)
					,(Player) ep);
	}
	
	protected void reportForEffect(byte type)
	{
		PacketDispatcher.sendPacketToServer(
				PacketHelper.toPacket("effect_enforcing", type)
				);
	}

	public String toString()
	{
		if(!this.enabled())return name + "(off)";
		return name + ": " + value + ":" + saturation + "/" + max + " rsv: " + reserve + " dtv: " + delta_value + " dts: " + delta_saturation;
	}
	
	public boolean drained()
	{
		return rangeMin.isIn(value);
	}
	
	public boolean fulled()
	{
		return rangeMax.isIn(value);
	}
	
	public void onReset()
	{
		this.value = max;
		this.saturation = value/2;
		this.reserve = 0;
	}

	public abstract void onTick(EntityPlayer ep);
}
